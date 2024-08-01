package ricky.examples.oidc.config;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ricky.examples.oidc.model.JwtUser;

@Configuration
@EnableJpaAuditing
public class AuditorAwareConfig {
  @Bean
  AuditorAware<UUID> auditorProvider() {
    return new AuditorAwareImpl();
  }
}

class AuditorAwareImpl implements AuditorAware<UUID> {
  @SuppressWarnings("null")
  @Override
  public Optional<UUID> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      // return Optional.empty();
      return Optional.of(Constants.SYSTEM_USER_UUID);
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof JwtUser) {
      JwtUser jwtUser = (JwtUser) principal;
      return Optional.of(jwtUser.getId());
    } else {
      return Optional.of(Constants.SYSTEM_USER_UUID);
    }
  }
}
