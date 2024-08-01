package ricky.examples.oidc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class JwtUser implements UserDetails {

  private UUID id;

  private String username;

  private String password;

  private String firstName;

  private String lastName;

  private String email;

  private boolean enabled;

  @SuppressWarnings("unused")
  private Collection<Role> authorities;

  private Set<String> roles = new HashSet<>();

  public JwtUser(User user) {
    this.username = user.getUsername();
    this.password = user.getPassword();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.enabled = user.getEnabled();
    this.roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonExpired() {
    return enabled;
  }

  @JsonIgnore
  @Override
  public boolean isAccountNonLocked() {
    return enabled;
  }

  @JsonIgnore
  @Override
  public boolean isCredentialsNonExpired() {
    return enabled;
  }

  @JsonIgnore
  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return AuthorityUtils.commaSeparatedStringToAuthorityList(
        getRoles().stream().map(r -> "ROLE_" + r).collect(Collectors.joining()));
  }
}
