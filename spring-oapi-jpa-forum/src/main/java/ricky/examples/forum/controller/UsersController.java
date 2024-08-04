package ricky.examples.forum.controller;

import ricky.examples.forum.api.UsersApi;
import ricky.examples.forum.dto.*;
import ricky.examples.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsersController implements UsersApi {

  private final UserService userService;

  @Autowired
  public UsersController(UserService userService) {
    this.userService = userService;
  }

  @Override
  public ResponseEntity<UserDTO> createUser(UserCreateDTO userCreateDTO) {
    UserDTO createdUser = userService.createUser(userCreateDTO);
    return ResponseEntity.status(201).body(createdUser);
  }

  @Override
  public ResponseEntity<Void> deleteUser(Integer userId) {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<UserDTO> getUser(Integer userId) {
    UserDTO user = userService.getUserById(userId);
    return ResponseEntity.ok(user);
  }

  @Override
  public ResponseEntity<GetUsers200ResponseDTO> getUsers(Integer page, Integer size, String sort, String search) {
    GetUsers200ResponseDTO usersResponse = userService.getUsers(page, size, sort, search);
    return ResponseEntity.ok(usersResponse);
  }

  @Override
  public ResponseEntity<Void> updateUser(Integer userId, UserUpdateDTO userUpdateDTO) {
    userService.updateUser(userId, userUpdateDTO);
    return ResponseEntity.ok().build();
  }
}