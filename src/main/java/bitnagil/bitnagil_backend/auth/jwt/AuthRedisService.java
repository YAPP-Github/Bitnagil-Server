package bitnagil.bitnagil_backend.auth.jwt;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthRedisService {
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final StringRedisTemplate stringRedisTemplate;

    // 저장
    public void saveRefreshToken(Long userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
            .userId(String.valueOf(userId))  // 복합키를 문자열 ID로 저장
            .refreshToken(token)
            .build();

        refreshTokenRedisRepository.save(refreshToken);
    }

    // 조회 by 복합키
    public Optional<RefreshToken> getRefreshTokenByUserId(Long userId) {
        return refreshTokenRedisRepository.findById(String.valueOf(userId));
    }

    // 조회 by refreshToken
    public Optional<RefreshToken> getRefreshTokenByToken(String token) {
        return refreshTokenRedisRepository.findByRefreshToken(token);
    }

    // 삭제
    public void deleteRefreshToken(Long userId) {
        refreshTokenRedisRepository.deleteById(String.valueOf(userId));
    }

    // Access Token 블랙리스트 등록
    public void addAccessTokenToBlacklist(String accessToken, long expirationMillis) {
        String key = "blacklist:" + accessToken;
        // value는 의미 없는 값, 만료시간은 access token의 남은 유효기간(ms)
        stringRedisTemplate.opsForValue().set(key, "logout", expirationMillis, TimeUnit.MILLISECONDS);
    }

    // Access Token 블랙리스트 여부 확인
    public boolean isAccessTokenBlacklisted(String accessToken) {
        String key = "blacklist:" + accessToken;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }


}
