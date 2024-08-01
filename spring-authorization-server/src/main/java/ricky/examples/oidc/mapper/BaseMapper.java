package ricky.examples.oidc.mapper;

import java.util.List;

/**
 * 기본 매퍼 인터페이스.
 *
 * @param <D> DTO(Data Transfer Object) 타입
 * @param <E> 엔티티(Entity) 타입
 */
public interface BaseMapper<D, E> {

  /**
   * 엔티티를 DTO로 변환합니다.
   *
   * @param e 엔티티 객체
   * @return 변환된 DTO 객체
   */
  D toDto(E e);

  /**
   * DTO를 엔티티로 변환합니다.
   *
   * @param d DTO 객체
   * @return 변환된 엔티티 객체
   */
  E toEntity(D d);

  /**
   * 엔티티 목록을 DTO 목록으로 변환합니다.
   *
   * @param entityList 엔티티 객체 목록
   * @return 변환된 DTO 객체 목록
   */
  List<D> toDtoList(List<E> entityList);

  /**
   * DTO 목록을 엔티티 목록으로 변환합니다.
   *
   * @param dtoList DTO 객체 목록
   * @return 변환된 엔티티 객체 목록
   */
  List<E> toEntityList(List<D> dtoList);
}
