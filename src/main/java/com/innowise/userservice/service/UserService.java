package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.UserWithCardsResponseDto;
import com.innowise.userservice.model.entity.User;

public interface UserService {

  User create(User user);
  User getById(Long id);
  User updateById(Long id, User updated);
  void setActive(Long id, boolean active);
  User save(User user);
  UserWithCardsResponseDto getByIdWithCards(Long id);
  void deleteById(Long id);

}
