package co.syngleton.chartomancer.user_management;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Document(collection = "users")
@EqualsAndHashCode(of = "username")
public class User implements UserDetails {
    @Id
    private String id;
    @Indexed()
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String hiddenPassword;
    private UserSettings settings;

    protected User(String username, String password, String hiddenPassword) {
        this.username = username;
        this.password = password;
        this.hiddenPassword = hiddenPassword;
        this.settings = UserSettings.builder().build();
    }

    public static User getNew(String username, String password, String hiddenPassword) {
        return new User(username, password, hiddenPassword);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "USER");
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
}
