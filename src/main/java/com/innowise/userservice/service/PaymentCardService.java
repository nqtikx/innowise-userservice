package com.innowise.userservice.service;

import com.innowise.userservice.model.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentCardService {

  PaymentCard createForUser(Long userId, PaymentCard paymentCard) throws IllegalAccessException;
  PaymentCard getById(Long id);
  PaymentCard getByIdAndUserId(Long id, Long userId);
  Page<PaymentCard> getAll(Pageable pageable);
  Page<PaymentCard> getAllByUserId(Long userId, Pageable pageable);
  PaymentCard updateById(Long id, PaymentCard updated);
  void setActive(Long id, boolean active);

}
