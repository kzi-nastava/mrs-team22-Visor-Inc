package inc.visor.voom_service.auth.user.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class VoomUserDetails implements UserDetails {

  private final User user;

  public VoomUserDetails(User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getUserRole().getPermissions();
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return switch (user.getUserStatus()) {
      case INACTIVE, PENDING, ACTIVE -> true;
      case SUSPENDED, NOTACTIVATED -> false;
    };
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return switch (user.getUserStatus()) {
      case ACTIVE -> true;
      case INACTIVE, PENDING, SUSPENDED, NOTACTIVATED -> false;
    };
  }
}
