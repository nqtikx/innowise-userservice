package com.innowise.userservice.model.dto;

import java.util.List;

public class UserWithCardsResponseDto {

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