package com.innowise.userservice.service.impl;

import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.UserService;
import com.innowise.userservice.specification.UserSpecification;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional
  public User create(User user) {
    if (user == null) {
      throw new IllegalArgumentException("user must not be null");
    }
    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public User getById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("user with id not found id=" + id));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<User> getAll(String name, String surname, Pageable pageable) {
    Specification<User> spec = Specification.where(UserSpecification.nameContainsIgnoreCase(name))
        .and(UserSpecification.surnameContainsIgnoreCase(surname));

    return userRepository.findAll(spec, pageable);
  }

  @Override
  @Transactional
  public User updateById(Long id, User updated) {
    User existing = getById(id);

    existing.setName(updated.getName());
    existing.setSurname(updated.getSurname());
    existing.setBirthDate(updated.getBirthDate());
    existing.setEmail(updated.getEmail());
    existing.setActive(updated.isActive());

    return userRepository.save(existing);
  }

  @Override
  @Transactional
  public void setActive(Long id, boolean active) {
    int updated = userRepository.updateActiveById(id, active);
    if (updated == 0) {
      throw new NoSuchElementException("user with id not found id=" + id);
    }
  }
}
