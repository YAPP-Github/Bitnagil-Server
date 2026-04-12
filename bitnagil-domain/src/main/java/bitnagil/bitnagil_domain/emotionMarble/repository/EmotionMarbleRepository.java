package bitnagil.bitnagil_domain.emotionMarble.repository;

import bitnagil.bitnagil_domain.emotionMarble.domain.EmotionMarble;
import bitnagil.bitnagil_domain.emotionMarble.domain.enums.EmotionMarbleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmotionMarbleRepository extends JpaRepository<EmotionMarble, Long> {
    EmotionMarble findByUserId(Long userId);

    EmotionMarble findByUserIdAndDateIs(Long userId, LocalDate now);

    boolean existsByUserIdAndDate(Long userId, LocalDate nowDate);

    @Query("SELECT COUNT(e) FROM EmotionMarble e WHERE e.date BETWEEN :start AND :end")
    long countByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(e) FROM EmotionMarble e WHERE e.date BETWEEN :start AND :end AND e.emotionMarbleType IN :types")
    long countByDateBetweenAndEmotionMarbleTypeIn(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end,
        @Param("types") List<EmotionMarbleType> types);
}
