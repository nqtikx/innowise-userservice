package com.innowise.userservice.service.impl;

import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.PaymentCardService;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentCardServiceImpl implements PaymentCardService {

  private static final int MAX_CARDS_PER_USER = 5;

  private final UserRepository userRepository;
  private final PaymentCardRepository paymentCardRepository;

  @Autowired
  public PaymentCardServiceImpl(UserRepository userRepository, PaymentCardRepository paymentCardRepository) {
    this.userRepository = userRepository;
    this.paymentCardRepository = paymentCardRepository;
  }

  @Override
  @Transactional
  public PaymentCard createForUser(Long userId, PaymentCard paymentCard)
      throws IllegalAccessException {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found. id=" + userId));

    long cardsCount = paymentCardRepository.countByUserId(userId);
    if (cardsCount >= MAX_CARDS_PER_USER) {
      throw new IllegalAccessException("User cannot have more than 5 payment cards. userId=" + userId);
    }

    user.addPaymentCard(paymentCard);
    userRepository.save(user);

    return paymentCard;
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentCard getByIdAndUserId(Long id, Long userId) {
    return paymentCardRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new NoSuchElementException("Payment card not found. id=" + id + ", userId=" + userId));
  }
}
