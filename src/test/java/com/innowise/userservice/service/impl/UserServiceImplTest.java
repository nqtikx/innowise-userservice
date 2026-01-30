package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessValidationException;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.PaymentCardResponseDto;
import com.innowise.userservice.model.dto.UserResponseDto;
import com.innowise.userservice.model.dto.UserWithCardsResponseDto;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PaymentCardMapper paymentCardMapper;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void createShouldThrowWhenUserNull() {
    Assertions.assertThrows(BusinessValidationException.class, () -> userService.create(null));
  }

  @Test
  void createShouldThrowWhenEmailExists() {
    User user = new User("Max", "Try", LocalDate.of(2002, 2, 2), "max@mail.com", true);
    when(userRepository.existsByEmail("max@mail.com")).thenReturn(true);

    Assertions.assertThrows(BusinessValidationException.class, () -> userService.create(user));
    verify(userRepository, times(1)).existsByEmail("max@mail.com");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void createShouldSaveWhenValid() {
    User user = new User("Max", "Try", LocalDate.of(2002, 2, 2), "max@mail.com", true);
    when(userRepository.existsByEmail("max@mail.com")).thenReturn(false);
    when(userRepository.save(user)).thenReturn(user);

    User saved = userService.create(user);

    Assertions.assertNotNull(saved);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void getByIdShouldThrowWhenNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getById(1L));
  }

  @Test
  void getByIdShouldReturnUser() {
    User user = new User("Max", "Try", LocalDate.of(2002, 2, 2), "max@mail.com", true);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    User result = userService.getById(1L);

    Assertions.assertNotNull(result);
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void updateByIdShouldThrowWhenUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    User userToUpdate = new User("A", "B", LocalDate.of(2000, 1, 1), "a@b.com", true);

    Assertions.assertThrows(EntityNotFoundException.class,
        () -> userService.updateById(1L, userToUpdate));
  }

  @Test
  void updateByIdShouldSaveUpdatedFields() {
    User existing = new User("Old", "Name", LocalDate.of(2000, 1, 1), "old@mail.com", false);
    User updated = new User("New", "Surname", LocalDate.of(2002, 2, 2), "new@mail.com", true);

    when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(userRepository.save(any(User.class))).thenReturn(existing);

    User result = userService.updateById(1L, updated);

    Assertions.assertNotNull(result);

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository, times(1)).save(captor.capture());

    User saved = captor.getValue();
    Assertions.assertEquals("New", saved.getName());
    Assertions.assertEquals("Surname", saved.getSurname());
    Assertions.assertEquals(LocalDate.of(2002, 2, 2), saved.getBirthDate());
    Assertions.assertEquals("new@mail.com", saved.getEmail());
    Assertions.assertTrue(saved.isActive());
  }

  @Test
  void setActiveAndReturnShouldThrowWhenNoRowsUpdated() {
    when(userRepository.updateActiveById(1L, true)).thenReturn(0);

    Assertions.assertThrows(EntityNotFoundException.class,
        () -> userService.setActiveAndReturn(1L, true));

    verify(userRepository, never()).findById(1L);
  }

  @Test
  void setActiveAndReturnShouldReturnUserWhenUpdated() {
    User user = new User("Max", "Try", LocalDate.of(2002, 2, 2), "max@mail.com", true);

    when(userRepository.updateActiveById(1L, true)).thenReturn(1);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    User result = userService.setActiveAndReturn(1L, true);

    Assertions.assertNotNull(result);
    verify(userRepository, times(1)).updateActiveById(1L, true);
    verify(userRepository, times(1)).findById(1L);
  }

  @Test
  void saveShouldThrowWhenUserNull() {
    Assertions.assertThrows(BusinessValidationException.class, () -> userService.save(null));
  }

  @Test
  void saveShouldCallRepositorySave() {
    User user = new User("Max", "Try", LocalDate.of(2002, 2, 2), "max@mail.com", true);
    when(userRepository.save(user)).thenReturn(user);

    User saved = userService.save(user);

    Assertions.assertNotNull(saved);
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void getByIdWithCardsShouldThrowWhenNotFound() {
    when(userRepository.findWithPaymentCardsById(1L)).thenReturn(Optional.empty());
    Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getByIdWithCards(1L));
  }

  @Test
  void getByIdWithCardsShouldReturnDto() {
    User user = new User("Max", "Try", LocalDate.of(2002, 2, 2), "max@mail.com", true);
    PaymentCard card = new PaymentCard("1234567890123456", "MAX TRY", LocalDate.of(2030, 12, 31), true);
    user.addPaymentCard(card);

    when(userRepository.findWithPaymentCardsById(1L)).thenReturn(Optional.of(user));

    UserResponseDto userResponseDto = new UserResponseDto();
    userResponseDto.setId(1L);
    when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

    PaymentCardResponseDto cardResponseDto = new PaymentCardResponseDto();
    when(paymentCardMapper.toResponseDto(card)).thenReturn(cardResponseDto);

    UserWithCardsResponseDto dto = userService.getByIdWithCards(1L);

    Assertions.assertNotNull(dto);
    Assertions.assertNotNull(dto.getUser());
    Assertions.assertNotNull(dto.getCards());
    Assertions.assertEquals(1, dto.getCards().size());

    verify(userRepository, times(1)).findWithPaymentCardsById(1L);
    verify(userMapper, times(1)).toResponseDto(user);
    verify(paymentCardMapper, times(1)).toResponseDto(card);
  }

  @Test
  void deleteByIdShouldThrowWhenNotExists() {
    when(userRepository.existsById(1L)).thenReturn(false);
    Assertions.assertThrows(EntityNotFoundException.class, () -> userService.deleteById(1L));
  }

  @Test
  void deleteByIdShouldDeleteWhenExists() {
    when(userRepository.existsById(1L)).thenReturn(true);

    userService.deleteById(1L);

    verify(userRepository, times(1)).deleteById(1L);
  }

  @Test
  void getAllShouldCallRepositoryFindAllWithSpecificationAndPageable() {
    Pageable pageable = Pageable.ofSize(10);
    Page<User> expected = new PageImpl<>(List.of(), pageable, 0);

    when(userRepository.findAll(Mockito.<Specification<User>>any(), eq(pageable)))
        .thenReturn(expected);

    Page<User> result = userService.getAll("Max", "Try", pageable);

    Assertions.assertNotNull(result);
    verify(userRepository, times(1))
        .findAll(Mockito.<Specification<User>>any(), eq(pageable));
  }
}
