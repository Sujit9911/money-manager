package com.app.moneymanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.app.moneymanager.dto.ExpenseDTO;
import com.app.moneymanager.entity.CategoryEntity;
import com.app.moneymanager.entity.ExpenseEntity;
import com.app.moneymanager.entity.ProfileEntity;
import com.app.moneymanager.repository.CategoryRepository;
import com.app.moneymanager.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
	private final CategoryRepository categoryRepository;
	private final ExpenseRepository expenseRepository;
	private final ProfileService profileService;

	public ExpenseDTO addExpense(ExpenseDTO dto) {
		ProfileEntity profile = profileService.getCurrentProfile();
		CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found"));
		ExpenseEntity newExpense = toEntity(dto, profile, category);
		newExpense = expenseRepository.save(newExpense);
		return toDTO(newExpense);

	}

	public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
		ProfileEntity profile = profileService.getCurrentProfile();
		LocalDate now = LocalDate.now();
		LocalDate startDate = now.withDayOfMonth(1);
		LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

		List<ExpenseEntity> list = expenseRepository.findByProfile_IdAndDateBetween(profile.getId(), startDate, endDate,
				Sort.by(Sort.Direction.DESC, "date"));

		return list.stream().map(this::toDTO).toList(); // ← this was missing
	}

	public void deleteExpense(Long id) {
		ProfileEntity profile = profileService.getCurrentProfile();
		ExpenseEntity entity = expenseRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Expense not found"));
		if (!entity.getProfile().getId().equals(profile.getId())) {
			throw new RuntimeException("You are not authorized to delete this expense");
		}
		expenseRepository.delete(entity);
	}

	public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
		ProfileEntity profile = profileService.getCurrentProfile();
		List<ExpenseEntity> list = expenseRepository.findTop5ByProfile_IdOrderByDateDesc(profile.getId());
		return list.stream().map(this::toDTO).toList();
	}

	public BigDecimal getTotalExpensesForCurrentUser() {
		ProfileEntity profile = profileService.getCurrentProfile();
		BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
		return total != null ? total : BigDecimal.ZERO;
	}

	public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
		ProfileEntity profile = profileService.getCurrentProfile();
		List<ExpenseEntity> list = expenseRepository.findByProfile_IdAndDateBetweenAndNameContainingIgnoreCase(0,
				startDate, endDate, keyword, sort);
		return list.stream().map(this::toDTO).toList();
	}
	
	
public List<ExpenseDTO> getExpensesForUserOnDate(long profileId, LocalDate date) {
	List<ExpenseEntity> list= expenseRepository.findByProfileIdAndDate(profileId, date);
	return list.stream().map(this::toDTO).toList();
}
	private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category) {
		return ExpenseEntity.builder().id(dto.getId()).name(dto.getName()).icon(dto.getIcon()).amount(dto.getAmount())
				.date(dto.getDate()).createdAt(dto.getCreatedAt()).updatedAt(dto.getUpdatedAt()).profile(profile)
				.category(category).build();
	}

	private ExpenseDTO toDTO(ExpenseEntity entity) {
		return ExpenseDTO.builder().id(entity.getId()).name(entity.getName()).icon(entity.getIcon())
				.amount(entity.getAmount()).date(entity.getDate()).createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)

				.categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A").build();
	}
}
