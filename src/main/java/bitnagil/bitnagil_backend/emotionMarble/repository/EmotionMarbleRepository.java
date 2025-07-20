package bitnagil.bitnagil_backend.emotionMarble.repository;

import bitnagil.bitnagil_backend.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmotionMarbleRepository extends JpaRepository<EmotionMarble, HistoryPk> {
}
