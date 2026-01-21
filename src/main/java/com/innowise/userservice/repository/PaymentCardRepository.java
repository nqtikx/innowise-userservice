package com.innowise.userservice.repository;

import com.innowise.userservice.model.PaymentCard;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

  Page<PaymentCard> findAllByUserId(Long userId, Pageable pageable);

  Optional<PaymentCard> findByIdAndUserId(Long id, Long userId);

  long countByUserId(Long userId);
}
