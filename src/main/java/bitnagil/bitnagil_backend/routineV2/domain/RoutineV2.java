package bitnagil.bitnagil_backend.routineV2.domain;

import java.time.LocalDate;
import java.util.List;

import bitnagil.bitnagil_backend.global.utils.BooleanListConverter;
import bitnagil.bitnagil_backend.global.utils.StringListConverter;
import bitnagil.bitnagil_backend.routineInfoV2.domain.RoutineInfoV2;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoutineV2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routineInfoId;

    @NotNull
    private LocalDate routineDate;

    @NotNull
    private Boolean routineCompleteYn;

    @NotNull
    @Convert(converter = StringListConverter.class)
    List<String> subRoutineNames;

    @NotNull
    @Convert(converter = BooleanListConverter.class)
    List<Boolean> subRoutineCompleteYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_info_id")
    private RoutineInfoV2 routineInfo;

    @Builder
    public RoutineV2(LocalDate routineDate, Boolean routineCompleteYn, List<String> subRoutineNames,
        List<Boolean> subRoutineCompleteYn, RoutineInfoV2 routineInfo) {
        this.routineDate = routineDate;
        this.routineCompleteYn = routineCompleteYn;
        this.subRoutineNames = subRoutineNames;
        this.subRoutineCompleteYn = subRoutineCompleteYn;
        this.routineInfo = routineInfo;
    }
}
