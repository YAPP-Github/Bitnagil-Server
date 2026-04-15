package bitnagil.bitnagil_domain.kpi.event;

import bitnagil.bitnagil_domain.kpi.domain.MonthlyKpi;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MonthlyKpiSavedEvent {
    private final MonthlyKpi monthlyKpi;
}

