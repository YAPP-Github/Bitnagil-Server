package bitnagil.bitnagil_backend.routine.request;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * 루틴을 저장하는 메서드를 루틴을 등록할 때, 수정할 때 모두 사용되어 코드 중복을 줄이기 위해 만들어진 인터페이스입니다.
 * RegisterRoutjneRequest, UpdateRoutineRequest에서 공통 필드에 대해 get 메서드를 추상화하였습니다.
 * 이를 통해 RoutineService에서 saveRoutine 메서드 하나로 위의 두가지 요청 객체 모두에게 호환될 수 있게 만들 수 있습니다.
 */
public interface RoutineRequestBase {
    String getRoutineName();
    List<DayOfWeek> getRepeatDay();
    LocalTime getExecutionTime();
    List<String> getSubRoutineName();
}