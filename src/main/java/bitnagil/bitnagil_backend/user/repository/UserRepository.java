package bitnagil.bitnagil_backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.user.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}