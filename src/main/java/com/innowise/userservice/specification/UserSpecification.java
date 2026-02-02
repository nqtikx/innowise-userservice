package com.innowise.userservice.specification;

import com.innowise.userservice.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {

  private UserSpecification() {
  }

  public static Specification<User> nameContainsIgnoreCase(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isBlank()) {
        return cb.conjunction();
      }
      return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<User> surnameContainsIgnoreCase(String surname) {
    return (root, query, cb) -> {
      if (surname == null || surname.isBlank()) {
        return cb.conjunction();
      }
      return cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%");
    };
  }
}
