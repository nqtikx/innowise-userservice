package com.innowise.userservice.service;

import com.innowise.userservice.model.entity.PaymentCard;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentCardService {

  PaymentCard createForUser(Long userId, PaymentCard paymentCard);
  PaymentCard getById(Long id);
  PaymentCard getByIdAndUserId(Long id, Long userId);
  Page<PaymentCard> getAll(Pageable pageable);
  Page<PaymentCard> getAllByUserId(Long userId, Pageable pageable);
  List<PaymentCard> getAllByUserId(Long userId);
  PaymentCard updateById(Long id, PaymentCard updated);
  PaymentCard save(PaymentCard paymentCard);
  PaymentCard updateById(Long id, Long userId, PaymentCard updated);
  PaymentCard setActiveAndReturn(Long id, boolean active);
  PaymentCard setActiveAndReturn(Long id, Long userId, boolean active);
}
