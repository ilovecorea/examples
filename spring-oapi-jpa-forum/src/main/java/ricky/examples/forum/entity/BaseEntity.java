package ricky.examples.forum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @Column(name = "created_by", nullable = false)
  Integer createdBy;

  @Column(name = "modified_by", nullable = false)
  Integer modifiedBy;

  @Column(name = "created_at", nullable = false, updatable = false)
  LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "updated_at", nullable = false)
  LocalDateTime updatedAt = LocalDateTime.now();

}