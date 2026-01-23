package com.innowise.userservice.controller;

import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserWithCardsResponseDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.model.dto.UserCreateDto;
import com.innowise.userservice.model.dto.UserResponseDto;
import com.innowise.userservice.model.dto.UserUpdateDto;
import com.innowise.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final UserMapper userMapper;

  @Autowired
  public UserController(UserService userService, UserMapper userMapper) {
    this.userService = userService;
    this.userMapper = userMapper;
  }

  @PostMapping("")
  public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto dto) {
    User user = userMapper.toEntity(dto);
    User created = userService.create(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponseDto(created));
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
    User user = userService.getById(id);
    return ResponseEntity.ok(userMapper.toResponseDto(user));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDto> updateById(
      @PathVariable Long id,
      @Valid @RequestBody UserUpdateDto dto
  ) {
    User existing = userService.getById(id);
    userMapper.updateEntity(dto, existing);
    User updated = userService.save(existing);

    return ResponseEntity.ok(userMapper.toResponseDto(updated));
  }

  @PatchMapping("/{id}/active")
  public ResponseEntity<Void> setActive(
      @PathVariable Long id,
      @RequestParam("active") boolean active
  ) {
    userService.setActive(id, active);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/with-cards")
  public ResponseEntity<UserWithCardsResponseDto> getByIdWithCards(@PathVariable Long id) {
    return ResponseEntity.ok(userService.getByIdWithCards(id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
