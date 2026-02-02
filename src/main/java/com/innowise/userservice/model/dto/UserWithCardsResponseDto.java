package com.innowise.userservice.model.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UserWithCardsResponseDto implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  private UserResponseDto user;
  private List<PaymentCardResponseDto> cards;

  public UserResponseDto getUser() {
    return user;
  }

  public void setUser(UserResponseDto user) {
    this.user = user;
  }

  public List<PaymentCardResponseDto> getCards() {
    return cards;
  }

  public void setCards(List<PaymentCardResponseDto> cards) {
    this.cards = cards;
  }
}