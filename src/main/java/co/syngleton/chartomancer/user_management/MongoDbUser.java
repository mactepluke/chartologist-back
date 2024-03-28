package co.syngleton.chartomancer.user_management;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

import static co.syngleton.chartomancer.user_management.UserValidationConstants.EMAIL_PATTERN;

@Data
@Document(collection = "users")
@EqualsAndHashCode(of = "username")
class MongoDbUser implements User {
    @Id
    private String id;
    @Indexed()
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String hiddenPassword;
    private UserSettings settings;

    MongoDbUser(String username, String password, String hiddenPassword) {
        this.username = username;
        this.password = password;
        this.hiddenPassword = hiddenPassword;
        this.settings = UserSettings.builder().build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(Role.ROLE_USER);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getEmail()   {
        if (this.settings != null && this.settings.getEmail() != null) {
            return this.settings.getEmail();
        }
        if (this.username != null && this.username.matches(EMAIL_PATTERN)) {
            return this.username;
        }
        return "";
    }

    @Override
    public boolean hasEnableLightMode() {
        return this.settings.isEnableLightMode();
    }
}
