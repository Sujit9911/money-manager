package com.app.moneymanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.moneymanager.dto.CategoryDTO;
import com.app.moneymanager.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
		CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
	}

	@GetMapping
	public ResponseEntity<List<CategoryDTO>> getCategories() {
		List<CategoryDTO> categories = categoryService.getCategoriesForCurrentUser();
		return ResponseEntity.ok(categories);
	}

	@GetMapping("/type")
	public ResponseEntity<List<CategoryDTO>> getCategoriesByTypeForCurrentUser(@PathVariable String type) {
		List<CategoryDTO> list = categoryService.getCategoriesByTypeForCurrentUser(type);
		return ResponseEntity.ok(list);
	}
	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO){
		CategoryDTO updatedCategory=categoryService.upadateCategory(categoryId, categoryDTO);
		return ResponseEntity.ok(updatedCategory);
	}

}