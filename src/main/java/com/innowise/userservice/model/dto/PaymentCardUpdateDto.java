package com.innowise.userservice.model.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class PaymentCardUpdateDto {

  @NotBlank
  @Size(min = 16, max = 16)
  @Pattern(regexp = "\\d{16}", message = "must contain exactly 16 digits")
  private String number;

  @NotBlank
  @Size(max = 100)
  private String holder;

  @NotNull
  @Future
  private LocalDate expirationDate;

  @NotNull
  private Boolean active;

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getHolder() {
    return holder;
  }

  public void setHolder(String holder) {
    this.holder = holder;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
}
