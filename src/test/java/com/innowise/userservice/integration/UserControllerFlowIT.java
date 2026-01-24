package com.innowise.userservice.integration;

import com.innowise.userservice.model.dto.ApiErrorResponse;
import com.innowise.userservice.model.dto.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.UserCreateDto;
import com.innowise.userservice.model.dto.UserUpdateDto;
import com.innowise.userservice.model.dto.UserWithCardsResponseDto;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerFlowIT extends AbstractIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PaymentCardRepository paymentCardRepository;

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @AfterEach
  void clean() {
    stringRedisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    paymentCardRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void shouldCreateUserCreateCardGetWithCardsAndCacheKeyShouldAppear() {
    Long userId = createUser("max.try@mail.com");
    createCard(userId);

    ResponseEntity<UserWithCardsResponseDto> first = restTemplate.getForEntity(
        "/users/" + userId + "/with-cards",
        UserWithCardsResponseDto.class
    );

    Assertions.assertEquals(HttpStatus.OK, first.getStatusCode());
    Assertions.assertNotNull(first.getBody());
    Assertions.assertEquals(userId, first.getBody().getUser().getId());
    Assertions.assertEquals(1, first.getBody().getCards().size());

    Set<String> keys = stringRedisTemplate.keys("usersWithCards::*");
    Assertions.assertNotNull(keys);
    Assertions.assertTrue(keys.contains("usersWithCards::" + userId));
  }

  @Test
  void shouldEvictCacheOnUserUpdate() {
    Long userId = createUser("update@mail.com");
    createCard(userId);

    restTemplate.getForEntity("/users/" + userId + "/with-cards", UserWithCardsResponseDto.class);

    Assertions.assertTrue(Boolean.TRUE.equals(stringRedisTemplate.hasKey("usersWithCards::" + userId)));

    UserUpdateDto updateDto = new UserUpdateDto();
    updateDto.setName("Max");
    updateDto.setSurname("Updated");
    updateDto.setBirthDate(LocalDate.of(2002, 2, 2));
    updateDto.setEmail("update@mail.com");
    updateDto.setActive(true);

    ResponseEntity<Void> updateResponse = restTemplate.exchange(
        "/users/" + userId,
        HttpMethod.PUT,
        new HttpEntity<>(updateDto),
        Void.class
    );

    Assertions.assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
    Assertions.assertFalse(Boolean.TRUE.equals(stringRedisTemplate.hasKey("usersWithCards::" + userId)));
  }

  @Test
  void shouldEvictCacheOnUserDeleteAndCascadeCards() {
    Long userId = createUser("delete@mail.com");
    createCard(userId);

    restTemplate.getForEntity("/users/" + userId + "/with-cards", UserWithCardsResponseDto.class);
    Assertions.assertTrue(Boolean.TRUE.equals(stringRedisTemplate.hasKey("usersWithCards::" + userId)));

    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
        "/users/" + userId,
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        Void.class
    );

    Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    Assertions.assertFalse(Boolean.TRUE.equals(stringRedisTemplate.hasKey("usersWithCards::" + userId)));

    Assertions.assertEquals(0, userRepository.count());
    Assertions.assertEquals(0, paymentCardRepository.count());
  }

  @Test
  void shouldNotAllowMoreThan5Cards() {
    Long userId = createUser("limit@mail.com");

    for (int i = 0; i < 5; i++) {
      ResponseEntity<Void> resp = restTemplate.postForEntity(
          "/users/" + userId + "/cards",
          buildCardDto(),
          Void.class
      );
      Assertions.assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    }

    ResponseEntity<ApiErrorResponse> sixth = restTemplate.postForEntity(
        "/users/" + userId + "/cards",
        buildCardDto(),
        ApiErrorResponse.class
    );

    Assertions.assertEquals(HttpStatus.BAD_REQUEST, sixth.getStatusCode());
    Assertions.assertNotNull(sixth.getBody());
    Assertions.assertTrue(sixth.getBody().getMessage().contains("cannot have more than 5"));
  }

  private Long createUser(String email) {
    UserCreateDto dto = new UserCreateDto();
    dto.setName("Max");
    dto.setSurname("Try");
    dto.setBirthDate(LocalDate.of(2002, 2, 2));
    dto.setEmail(email);
    dto.setActive(true);

    ResponseEntity<Void> createResponse = restTemplate.postForEntity("/users", dto, Void.class);
    Assertions.assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

    Long id = userRepository.findAll().get(0).getId();
    Assertions.assertNotNull(id);
    return id;
  }

  private void createCard(Long userId) {
    ResponseEntity<Void> resp = restTemplate.postForEntity(
        "/users/" + userId + "/cards",
        buildCardDto(),
        Void.class
    );
    Assertions.assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    Assertions.assertEquals(1, paymentCardRepository.count());
  }

  private PaymentCardCreateDto buildCardDto() {
    PaymentCardCreateDto cardCreateDto = new PaymentCardCreateDto();
    cardCreateDto.setNumber("1234567890123456");
    cardCreateDto.setHolder("MAX TRY");
    cardCreateDto.setExpirationDate(LocalDate.of(2030, 12, 31));
    cardCreateDto.setActive(true);
    return cardCreateDto;
  }
}
