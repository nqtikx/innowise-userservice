package com.innowise.userservice.mapper;

import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.model.dto.UserCreateDto;
import com.innowise.userservice.model.dto.UserResponseDto;
import com.innowise.userservice.model.dto.UserUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "paymentCards", ignore = true)
  User toEntity(UserCreateDto dto);

  UserResponseDto toResponseDto(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "paymentCards", ignore = true)
  void updateEntity(UserUpdateDto dto, @MappingTarget User user);
}
