package bitnagil.bitnagil_backend.routine.service;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import bitnagil.common.entity.HistoryPk;
import bitnagil.bitnagil_backend.routine.domain.Routine;
import bitnagil.bitnagil_backend.routine.domain.SubRoutine;
import bitnagil.bitnagil_backend.routine.repository.RoutineRepository;
import bitnagil.bitnagil_backend.routine.repository.SubRoutineRepository;
import bitnagil.bitnagil_backend.routine.request.RegisterRoutineRequest;
import bitnagil.bitnagil_backend.user.domain.User;

class RoutineServiceTest {
    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private SubRoutineRepository subRoutineRepository;

    @InjectMocks
    private RoutineService routineService; // 테스트 대상 서비스 클래스

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("루틴 및 서브루틴 등록 - 성공 케이스")
    public void registerRoutine_Success() {

        // TODO 테스트코드에 대한 브랜치 생성 후 리팩터링 예정

        // //given
        // HistoryPk historyPk = new HistoryPk(UUID.randomUUID(), 1L);
        // User user = mock(User.class);
        // Routine routine = mock(Routine.class);
        // RegisterRoutineRequest registerRoutineRequest = mock(RegisterRoutineRequest.class);
        //
        // when(user.getUserPk()).thenReturn(historyPk);
        // when(routine.getRoutinePk()).thenReturn(historyPk);
        // when(registerRoutineRequest.getRoutineName()).thenReturn("Morning Routine");
        // when(registerRoutineRequest.getSubRoutineName()).thenReturn(List.of("손 씻기", "양치하기", "세수하기"));
        //
        // // when
        // routineService.registerRoutine(user, registerRoutineRequest);
        //
        // // then
        // verify(routineRepository).save(any(Routine.class));
        // verify(subRoutineRepository, times(3)).save(any(SubRoutine.class));
    }
}