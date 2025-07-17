package bitnagil.bitnagil_backend.routine.request;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SubRoutineInfo {
    private UUID subRoutineId;
    private String subRoutineName;
    private Integer sortOrder;
}
