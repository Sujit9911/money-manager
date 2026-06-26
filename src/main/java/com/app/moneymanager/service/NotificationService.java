package com.app.moneymanager.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.app.moneymanager.dto.ExpenseDTO;
import com.app.moneymanager.entity.ProfileEntity;
import com.app.moneymanager.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Kolkata")
    public void sendDailyIncomeExpenseReminder() {
        log.info("JOB STARTED: sendDailyIncomeExpenseReminder()");

        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            String body = "Hi " + profile.getFullname() + ",<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>"
                    + "<a href='" + frontendUrl + "' style='display:inline-block;padding:10px 20px;"
                    + "background:#4CAF50;color:#fff;text-decoration:none;"
                    + "border-radius:5px;font-weight:bold;'>Go to Money Manager</a>"
                    + "<br><br>Best regards,<br>Money Manager Team";

            emailService.sendEmail(profile.getEmail(), "Daily Income and Expense Reminder", body);
        }

        log.info("JOB COMPLETED: sendDailyIncomeExpenseReminder()");
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Kolkata")
    public void sendDailyExpenseSummary() {
        log.info("JOB STARTED: sendDailyExpenseSummary()");

        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todaysExpenses = expenseService
                    .getExpensesForUserOnDate(profile.getId(), LocalDate.now());

            if (todaysExpenses.isEmpty()) {
                continue;
            }

            StringBuilder table = new StringBuilder();
            table.append("<table style='border-collapse:collapse;width:100%;'>")
                 .append("<tr style='background-color:#f2f2f2;'>")
                 .append("<th style='border:1px solid #ddd;padding:8px;'>Sr No</th>")
                 .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                 .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                 .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
        
                 .append("</tr>");

            int i = 1;
            for (ExpenseDTO expense : todaysExpenses) {
                table.append("<tr>")
                     .append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>")
                     .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>")
                     .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>")
                     .append("<td style='border:1px solid #ddd;padding:8px;'>")
                     .append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A")
                     .append("</td>")
                     .append("<td style='border:1px solid #ddd;padding:8px;'>").append("</td>")
                     .append("</tr>");   // ✅ closing tag
            }

            table.append("</table>");

            String body = "Hi " + profile.getFullname() + ",<br><br>"
                    + "Here is the summary of your expenses for today:<br><br>"
                    + table
                    + "<br><br>Best regards,<br>Money Manager Team";

            emailService.sendEmail(profile.getEmail(), "Daily Expense Summary", body);
        }

        log.info("JOB COMPLETED: sendDailyExpenseSummary()");  // ✅ inside the method
    }
}