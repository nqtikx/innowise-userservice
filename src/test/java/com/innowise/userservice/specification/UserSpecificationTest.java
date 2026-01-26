package com.innowise.userservice.specification;

import com.innowise.userservice.model.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserSpecificationTest {

  @Test
  void nameContainsIgnoreCaseShouldReturnConjunctionWhenNameNull() {
    Specification<User> spec = UserSpecification.nameContainsIgnoreCase(null);
    verifyConjunction(spec);
  }

  @Test
  void nameContainsIgnoreCaseShouldReturnLikeWhenNameValid() {
    Root<User> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    Path<String> namePath = mock(Path.class);

    when(root.<String>get("name")).thenReturn(namePath);
    when(cb.lower(namePath)).thenReturn(namePath);
    when(cb.like(any(), anyString())).thenReturn(mock(Predicate.class));

    UserSpecification.nameContainsIgnoreCase("Max").toPredicate(root, query, cb);

    verify(cb).like(any(), eq("%max%"));
  }

  @Test
  void surnameContainsIgnoreCaseShouldReturnConjunctionWhenSurnameBlank() {
    Specification<User> spec = UserSpecification.surnameContainsIgnoreCase("   ");
    verifyConjunction(spec);
  }

  private void verifyConjunction(Specification<User> spec) {
    Root<User> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);

    spec.toPredicate(root, query, cb);

    verify(cb).conjunction();
    verify(cb, never()).like(any(), anyString());
  }
}