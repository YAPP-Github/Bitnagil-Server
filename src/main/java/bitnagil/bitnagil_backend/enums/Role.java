package bitnagil.bitnagil_backend.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role implements EnumType {

    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    WITHDRAWN("ROLE_WITHDRAWN");

    private final String description;

    @Override
    public String getDescription() {
        return this.description;
    }
}
