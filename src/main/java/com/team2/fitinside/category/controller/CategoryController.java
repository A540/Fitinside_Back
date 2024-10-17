package com.team2.fitinside.category.controller;

//import com.team2.fitinside.category.dto.CategoryImageResponseDTO;
import com.team2.fitinside.category.dto.CategoryResponseDTO;
import com.team2.fitinside.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/categories")
//@RequiredArgsConstructor
//public class CategoryController {
//
//    private final CategoryService categoryService;
//
//    // 모든 카테고리 조회
//    @GetMapping
//    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
//        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
////        if (categories.isEmpty()) {
////            return ResponseEntity.noContent().build();
////        }
//        return ResponseEntity.ok(categories);
//    }
//
//    // 특정 ID의 카테고리 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
//        CategoryResponseDTO category = categoryService.getCategoryById(id);
//        return ResponseEntity.ok(category);
//    }
//}

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/parents")
    public ResponseEntity<List<CategoryResponseDTO>> getParentCategories() {
        return ResponseEntity.ok(categoryService.getParentCategories());
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<CategoryResponseDTO>> getChildCategories(@PathVariable Long parentId) {
        return ResponseEntity.ok(categoryService.getChildCategories(parentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
}
