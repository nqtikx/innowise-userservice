package com.innowise.userservice.mapper;

import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.dto.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.PaymentCardResponseDto;
import com.innowise.userservice.model.dto.PaymentCardUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  PaymentCard toEntity(PaymentCardCreateDto dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  PaymentCard toEntity(PaymentCardUpdateDto dto);

  @Mapping(target = "userId", source = "user.id")
  PaymentCardResponseDto toResponseDto(PaymentCard paymentCard);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  void updateEntity(PaymentCardUpdateDto dto, @MappingTarget PaymentCard paymentCard);

}
