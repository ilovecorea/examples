package ricky.examples.oidc.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditing {
  @CreatedBy
  @Column(name = "created_by", updatable = false)
  private UUID createdBy;

  @CreatedDate
  @Column(name = "created_on", updatable = false)
  private LocalDateTime createdOn;

  @LastModifiedBy
  @Column(name = "updated_by")
  private UUID updatedBy;

  @LastModifiedDate
  @Column(name = "updated_on")
  private LocalDateTime updatedOn;
}
