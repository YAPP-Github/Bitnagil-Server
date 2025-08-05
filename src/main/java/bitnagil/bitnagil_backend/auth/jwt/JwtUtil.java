package bitnagil.bitnagil_backend.auth.jwt;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import bitnagil.bitnagil_backend.user.repository.UserRepository;
import bitnagil.bitnagil_backend.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtUtil {
    private final AuthRedisService authRedisService;

    @Value("${jwt.secret}")
    private String secretKey;

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = (long)(1000 * 60 * 30);            // 30분
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = (long)(1000 * 60 * 60 * 24 * 7);
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";// 7일

    private Key key;

    private final UserRepository userRepository;

    // jwt.secret을 사용해서 암호화 키값 생성
    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Token generateToken(Long userId) {
        Date now = new Date();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
            .setSubject(ACCESS_TOKEN_SUBJECT)
            .claim("userId", userId)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
            .setSubject(REFRESH_TOKEN_SUBJECT)
            .claim("userId", userId)
            .setExpiration(refreshTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact();

        authRedisService.saveRefreshToken(userId, refreshToken);

        return Token.builder()
            .accessToken(accessToken)
            .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
            .refreshToken(refreshToken)
            .build();
    }

    // Request Header 에서 토큰 정보를 꺼내오기
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public Authentication getAuthentication(String accessToken) {
        User user = findValidUserByRefreshTokenOrAccessToken(accessToken);

        return new UsernamePasswordAuthenticationToken(user, null, getAuthorities(user));
        // TODO 추후 회원탈퇴한 유저를 어떻게 관리하는지에 따라 추가 검증 필요
    }

    // RefreshToken 혹은 AccessToken으로 인증된 유효 User 조회
    public User findValidUserByRefreshTokenOrAccessToken(String token) {

        Long userId = Long.valueOf(parseClaims(token).get("userId", String.class));

        return userRepository
            .findByUserId(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

            return claims.getExpiration().after(new Date());
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new CustomException(ErrorCode.INVALID_JWT_SIGNATURE);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_JWT_TOKEN);
        }
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
        );
    }
}