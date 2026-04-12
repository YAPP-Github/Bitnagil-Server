package bitnagil.bitnagil_domain.user.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bitnagil.bitnagil_domain.user.domain.User;
import bitnagil.bitnagil_domain.user.domain.enums.SocialType;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<User> findByUserId(Long userId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByCreatedAtBefore(LocalDateTime endExclusive);
}
