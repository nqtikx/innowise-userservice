package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.PaymentCard;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

  Page<PaymentCard> findAllByUserId(Long userId, Pageable pageable);
  Optional<PaymentCard> findByIdAndUserId(Long id, Long userId);
  long countByUserId(Long userId);

  @Modifying
  @Query("UPDATE PaymentCard p SET p.active = :active WHERE p.id = :id")
  int updateActiveById(@Param("id") Long id, @Param("active") boolean active);

  @Query(value = "SELECT * FROM payment_cards WHERE user_id = :userId ORDER BY id", nativeQuery = true)
  List<PaymentCard> findAllByUserIdNative(@Param("userId") Long userId);


}
