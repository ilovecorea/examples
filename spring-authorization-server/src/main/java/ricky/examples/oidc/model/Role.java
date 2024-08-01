package ricky.examples.oidc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

/** 역할 정보를 나타내는 클래스 */
@Entity
@Table(name = "role")
@Getter
@Setter
public class Role implements GrantedAuthority {
  /** 역할 ID */
  @Id
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  /** 역할 이름 */
  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Override
  public String getAuthority() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role role)) {
      return false;
    }
    return Objects.equals(getId(), role.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
