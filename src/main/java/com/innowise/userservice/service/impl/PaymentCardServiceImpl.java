package com.innowise.userservice.service.impl;

import com.innowise.userservice.exception.BusinessValidationException;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.PaymentCardService;
import com.innowise.userservice.service.UsersWithCardsCacheService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentCardServiceImpl implements PaymentCardService {

  private static final String CARD_NOT_FOUND_MSG = "payment card not found id=";
  private static final int MAX_CARDS_PER_USER = 5;

  private final UsersWithCardsCacheService usersWithCardsCacheService;
  private final UserRepository userRepository;
  private final PaymentCardRepository paymentCardRepository;

  @Autowired
  public PaymentCardServiceImpl(UserRepository userRepository, PaymentCardRepository paymentCardRepository,
      UsersWithCardsCacheService usersWithCardsCacheService) {
    this.userRepository = userRepository;
    this.paymentCardRepository = paymentCardRepository;
    this.usersWithCardsCacheService = usersWithCardsCacheService;
  }

  @Override
  public PaymentCard createForUser(Long userId, PaymentCard paymentCard) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("user not found id=" + userId));

    long cardsCount = paymentCardRepository.countByUserId(userId);
    if (cardsCount >= MAX_CARDS_PER_USER) {
      throw new BusinessValidationException("user cannot have more than 5 payment cards userId=" + userId);
    }

    user.addPaymentCard(paymentCard);
    PaymentCard saved = paymentCardRepository.saveAndFlush(paymentCard);
    usersWithCardsCacheService.evict(userId);

    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentCard getById(Long id) {
    return paymentCardRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_MSG + id));
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentCard getByIdAndUserId(Long id, Long userId) {
    return paymentCardRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_MSG + id + ", userId=" + userId));
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
  public PaymentCard updateById(Long id, PaymentCard updated) {
    PaymentCard existing = paymentCardRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_MSG + id));
    Long userId = existing.getUser().getId();
    applyUpdates(existing, updated);

    PaymentCard saved = paymentCardRepository.save(existing);
    usersWithCardsCacheService.evict(userId);
    return saved;
  }

  @Override
  public PaymentCard updateById(Long id, Long userId, PaymentCard updated) {
    PaymentCard existing = paymentCardRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_MSG + id + ", userId=" + userId));
    applyUpdates(existing, updated);

    PaymentCard saved = paymentCardRepository.save(existing);
    usersWithCardsCacheService.evict(userId);
    return saved;
  }

  private void applyUpdates(PaymentCard target, PaymentCard source) {
    target.setNumber(source.getNumber());
    target.setHolder(source.getHolder());
    target.setExpirationDate(source.getExpirationDate());
    target.setActive(source.isActive());
  }

  @Override
  public PaymentCard save(PaymentCard paymentCard) {
    if (paymentCard == null) {
      throw new BusinessValidationException("payment card must not be null");
    }
    return paymentCardRepository.save(paymentCard);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentCard> getAllByUserId(Long userId) {
    return paymentCardRepository.findAllByUserIdNative(userId);
  }

  @Override
  public PaymentCard setActiveAndReturn(Long id, boolean active) {
    int updatedRows = paymentCardRepository.updateActiveById(id, active);
    if (updatedRows == 0) {
      throw new EntityNotFoundException(CARD_NOT_FOUND_MSG + id);
    }

    PaymentCard card = paymentCardRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_MSG + id));

    usersWithCardsCacheService.evict(card.getUser().getId());
    return card;
  }

  @Override
  public PaymentCard setActiveAndReturn(Long id, Long userId, boolean active) {
    int updatedRows = paymentCardRepository.updateActiveByIdAndUserId(id, userId, active);
    if (updatedRows == 0) {
      throw new EntityNotFoundException(CARD_NOT_FOUND_MSG + id + ", userId=" + userId);
    }
    usersWithCardsCacheService.evict(userId);
    return paymentCardRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new EntityNotFoundException(CARD_NOT_FOUND_MSG + id + ", userId=" + userId));
  }
}
