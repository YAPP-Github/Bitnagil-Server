package bitnagil.bitnagil_backend.appVersion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bitnagil.bitnagil_backend.appVersion.domain.AndroidAppVersion;

public interface AndroidAppVersionRepository extends JpaRepository<AndroidAppVersion, Long> {

    // major, minor가 가장 높은 AndroidAppVersion을 조회
    AndroidAppVersion findFirstByOrderByMajorDescMinorDesc();
}
