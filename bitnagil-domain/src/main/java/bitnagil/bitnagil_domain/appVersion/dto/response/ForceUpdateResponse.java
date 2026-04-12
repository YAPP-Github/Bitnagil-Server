package bitnagil.bitnagil_domain.appVersion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ForceUpdateResponse {
    private boolean forceUpdateYn;
}
