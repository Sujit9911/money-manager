package com.app.moneymanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.moneymanager.entity.IncomeEntity;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    List<IncomeEntity> findByProfile_IdOrderByDateDesc(long profileId);

    List<IncomeEntity> findTop5ByProfile_IdOrderByDateDesc(long profileId);

    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") long profileId);

    List<IncomeEntity> findByProfile_IdAndDateBetweenAndNameContainingIgnoreCase(
            long profileId, LocalDate startDate, LocalDate endDate,
            String keyword, Sort sort);

    List<IncomeEntity> findByProfile_IdAndDateBetween(
            long profileId, LocalDate startDate, LocalDate endDate, Sort sort);
}