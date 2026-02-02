package com.innowise.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.model.dto.UserActivePatchDto;
import com.innowise.userservice.model.dto.UserCreateDto;
import com.innowise.userservice.model.dto.UserResponseDto;
import com.innowise.userservice.model.dto.UserUpdateDto;
import com.innowise.userservice.model.dto.UserWithCardsResponseDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.service.UserService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    User user = new User("Cat", "Dog", LocalDate.of(2000, 1, 1), "test@mail.com", true);
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
    User user = new User("Cat", "Dog", LocalDate.of(2000, 1, 1), "test@mail.com", true);
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

    User updatedEntity = new User("newCat", "newDog", LocalDate.of(2025, 2, 1), "new@mail.com", true);
    ReflectionTestUtils.setField(updatedEntity, "id", 1L);

    UserResponseDto responseDto = new UserResponseDto();
    responseDto.setId(1L);
    responseDto.setName("newCat");

    when(userMapper.toEntity(any(UserUpdateDto.class))).thenReturn(updatedEntity);
    when(userService.updateById(1L, updatedEntity)).thenReturn(updatedEntity);
    when(userMapper.toResponseDto(updatedEntity)).thenReturn(responseDto);

    mockMvc.perform(put("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("newCat"));
  }

  @Test
  void patchUserShouldReturnUpdated() throws Exception {
    UserActivePatchDto dto = new UserActivePatchDto();
    dto.setActive(false);

    User updated = new User("Cat", "Dog", LocalDate.of(2000, 1, 1), "test@mail.com", false);
    ReflectionTestUtils.setField(updated, "id", 1L);

    UserResponseDto responseDto = new UserResponseDto();
    responseDto.setId(1L);
    responseDto.setActive(false);

    when(userService.setActiveAndReturn(1L, false)).thenReturn(updated);
    when(userMapper.toResponseDto(updated)).thenReturn(responseDto);

    mockMvc.perform(patch("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.active").value(false));

    verify(userService).setActiveAndReturn(1L, false);
  }

  @Test
  void getByIdWithCardsShouldReturnDto() throws Exception {
    UserWithCardsResponseDto dto = new UserWithCardsResponseDto();
    dto.setUser(new UserResponseDto());
    dto.setCards(Collections.emptyList());

    when(userService.getByIdWithCards(1L)).thenReturn(dto);

    mockMvc.perform(get("/users/1")
            .param("expand", "cards"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.cards").isArray());
  }


  @Test
  void deleteShouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/users/1"))
        .andExpect(status().isNoContent());

    verify(userService).deleteById(1L);
  }

  @Test
  void getAllShouldReturnPage() throws Exception {
    User user = new User("Cat", "Dog", LocalDate.of(2002, 3, 1), "test@mail.com", true);
    ReflectionTestUtils.setField(user, "id", 1L);

    Page<User> page = new PageImpl<>(List.of(user));

    UserResponseDto responseDto = new UserResponseDto();
    responseDto.setId(1L);

    when(userService.getAll(any(), any(), any(Pageable.class))).thenReturn(page);
    when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

    mockMvc.perform(get("/users")
            .param("page", "0")
            .param("size", "10")
            .param("name", "Max")
            .param("surname", "Try"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].id").value(1L));
  }
}
