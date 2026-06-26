package com.app.moneymanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.moneymanager.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByProfile_IdOrderByDateDesc(long profileId);

    List<ExpenseEntity> findTop5ByProfile_IdOrderByDateDesc(long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") long profileId);

    List<ExpenseEntity> findByProfile_IdAndDateBetweenAndNameContainingIgnoreCase(
            long profileId, LocalDate startDate, LocalDate endDate,
            String keyword, Sort sort);

    List<ExpenseEntity> findByProfile_IdAndDateBetween(
            long profileId, LocalDate startDate, LocalDate endDate, Sort sort);




List<ExpenseEntity>findByProfileIdAndDate(Long profileId, LocalDate date);}