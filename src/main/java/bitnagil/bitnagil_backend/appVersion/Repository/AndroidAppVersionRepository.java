package bitnagil.bitnagil_backend.appVersion.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import bitnagil.bitnagil_backend.appVersion.domain.AndroidAppVersion;

public interface AndroidAppVersionRepository extends JpaRepository<AndroidAppVersion, Long> {

    @Query("SELECT a FROM AndroidAppVersion a ORDER BY a.major DESC, a.minor DESC")
    AndroidAppVersion findLatestVersion();
}
