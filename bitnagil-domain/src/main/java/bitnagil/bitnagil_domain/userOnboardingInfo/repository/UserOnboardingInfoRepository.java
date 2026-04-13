package bitnagil.bitnagil_domain.userOnboardingInfo.repository;


import bitnagil.bitnagil_domain.user.domain.User;
import bitnagil.bitnagil_domain.userOnboardingInfo.domain.UserOnboardingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOnboardingInfoRepository extends JpaRepository<UserOnboardingInfo, Long> {
    UserOnboardingInfo findByUser(User user);
}
