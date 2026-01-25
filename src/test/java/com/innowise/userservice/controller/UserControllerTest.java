package com.innowise.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserCreateDto;
import com.innowise.userservice.model.dto.UserResponseDto;
import com.innowise.userservice.model.dto.UserUpdateDto;
import com.innowise.userservice.model.dto.UserWithCardsResponseDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private UserMapper userMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void createShouldReturnCreated() throws Exception {
    UserCreateDto dto = new UserCreateDto();
    dto.setName("Cat");
    dto.setSurname("Dog");
    dto.setBirthDate(LocalDate.of(2000, 1, 1));
    dto.setEmail("test@mail.com");
    dto.setActive(true);

    User user = new User("Cat", "Dog", LocalDate.of(2025, 1, 1), "test@mail.com", true);
    ReflectionTestUtils.setField(user, "id", 1L);

    UserResponseDto responseDto = new UserResponseDto();
    responseDto.setId(1L);

    when(userMapper.toEntity(any(UserCreateDto.class))).thenReturn(user);
    when(userService.create(user)).thenReturn(user);
    when(userMapper.toResponseDto(user)).thenReturn(responseDto);

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  void getByIdShouldReturnUser() throws Exception {
    User user = new User("Cat", "Dog", LocalDate.of(2025, 1, 1), "test@mail.com", true);
    ReflectionTestUtils.setField(user, "id", 1L);

    UserResponseDto responseDto = new UserResponseDto();
    responseDto.setId(1L);

    when(userService.getById(1L)).thenReturn(user);
    when(userMapper.toResponseDto(user)).thenReturn(responseDto);

    mockMvc.perform(get("/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  void updateByIdShouldReturnUpdated() throws Exception {
    UserUpdateDto dto = new UserUpdateDto();
    dto.setName("newCat");
    dto.setSurname("newDog");
    dto.setBirthDate(LocalDate.of(2025, 2, 1));
    dto.setEmail("new@mail.com");
    dto.setActive(true);

    User user = new User("newCat", "newDog", LocalDate.of(2025, 2, 1), "new@mail.com", true);
    ReflectionTestUtils.setField(user, "id", 1L);

    UserResponseDto responseDto = new UserResponseDto();
    responseDto.setName("newCat");

    when(userService.getById(1L)).thenReturn(user);
    when(userService.save(user)).thenReturn(user);
    when(userMapper.toResponseDto(user)).thenReturn(responseDto);

    mockMvc.perform(put("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("newCat"));
  }

  @Test
  void setActiveShouldReturnNoContent() throws Exception {
    mockMvc.perform(patch("/users/1/active")
            .param("active", "false"))
        .andExpect(status().isNoContent());

    verify(userService).setActive(1L, false);
  }

  @Test
  void getByIdWithCardsShouldReturnDto() throws Exception {
    UserWithCardsResponseDto dto = new UserWithCardsResponseDto();
    dto.setCards(Collections.emptyList());

    when(userService.getByIdWithCards(1L)).thenReturn(dto);

    mockMvc.perform(get("/users/1/with-cards"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cards").isArray());
  }

  @Test
  void deleteShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/users/1"))
        .andExpect(status().isNoContent());

    verify(userService).deleteById(1L);
  }
}