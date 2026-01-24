package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessValidationException;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.UsersWithCardsCacheService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

  @Mock
  private UsersWithCardsCacheService usersWithCardsCacheService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PaymentCardRepository paymentCardRepository;

  @InjectMocks
  private PaymentCardServiceImpl paymentCardService;

  @Test
  void createForUserShouldThrowWhenUserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    Assertions.assertThrows(EntityNotFoundException.class,
        () -> paymentCardService.createForUser(1L, new PaymentCard("1", "H", LocalDate.of(2030, 12, 31), true)));
  }

  @Test
  void createForUserShouldThrowWhenLimitReached() {
    User user = new User("Max", "Try", LocalDate.of(2002, 2, 2), "max@mail.com", true);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(paymentCardRepository.countByUserId(1L)).thenReturn(5L);

    Assertions.assertThrows(BusinessValidationException.class,
        () -> paymentCardService.createForUser(1L, new PaymentCard("1", "H", LocalDate.of(2030, 12, 31), true)));

    verify(paymentCardRepository, never()).saveAndFlush(any(PaymentCard.class));
    verify(usersWithCardsCacheService, never()).evict(any(Long.class));
  }

  @Test
  void createForUserShouldSaveAndEvictCache() {
    User user = new User("Max", "Try", LocalDate.of(2002, 2, 2), "max@mail.com", true);
    PaymentCard card = new PaymentCard("1", "H", LocalDate.of(2030, 12, 31), true);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(paymentCardRepository.countByUserId(1L)).thenReturn(0L);
    when(paymentCardRepository.saveAndFlush(any(PaymentCard.class))).thenReturn(card);

    PaymentCard saved = paymentCardService.createForUser(1L, card);

    Assertions.assertNotNull(saved);
    verify(paymentCardRepository, times(1)).saveAndFlush(card);
    verify(usersWithCardsCacheService, times(1)).evict(1L);
  }

  @Test
  void getByIdShouldThrowWhenNotFound() {
    when(paymentCardRepository.findById(10L)).thenReturn(Optional.empty());
    Assertions.assertThrows(EntityNotFoundException.class, () -> paymentCardService.getById(10L));
  }

  @Test
  void getByIdShouldReturnCard() {
    PaymentCard card = new PaymentCard("1", "H", LocalDate.of(2030, 12, 31), true);
    when(paymentCardRepository.findById(10L)).thenReturn(Optional.of(card));

    PaymentCard result = paymentCardService.getById(10L);

    Assertions.assertNotNull(result);
    verify(paymentCardRepository, times(1)).findById(10L);
  }

  @Test
  void getByIdAndUserIdShouldThrowWhenNotFound() {
    when(paymentCardRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());
    Assertions.assertThrows(EntityNotFoundException.class, () -> paymentCardService.getByIdAndUserId(10L, 1L));
  }

  @Test
  void getAllShouldDelegateToRepository() {
    Pageable pageable = Pageable.ofSize(10);
    PaymentCard card = new PaymentCard("1", "H", LocalDate.of(2030, 12, 31), true);
    Page<PaymentCard> page = new PageImpl<>(List.of(card), pageable, 1);

    when(paymentCardRepository.findAll(pageable)).thenReturn(page);

    Page<PaymentCard> result = paymentCardService.getAll(pageable);

    Assertions.assertEquals(1, result.getTotalElements());
    verify(paymentCardRepository, times(1)).findAll(pageable);
  }

  @Test
  void getAllByUserIdShouldDelegateToRepository() {
    Pageable pageable = Pageable.ofSize(10);
    PaymentCard card = new PaymentCard("1", "H", LocalDate.of(2030, 12, 31), true);
    Page<PaymentCard> page = new PageImpl<>(List.of(card), pageable, 1);

    when(paymentCardRepository.findAllByUserId(1L, pageable)).thenReturn(page);

    Page<PaymentCard> result = paymentCardService.getAllByUserId(1L, pageable);

    Assertions.assertEquals(1, result.getTotalElements());
    verify(paymentCardRepository, times(1)).findAllByUserId(1L, pageable);
  }

  @Test
  void updateByIdShouldUpdateAndEvictCacheByUserId() {
    User user = Mockito.mock(User.class);
    when(user.getId()).thenReturn(1L);
    PaymentCard existing = new PaymentCard("old", "old", LocalDate.of(2030, 12, 31), false);
    existing.setUser(user);

    PaymentCard updated = new PaymentCard("new", "new", LocalDate.of(2031, 1, 1), true);

    when(paymentCardRepository.findById(10L)).thenReturn(Optional.of(existing));
    when(paymentCardRepository.save(existing)).thenReturn(existing);

    PaymentCard result = paymentCardService.updateById(10L, updated);

    Assertions.assertNotNull(result);
    Assertions.assertEquals("new", existing.getNumber());
    Assertions.assertEquals("new", existing.getHolder());
    Assertions.assertEquals(LocalDate.of(2031, 1, 1), existing.getExpirationDate());
    Assertions.assertTrue(existing.isActive());

    verify(usersWithCardsCacheService, times(1)).evict(1L);
  }

  @Test
  void setActiveShouldChangeActiveAndEvictCache() {
    User user = Mockito.mock(User.class);
    when(user.getId()).thenReturn(1L);

    PaymentCard existing = new PaymentCard("1", "H", LocalDate.of(2030, 12, 31), false);
    existing.setUser(user);

    when(paymentCardRepository.findById(10L)).thenReturn(Optional.of(existing));
    when(paymentCardRepository.save(existing)).thenReturn(existing);

    paymentCardService.setActive(10L, true);

    Assertions.assertTrue(existing.isActive());
    verify(paymentCardRepository, times(1)).save(existing);
    verify(usersWithCardsCacheService, times(1)).evict(1L);
  }

  @Test
  void setActiveWithUserIdShouldThrowWhenNotFound() {
    when(paymentCardRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.empty());
    Assertions.assertThrows(EntityNotFoundException.class, () -> paymentCardService.setActive(10L, 1L, true));
  }

  @Test
  void setActiveWithUserIdShouldSaveAndEvictCache() {
    User user = new User("Max", "Try", LocalDate.of(2002, 2, 2), "max@mail.com", true);
    PaymentCard existing = new PaymentCard("1", "H", LocalDate.of(2030, 12, 31), false);
    existing.setUser(user);

    when(paymentCardRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(existing));
    when(paymentCardRepository.save(existing)).thenReturn(existing);

    paymentCardService.setActive(10L, 1L, true);

    Assertions.assertTrue(existing.isActive());
    verify(paymentCardRepository, times(1)).save(existing);
    verify(usersWithCardsCacheService, times(1)).evict(1L);
  }

  @Test
  void saveShouldThrowWhenNull() {
    Assertions.assertThrows(BusinessValidationException.class, () -> paymentCardService.save(null));
  }

  @Test
  void saveShouldCallRepositorySave() {
    PaymentCard card = new PaymentCard("1", "H", LocalDate.of(2030, 12, 31), true);
    when(paymentCardRepository.save(card)).thenReturn(card);

    PaymentCard saved = paymentCardService.save(card);

    Assertions.assertNotNull(saved);
    verify(paymentCardRepository, times(1)).save(card);
  }
}
