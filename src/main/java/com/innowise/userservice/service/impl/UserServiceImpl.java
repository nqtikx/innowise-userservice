package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessValidationException;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserWithCardsResponseDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.UserService;
import com.innowise.userservice.specification.UserSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  private static final String USER_NOT_FOUND_MSG = "user not found id=";

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PaymentCardMapper paymentCardMapper;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
      PaymentCardMapper paymentCardMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.paymentCardMapper = paymentCardMapper;
  }

  @Override
  public User create(User user) {
    if (user == null) {
      throw new BusinessValidationException("user must not be null");
    }

    if (userRepository.existsByEmail(user.getEmail())) {
      throw new BusinessValidationException("user with this email already exists email=" + user.getEmail());
    }

    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public User getById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + id));
  }

  @Override
  @CacheEvict(cacheNames = "usersWithCards", key = "#id")
  public User updateById(Long id, User updated) {
    User existing = userRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + id));

    existing.setName(updated.getName());
    existing.setSurname(updated.getSurname());
    existing.setBirthDate(updated.getBirthDate());
    existing.setEmail(updated.getEmail());
    existing.setActive(updated.isActive());

    return userRepository.save(existing);
  }

  @Override
  @CacheEvict(cacheNames = "usersWithCards", key = "#user.id")
  public User save(User user) {
    if (user == null) {
      throw new BusinessValidationException("user must not be null");
    }
    return userRepository.save(user);
  }

  @Override
  @Cacheable(cacheNames = "usersWithCards", key = "#id")
  @Transactional(readOnly = true)
  public UserWithCardsResponseDto getByIdWithCards(Long id) {
    User user = userRepository.findWithPaymentCardsById(id)
        .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG + id));

    UserWithCardsResponseDto dto = new UserWithCardsResponseDto();
    dto.setUser(userMapper.toResponseDto(user));
    dto.setCards(
        user.getPaymentCards()
            .stream()
            .map(paymentCardMapper::toResponseDto)
            .toList()
    );

    return dto;
  }

  @Override
  @CacheEvict(cacheNames = "usersWithCards", key = "#id")
  public void deleteById(Long id) {
    if (!userRepository.existsById(id)) {
      throw new EntityNotFoundException(USER_NOT_FOUND_MSG + id);
    }
    userRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<User> getAll(String name, String surname, Pageable pageable) {
    Specification<User> specification = Specification.where(UserSpecification.nameContainsIgnoreCase(name))
        .and(UserSpecification.surnameContainsIgnoreCase(surname));

    return userRepository.findAll(specification, pageable);
  }

  @Override
  public User setActiveAndReturn(Long id, Boolean active) {
    int updatedRows = userRepository.updateActiveById(id, active);
    if (updatedRows == 0) {
      throw new EntityNotFoundException(USER_NOT_FOUND_MSG + id);
    }
    return getById(id);
  }
}
