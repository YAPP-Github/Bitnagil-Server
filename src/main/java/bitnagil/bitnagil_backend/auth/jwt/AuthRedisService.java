package bitnagil.bitnagil_backend.auth.jwt;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthRedisService {
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    // 🔸 저장
    public void saveRefreshToken(Long userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
            .userId(String.valueOf(userId))
            .refreshToken(token)
            .build();
        refreshTokenRedisRepository.save(refreshToken);
    }

    // 🔸 조회 by userId
    public Optional<RefreshToken> getRefreshTokenByUserId(Long userId) {
        return refreshTokenRedisRepository.findById(String.valueOf(userId));
    }

    // 🔸 조회 by refreshToken
    public Optional<RefreshToken> getRefreshTokenByToken(String token) {
        return refreshTokenRedisRepository.findByRefreshToken(token);
    }

    // 🔸 삭제
    public void deleteRefreshToken(Long userId) {
        refreshTokenRedisRepository.deleteById(String.valueOf(userId));
    }
}
