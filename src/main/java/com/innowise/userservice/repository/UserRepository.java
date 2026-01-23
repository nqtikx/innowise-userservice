package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);

  @Modifying
  @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
  int updateActiveById(@Param("id") Long id, @Param("active") boolean active);

  @EntityGraph(attributePaths = "paymentCards")
  Optional<User> findWithPaymentCardsById(Long id);

}
