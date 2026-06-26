package com.app.moneymanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.moneymanager.dto.ExpenseDTO;
import com.app.moneymanager.dto.IncomeDTO;
import com.app.moneymanager.entity.CategoryEntity;
import com.app.moneymanager.entity.ExpenseEntity;
import com.app.moneymanager.entity.IncomeEntity;
import com.app.moneymanager.entity.ProfileEntity;
import com.app.moneymanager.repository.CategoryRepository;
import com.app.moneymanager.repository.IncomeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {
	private final CategoryRepository categoryRepository;
	private final ProfileService profileService;
	private final IncomeRepository incomeRepository;

	public IncomeDTO addIncome(IncomeDTO dto) {
		ProfileEntity profile = profileService.getCurrentProfile();
		CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found"));
		IncomeEntity newIncome = toEntity(dto, profile, category);
		newIncome = incomeRepository.save(newIncome);
		return toDTO(newIncome);

	}

	public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
		ProfileEntity profile = profileService.getCurrentProfile();
		LocalDate now = LocalDate.now();
		LocalDate startDate = now.withDayOfMonth(1);
		LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

		List<IncomeEntity> list = incomeRepository.findByProfile_IdAndDateBetween(profile.getId(), startDate, endDate,
				Sort.by(Sort.Direction.DESC, "date"));

		return list.stream().map(this::toDTO).toList(); // ← this was missing
	}

	public void deleteIncome(Long id) {
		ProfileEntity profile = profileService.getCurrentProfile();
		IncomeEntity entity = incomeRepository.findById(id).orElseThrow(() -> new RuntimeException("income not found"));
		if (!entity.getProfile().getId().equals(profile.getId())) {
			throw new RuntimeException("You are not authorized to delete this income");
		}
		incomeRepository.delete(entity);
	}

	public List<IncomeDTO> getLatest5IncomesForCurrentUser() {
		ProfileEntity profile = profileService.getCurrentProfile();
		List<IncomeEntity> list = incomeRepository.findTop5ByProfile_IdOrderByDateDesc(profile.getId());
		return list.stream().map(this::toDTO).toList();
	}

	public BigDecimal getTotalIncomesForCurrentUser() {
		ProfileEntity profile = profileService.getCurrentProfile();
		BigDecimal total = incomeRepository.findTotalIncomeByProfileId(profile.getId());
		return total != null ? total : BigDecimal.ZERO;
	}

	public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
		ProfileEntity profile = profileService.getCurrentProfile();
		List<IncomeEntity> list = incomeRepository.findByProfile_IdAndDateBetweenAndNameContainingIgnoreCase(0,
				startDate, endDate, keyword, sort);
		return list.stream().map(this::toDTO).toList();
	}

	private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category) {
		return IncomeEntity.builder().id(dto.getId()).name(dto.getName()).icon(dto.getIcon()).amount(dto.getAmount())
				.date(dto.getDate()).createdAt(dto.getCreatedAt()).updatedAt(dto.getUpdatedAt()).profile(profile)
				.category(category).build();
	}

	private IncomeDTO toDTO(IncomeEntity entity) {
		return IncomeDTO.builder().id(entity.getId()).name(entity.getName()).icon(entity.getIcon())
				.amount(entity.getAmount()).date(entity.getDate()).createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)

				.categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A").build();
	}
}
