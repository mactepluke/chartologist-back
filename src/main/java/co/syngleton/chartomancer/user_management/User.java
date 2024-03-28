package co.syngleton.chartomancer.user_management;

import org.springframework.security.core.userdetails.UserDetails;

public interface User extends UserDetails {
    String getEmail();

    boolean hasEnableLightMode();

    String getId();

    String getHiddenPassword();

    UserSettings getSettings();

    void setId(String id);

    void setUsername(String username);

    void setPassword(String password);

    void setSettings(UserSettings settings);
}
