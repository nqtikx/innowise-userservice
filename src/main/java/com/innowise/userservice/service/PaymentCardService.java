package com.innowise.userservice.service;

import com.innowise.userservice.model.PaymentCard;

public interface PaymentCardService {

  PaymentCard createForUser(Long userId, PaymentCard paymentCard) throws IllegalAccessException;
  PaymentCard getByIdAndUserId(Long id, Long userId);

}
