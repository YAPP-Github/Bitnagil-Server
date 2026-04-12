package bitnagil.bitnagil_backend.userOnboardingInfo.repository;

import bitnagil.bitnagil_domain.user.domain.User;
import bitnagil.bitnagil_backend.userOnboardingInfo.domain.UserOnboardingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOnboardingInfoRepository extends JpaRepository<UserOnboardingInfo, Long> {
    UserOnboardingInfo findByUser(User user);
}
