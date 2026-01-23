package com.innowise.userservice.model.entity;

import com.innowise.userservice.model.entity.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
public class PaymentCard extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "number", nullable = false, length = 18)
  private String number;

  @Column(name = "holder", nullable = false, length = 100)
  private String holder;

  @Column(name = "expiration_date", nullable = false)
  private LocalDate expirationDate;

  @Column(name = "active", nullable = false)
  private boolean active;

  protected PaymentCard() {
  }

  public PaymentCard(String number, String holder, LocalDate expirationDate, boolean active) {
    this.number = number;
    this.holder = holder;
    this.expirationDate = expirationDate;
    this.active = active;
  }

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getNumber() {
    return number;
  }

  public String getHolder() {
    return holder;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public boolean isActive() {
    return active;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public void setHolder(String holder) {
    this.holder = holder;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
