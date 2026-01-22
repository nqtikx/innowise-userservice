package com.innowise.userservice.controller;

import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.dto.PaymentCardCreateDto;
import com.innowise.userservice.model.dto.PaymentCardResponseDto;
import com.innowise.userservice.model.dto.PaymentCardUpdateDto;
import com.innowise.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/{userId}/cards")
public class PaymentCardController {

  private final PaymentCardService paymentCardService;
  private final PaymentCardMapper paymentCardMapper;

  @Autowired
  public PaymentCardController(PaymentCardService paymentCardService, PaymentCardMapper paymentCardMapper) {
    this.paymentCardService = paymentCardService;
    this.paymentCardMapper = paymentCardMapper;
  }

  @PostMapping("")
  public ResponseEntity<PaymentCardResponseDto> createForUser(
      @PathVariable Long userId,
      @Valid @RequestBody PaymentCardCreateDto dto
  ) {
    PaymentCard entity = paymentCardMapper.toEntity(dto);
    PaymentCard created = paymentCardService.createForUser(userId, entity);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(paymentCardMapper.toResponseDto(created));
  }

  @GetMapping("/{cardId}")
  public ResponseEntity<PaymentCardResponseDto> getById(
      @PathVariable Long userId,
      @PathVariable Long cardId
  ) {
    PaymentCard card = paymentCardService.getByIdAndUserId(cardId, userId);

    return ResponseEntity.ok(paymentCardMapper.toResponseDto(card));
  }

  @GetMapping("")
  public ResponseEntity<Page<PaymentCardResponseDto>> getAllByUserId(
      @PathVariable Long userId,
      Pageable pageable
  ) {
    Page<PaymentCardResponseDto> result = paymentCardService.getAllByUserId(userId, pageable)
        .map(paymentCardMapper::toResponseDto);

    return ResponseEntity.ok(result);
  }

  @PutMapping("/{cardId}")
  public ResponseEntity<PaymentCardResponseDto> updateById(
      @PathVariable Long userId,
      @PathVariable Long cardId,
      @Valid @RequestBody PaymentCardUpdateDto dto
  ) {
    PaymentCard existing = paymentCardService.getByIdAndUserId(cardId, userId);
    paymentCardMapper.updateEntity(dto, existing);

    PaymentCard updated = paymentCardService.save(existing);

    return ResponseEntity.ok(paymentCardMapper.toResponseDto(updated));
  }

  @PatchMapping("/{cardId}/active")
  public ResponseEntity<Void> setActive(
      @PathVariable Long userId,
      @PathVariable Long cardId,
      @RequestParam("active") boolean active
  ) {
    paymentCardService.setActive(cardId, userId, active);

    return ResponseEntity.noContent().build();
  }
}
