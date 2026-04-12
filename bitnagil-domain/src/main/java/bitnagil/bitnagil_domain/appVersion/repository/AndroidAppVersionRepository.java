package bitnagil.bitnagil_domain.appVersion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bitnagil.bitnagil_domain.appVersion.domain.AndroidAppVersion;

@Repository
public interface AndroidAppVersionRepository extends JpaRepository<AndroidAppVersion, Long> {

    Optional<AndroidAppVersion> findFirstByOrderByMajorDescMinorDesc();
}
