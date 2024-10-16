package com.team2.fitinside.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartUpdateRequestDto {

    private Long productId;
    private int quantity;

    // 상품 옵션 추가 예정 (기능 고도화)
}
