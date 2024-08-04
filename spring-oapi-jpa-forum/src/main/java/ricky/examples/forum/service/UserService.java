package ricky.examples.forum.service;

import ricky.examples.forum.dto.*;
import ricky.examples.forum.entity.UserEntity;
import ricky.examples.forum.mapper.UserMapper;
import ricky.examples.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Autowired
  public UserService(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  public UserDTO createUser(UserCreateDTO userCreateDTO) {
    UserEntity userEntity = userMapper.toEntity(userCreateDTO);
    userRepository.save(userEntity);
    return userMapper.toDTO(userEntity);
  }

  public void deleteUser(Integer userId) {
    userRepository.deleteById(userId);
  }

  public UserDTO getUserById(Integer userId) {
    UserEntity userEntity = userRepository.findById(userId).orElseThrow();
    return userMapper.toDTO(userEntity);
  }

  public GetUsers200ResponseDTO getUsers(Integer page, Integer size, String sort, String search) {
    Pageable pageable = PageRequest.of(page, size);
    Page<UserEntity> userPage = userRepository.findAll(pageable);
    List<UserDTO> userDTOs = userPage.getContent().stream()
        .map(userMapper::toDTO)
        .collect(Collectors.toList());

    GetUsers200ResponseDTO response = new GetUsers200ResponseDTO();
    response.setContent(userDTOs);
    response.setTotalPages(userPage.getTotalPages());
    response.setTotalElements((int) userPage.getTotalElements());
    response.setNumber(userPage.getNumber());
    response.setSize(userPage.getSize());
    response.setNumberOfElements(userPage.getNumberOfElements());
    response.setFirst(userPage.isFirst());
    response.setLast(userPage.isLast());
    response.setEmpty(userPage.isEmpty());
    // Set sort details if needed

    return response;
  }

  public void updateUser(Integer userId, UserUpdateDTO userUpdateDTO) {
    UserEntity userEntity = userRepository.findById(userId).orElseThrow();
    userMapper.updateEntityFromDTO(userUpdateDTO, userEntity);
    userRepository.save(userEntity);
  }
}