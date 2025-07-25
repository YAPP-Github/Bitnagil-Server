package bitnagil.bitnagil_backend.emotionMarble.repository;

import bitnagil.bitnagil_backend.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface EmotionMarbleRepository extends JpaRepository<EmotionMarble, HistoryPk> {
    EmotionMarble findByUserId(UUID id);

    EmotionMarble findByUserIdAndDateIs(UUID userId, LocalDate now);
}
