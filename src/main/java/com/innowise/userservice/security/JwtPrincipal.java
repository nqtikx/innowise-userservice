package com.innowise.userservice.security;

import com.innowise.userservice.model.Role;

public class JwtPrincipal {

  private final Long userId;
  private final String email;
  private final Role role;

  public JwtPrincipal(Long userId, String email, Role role) {
    this.userId = userId;
    this.email = email;
    this.role = role;
  }

  public Long getUserId() {
    return userId;
  }

  public String getEmail() {
    return email;
  }

  public Role getRole() {
    return role;
  }
}