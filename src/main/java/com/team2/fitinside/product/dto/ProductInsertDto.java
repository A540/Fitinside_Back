package com.team2.fitinside.product.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Getter
@Setter
public class ProductInsertDto {

    @NotBlank(message = "카테고리 ID는 필수 입력 값입니다.")
    private Long categoryId;

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String productName;

    @NotBlank(message = "가격은 필수 입력 값입니다.")
    private Integer price;

//    @NotBlank(message = "상품 설명은 필수 입력 값입니다.")
    private String info;

    @NotBlank(message = "재고는 필수 입력 값입니다.")
    private Integer stock;

//    @NotBlank(message = "제조사는 필수 입력 값입니다.")
    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

//    @NotBlank(message = "이미지 URL 목록은 필수 입력 값입니다.")
//    @Size(min = 1, message = "최소 하나 이상의 이미지 URL이 필요합니다.")
    private List<MultipartFile> productImgUrls;

}
