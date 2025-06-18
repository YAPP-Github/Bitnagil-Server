package bitnagil.bitnagil_backend.auth.apple.service;

import bitnagil.bitnagil_backend.auth.apple.domain.AppleIdTokenPayload;
import bitnagil.bitnagil_backend.auth.apple.domain.AppleProperties;
import bitnagil.bitnagil_backend.global.errorcode.ErrorCode;
import bitnagil.bitnagil_backend.global.exception.CustomException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleUserInfoService {
    private final AppleAuthClient appleAuthClient;

    private final AppleProperties appleProperties;

    // 클라이언트에게 받은 authorization code를 통해 APPLE ID 토큰을 가져오고, 해당 토큰의 페이로드를 디코딩하여 반환
    public AppleIdTokenPayload get(String authorizationCode) {

        String idToken = appleAuthClient.getIdToken(
                appleProperties.getClientId(),
                generateClientSecret(),
                appleProperties.getGrantType(),
                authorizationCode)
                .getIdToken();

        return TokenDecoder.decodePayload(idToken, AppleIdTokenPayload.class);
    }

    // Apple 인증을 위한 ClientSecret 생성
    private String generateClientSecret() {

        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);

        String clientSecret = Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, appleProperties.getKeyId())
                .setIssuer(appleProperties.getTeamId())
                .setAudience(appleProperties.getAudience())
                .setSubject(appleProperties.getClientId())
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .setIssuedAt(new Date())
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();

        return clientSecret;
    }

    // Apple 인증을 위한 PrivateKey 생성
    private PrivateKey getPrivateKey() {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(appleProperties.getPrivateKey());

            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);
            return converter.getPrivateKey(privateKeyInfo);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.PRIVATE_KEY_CONVERT_ERROR);
        }
    }
}
