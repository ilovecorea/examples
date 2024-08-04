package ricky.examples.forum.mapper;

import org.mapstruct.MappingTarget;
import ricky.examples.forum.dto.UserDTO;
import ricky.examples.forum.dto.UserCreateDTO;
import ricky.examples.forum.dto.UserUpdateDTO;
import ricky.examples.forum.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  UserDTO toDTO(UserEntity userEntity);

  UserEntity toEntity(UserDTO userDTO);

  UserEntity toEntity(UserCreateDTO userCreateDTO);

  void updateEntityFromDTO(UserUpdateDTO userUpdateDTO, @MappingTarget UserEntity userEntity);
}