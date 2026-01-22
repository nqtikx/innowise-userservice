package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessValidationException;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.model.PaymentCard;
import com.innowise.userservice.model.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.PaymentCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        .orElseThrow(() -> new EntityNotFoundException("user not found id=" + userId));

    long cardsCount = paymentCardRepository.countByUserId(userId);
    if (cardsCount >= MAX_CARDS_PER_USER) {
      throw new BusinessValidationException("user cannot have more than 5 payment cards userId=" + userId);
    }

    user.addPaymentCard(paymentCard);
    userRepository.save(user);

    return paymentCard;
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentCard getById(Long id) {
    return paymentCardRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("payment card not found id=" + id));
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentCard getByIdAndUserId(Long id, Long userId) {
    return paymentCardRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new EntityNotFoundException("payment card not found id=" + id + ", userId=" + userId));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaymentCard> getAll(Pageable pageable) {
    return paymentCardRepository.findAll(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaymentCard> getAllByUserId(Long userId, Pageable pageable) {
    return paymentCardRepository.findAllByUserId(userId, pageable);
  }

  @Override
  @Transactional
  public PaymentCard updateById(Long id, PaymentCard updated) {
    PaymentCard existing = getById(id);

    existing.setNumber(updated.getNumber());
    existing.setHolder(updated.getHolder());
    existing.setExpirationDate(updated.getExpirationDate());
    existing.setActive(updated.isActive());

    return paymentCardRepository.save(existing);
  }

  @Override
  @Transactional
  public void setActive(Long id, boolean active) {
    int updatedRows = paymentCardRepository.updateActiveById(id, active);
    if (updatedRows == 0) {
      throw new EntityNotFoundException("payment card not found id=" + id);
    }
  }
}
