package bitnagil.bitnagil_backend.routine.domain;

import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE sub_routine SET deleted_at = CURRENT_TIMESTAMP WHERE sub_routine_id = ?")
@Where(clause = "deleted_at IS NULL")
public class SubRoutine extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subRoutineId; // 서브 루틴 ID

    @NotNull
    private String subRoutineName; // 서브 루틴 이름

    @NotNull
    private boolean subRoutineCompleteYn; // 서브 루틴 완료 여부

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;
}
