package bitnagil.bitnagil_backend.user.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bitnagil.bitnagil_backend.enums.SocialType;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import bitnagil.bitnagil_backend.user.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, HistoryPk> {

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    // socialType, socialId 기반으로 유저 이력들 특정 후, 이력 시작일시 및 종료일시를 활용해서 현재시간 기준으로 유효한 유저 식별
    Optional<User> findBySocialTypeAndSocialIdAndHistoryStartDateTimeLessThanAndHistoryEndDateTimeGreaterThanEqual(
        SocialType socialType, String socialId, LocalDateTime historyStartDateBound, LocalDateTime historyEndDateBound);

    Optional<User> findByUserPk(HistoryPk userPk);
}