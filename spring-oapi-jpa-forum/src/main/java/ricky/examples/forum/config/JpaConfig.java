package ricky.examples.forum.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfig {

  @PersistenceContext
  private EntityManager entityManager;

  @Bean
  public JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(entityManager);
  }

  @Bean("auditorProvider")
  public AuditorAware<String> auditorProvider() {
    return new AuditorAwareImpl();
  }

  class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
      Optional<String> optionalString = Optional.ofNullable(SecurityContextHolder.getContext())
          .map(e -> e.getAuthentication())
          .filter(Authentication::isAuthenticated)
          .map(Authentication::getPrincipal)
          .filter(principal -> principal instanceof UserDetails)
          .map(principal -> ((UserDetails) principal).getUsername());
      log.debug("## Login username: {}", optionalString);

      return optionalString;

    }
  }

}
