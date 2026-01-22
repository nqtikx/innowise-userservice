package com.innowise.userservice.model.entity;

import com.innowise.userservice.exception.BusinessValidationException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 25)
  private String name;

  @Column(name = "surname", nullable = false, length = 25)
  private String surname;

  @Column(name = "birth_date", nullable = false)
  private LocalDate birthDate;

  @Column(name = "email", nullable = false, length = 70)
  private String email;

  @Column(name = "active", nullable = false)
  private boolean active;

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY
  )
  private List<PaymentCard> paymentCards = new ArrayList<>();

  protected User() {
  }

  public User(String name, String surname, LocalDate birthDate, String email, boolean active) {
    this.name = name;
    this.surname = surname;
    this.birthDate = birthDate;
    this.email = email;
    this.active = active;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public String getEmail() {
    return email;
  }

  public boolean isActive() {
    return active;
  }

  public List<PaymentCard> getPaymentCards() {
    return List.copyOf(paymentCards);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void addPaymentCard(PaymentCard card) {
    if (card == null) {
      throw new BusinessValidationException("payment card must not be null");
    }
    if (paymentCards.contains(card)) {
      return;
    }
    paymentCards.add(card);
    card.setUser(this);
  }

  public void removePaymentCard(PaymentCard card) {
    paymentCards.remove(card);
    card.setUser(null);
  }
}
