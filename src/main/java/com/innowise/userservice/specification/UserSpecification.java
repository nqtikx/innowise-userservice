package com.innowise.userservice.specification;

import com.innowise.userservice.model.User;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {

  private UserSpecification() {
  }

  public static Specification<User> nameContainsIgnoreCase(String name) {
    if (name == null || name.isBlank()) {
      return Specification.where(null);
    }

    String patternLike = "%" + name.trim().toLowerCase() + "%";
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), patternLike);
  }

  public static Specification<User> surnameContainsIgnoreCase(String surname) {
    if (surname == null || surname.isBlank()) {
      return Specification.where(null);
    }

    String patternLike = "%" + surname.trim().toLowerCase() + "%";
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.like(criteriaBuilder.lower(root.get("surname")), patternLike);
  }
}
