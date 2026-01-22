package com.innowise.userservice.service;

import com.innowise.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  User create(User user);
  User getById(Long id);
  Page<User> getAll(String name, String surname, Pageable pageable);
  User updateById(Long id, User updated);
  void setActive(Long id, boolean active);

}
