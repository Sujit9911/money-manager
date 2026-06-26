package com.app.moneymanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.moneymanager.dto.CategoryDTO;
import com.app.moneymanager.entity.CategoryEntity;
import com.app.moneymanager.entity.ProfileEntity;
import com.app.moneymanager.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	
	
	private final CategoryRepository categoryRepository;
	private final ProfileService profileService;
	 
	public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
		ProfileEntity profile=profileService.getCurrentProfile();
		if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
			throw new RuntimeException( "Category name already exists for this profile");
		}
		CategoryEntity newCategory=toEntity(categoryDTO, profile);
		newCategory=categoryRepository.save(newCategory);
		return toDTO(newCategory);
	}
	
	public List<CategoryDTO> getCategoriesForCurrentUser(){
		ProfileEntity profile=profileService.getCurrentProfile();
		List<CategoryEntity> categories=categoryRepository.findByProfileId(profile.getId());
		return categories.stream().map(this::toDTO).toList();
	}
	
	public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
		ProfileEntity profile=profileService.getCurrentProfile();
		List<CategoryEntity> entities=categoryRepository.findByTypeAndProfileId(type, profile.getId());
		return entities.stream().map(this::toDTO).toList();
	}
	
	public CategoryDTO upadateCategory(Long categoryId, CategoryDTO dto) {
		ProfileEntity profile=profileService.getCurrentProfile();
		CategoryEntity existingCategory=categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
				.orElseThrow(()-> new RuntimeException("Category not found for the current user"));
		existingCategory.setName(dto.getName());
		existingCategory.setIcon(dto.getIcon());
		
		existingCategory=categoryRepository.save(existingCategory);
		return toDTO(existingCategory);
		
	}
	
	
	
	private CategoryEntity toEntity(CategoryDTO categoryDTO,ProfileEntity profile) {
		return CategoryEntity.builder()
				.name(categoryDTO.getName())
				.icon(categoryDTO.getIcon())
				.profile (profile)
				.type(categoryDTO.getType())
				.build();
	

}
private	CategoryDTO toDTO(CategoryEntity entity) {
	return CategoryDTO.builder()
			.id(entity.getId())
			.profileId(entity.getProfile() !=null ? entity.getProfile().getId() : null)
			.name(entity.getName())
			.icon(entity.getIcon())
			.createdAt(entity.getCreatedAt())
			.updatedAt(entity.getUpdatedAt())
			.type(entity.getType())
			.build();
	
}
 
	
 }





