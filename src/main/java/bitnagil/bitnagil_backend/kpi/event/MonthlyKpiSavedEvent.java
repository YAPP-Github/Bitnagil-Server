package bitnagil.bitnagil_backend.kpi.event;

import bitnagil.bitnagil_backend.kpi.domain.MonthlyKpi;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MonthlyKpiSavedEvent {
    private final MonthlyKpi monthlyKpi;
}
