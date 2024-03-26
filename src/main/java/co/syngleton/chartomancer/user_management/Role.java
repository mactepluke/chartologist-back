package co.syngleton.chartomancer.user_management;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return this.name();
    }

    public static Role fromString(String role) {
        return Role.valueOf(role);
    }
}
