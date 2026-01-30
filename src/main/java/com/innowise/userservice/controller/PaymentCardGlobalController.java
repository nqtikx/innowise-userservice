package com.innowise.userservice.controller;

import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.model.dto.PaymentCardActivePatchDto;
import com.innowise.userservice.model.entity.PaymentCard;
import com.innowise.userservice.model.dto.PaymentCardResponseDto;
import com.innowise.userservice.model.dto.PaymentCardUpdateDto;
import com.innowise.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cards")
public class PaymentCardGlobalController {

  private final PaymentCardService paymentCardService;
  private final PaymentCardMapper paymentCardMapper;

  @Autowired
  public PaymentCardGlobalController(PaymentCardService paymentCardService, PaymentCardMapper paymentCardMapper) {
    this.paymentCardService = paymentCardService;
    this.paymentCardMapper = paymentCardMapper;
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentCardResponseDto> getById(@PathVariable Long id) {
    PaymentCard card = paymentCardService.getById(id);
    return ResponseEntity.ok(paymentCardMapper.toResponseDto(card));
  }

  @GetMapping("")
  public ResponseEntity<Page<PaymentCardResponseDto>> getAll(Pageable pageable) {
    Page<PaymentCardResponseDto> result = paymentCardService.getAll(pageable)
        .map(paymentCardMapper::toResponseDto);

    return ResponseEntity.ok(result);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PaymentCardResponseDto> updateById(
      @PathVariable Long id,
      @Valid @RequestBody PaymentCardUpdateDto dto
  ) {
    PaymentCard entity = paymentCardMapper.toEntity(dto);
    PaymentCard updated = paymentCardService.updateById(id, entity);
    return ResponseEntity.ok(paymentCardMapper.toResponseDto(updated));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<PaymentCardResponseDto> patchCard(
      @PathVariable Long id,
      @Valid @RequestBody PaymentCardActivePatchDto dto
  ) {
    PaymentCard updated = paymentCardService.setActiveAndReturn(id, dto.getActive());
    return ResponseEntity.ok(paymentCardMapper.toResponseDto(updated));
  }

}
