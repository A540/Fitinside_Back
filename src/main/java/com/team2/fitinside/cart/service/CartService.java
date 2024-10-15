package com.team2.fitinside.cart.service;

import com.team2.fitinside.cart.dto.*;
import com.team2.fitinside.cart.entity.Cart;
import com.team2.fitinside.cart.mapper.CartMapper;
import com.team2.fitinside.cart.repository.CartRepository;
import com.team2.fitinside.config.SecurityUtil;
import com.team2.fitinside.global.exception.CustomException;
import com.team2.fitinside.global.exception.ErrorCode;
import com.team2.fitinside.member.entity.Member;
import com.team2.fitinside.member.repository.MemberRepository;
import com.team2.fitinside.product.entity.Product;
import com.team2.fitinside.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final SecurityUtil securityUtil;

    // 장바구니 조회 메서드
    public CartResponseWrapperDto findAllCarts() {

        // member의 id 가져옴 + 권한검사
        Long loginMemberID = getAuthenticatedMemberId();
        List<CartResponseDto> dtos = new ArrayList<>();
        List<Cart> cartList = cartRepository.findAllByMember_Id(loginMemberID);

        // cart -> List<CartResponseDto>
        for (Cart cart : cartList) {
            CartResponseDto cartResponseDto = CartMapper.INSTANCE.toCartResponseDto(cart);
            dtos.add(cartResponseDto);
        }

        // 성공메시지 + List<CartResponseDto> -> CartResponseWrapperDto 반환
        return new CartResponseWrapperDto("장바구니 조회 완료했습니다!", dtos);
    }

    // 장바구니 생성 메서드
    @Transactional
    public void createCart(CartCreateRequestDto dto) {

        Product foundProduct = productRepository.findById(dto.getProductId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        checkQuantity(dto.getQuantity(), foundProduct);

        Long loginMemberID = getAuthenticatedMemberId();

        // 이미 같은 장바구니가 있다면 수정
        if (cartRepository.existsCartByMember_IdAndProduct_Id(loginMemberID, dto.getProductId())) {
            Cart foundCart = cartRepository.findByMember_IdAndProduct_Id(loginMemberID, dto.getProductId()).orElse(null);
            Objects.requireNonNull(foundCart).updateQuantity(dto.getQuantity());
            return;
        }

        Cart cart = CartMapper.INSTANCE.toEntity(dto);
        Member foundMember = memberRepository.findById(loginMemberID).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        cart.setUserAndProduct(foundMember, foundProduct);

        cartRepository.save(cart);
    }

    // 장바구니 수정 메서드
    @Transactional
    public void updateCart(CartUpdateRequestDto dto) {

        Long loginMemberID = getAuthenticatedMemberId();

        Cart cart = cartRepository.findByMember_IdAndProduct_Id(loginMemberID, dto.getProductId()).orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        checkQuantity(dto.getQuantity(), cart.getProduct());

        if (!loginMemberID.equals(cart.getMember().getId())) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
        }

        // 수량을 동일하게 수정하면 리턴
        if (cart.getQuantity() == dto.getQuantity()) return;
        cart.updateQuantity(dto.getQuantity());
    }

    // 장바구니 단일 삭제 메서드
    @Transactional
    public void deleteCart(Long productId) {

        Long loginMemberID = getAuthenticatedMemberId();

        Cart cart = cartRepository.findByMember_IdAndProduct_Id(loginMemberID, productId).orElseThrow(() -> new CustomException(ErrorCode.CART_NOT_FOUND));

        if (!loginMemberID.equals(cart.getMember().getId())) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
        }

        cartRepository.delete(cart);
    }

    // 장바구니 단일 삭제 메서드
    @Transactional
    public void clearCart() {

        Long loginMemberID = getAuthenticatedMemberId();

        List<Cart> cartList = cartRepository.findAllByMember_Id(loginMemberID);
        cartRepository.deleteAll(cartList);
    }

    // 수정범위 확인 메서드
    static void checkQuantity(int quantity, Product product) {

        if (quantity < 1 || quantity > 20) {
            throw new CustomException(ErrorCode.CART_OUT_OF_RANGE);
        }

        // 상품의 재고보다 많은 경우
        if(product.getStock() < quantity) {
            throw new CustomException(ErrorCode.OUT_OF_STOCK);
        }
    }

    // 사용자의 권환 확인 + memberId 가져오는 메서드
    // 따로 분리한 이유 : RuntimeException이 아닌 커스텀 예외 처리 위해서
    private Long getAuthenticatedMemberId() {
        try {
            return securityUtil.getCurrentMemberId();
        } catch (RuntimeException e) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
        }
    }

    // 사용자의 권환 확인 + 정보 가져오는 메서드
//    private String getAuthenticatedUserEmail() throws AccessDeniedException {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
//            throw new AccessDeniedException("권한이 없습니다!");
//        }
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        return userDetails.getUsername(); // getUsername()을 호출
////        return (CustomUserDetails) authentication.getPrincipal().getUserName();
//    }

    // 장바구니 조회 시 상품 정보 포함
    public CartProductResponseWrapperDto getCartProducts() {
        Long loginMemberID = getAuthenticatedMemberId();
        List<Object[]> results = cartRepository.findCartProductsByMemberId(loginMemberID);

        // Object[] 결과를 CartProductDto로 변환
        List<CartProductResponseDto> dtos = new ArrayList<>();
        for (Object[] result : results) {
            String productName = (String) result[0];
            int price = (int) result[1];
            int quantity = (int) result[2];
            dtos.add(CartProductResponseDto.builder()
                    .productName(productName)
                    .price(price)
                    .quantity(quantity)
                    .build());
        }

        return new CartProductResponseWrapperDto("장바구니 조회(상품 정보 포함) 완료했습니다!", dtos);
    }

}
