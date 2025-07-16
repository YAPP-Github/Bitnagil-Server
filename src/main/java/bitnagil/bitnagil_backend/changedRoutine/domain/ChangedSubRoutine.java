package bitnagil.bitnagil_backend.changedRoutine.domain;

import bitnagil.bitnagil_backend.global.entity.BaseTimeEntity;
import bitnagil.bitnagil_backend.global.entity.HistoryPk;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChangedSubRoutine extends BaseTimeEntity {

    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "changed_sub_routine_id")),
        @AttributeOverride(name = "historySeq", column = @Column(name = "history_seq"))
    })
    private HistoryPk changedSubRoutinePk;

    @NotNull
    private String changedSubRoutineName;

    @NotNull
    private LocalDateTime historyStartDateTime;

    @NotNull
    private LocalDateTime historyEndDateTime;


    @Builder
    public ChangedSubRoutine(HistoryPk changedSubRoutinePk, String changedSubRoutineName,
        LocalDateTime historyStartDateTime, LocalDateTime historyEndDateTime) {
        this.changedSubRoutinePk = changedSubRoutinePk;
        this.changedSubRoutineName = changedSubRoutineName;
        this.historyStartDateTime = historyStartDateTime;
        this.historyEndDateTime = historyEndDateTime;
    }
}
