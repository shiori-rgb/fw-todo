package com.example.todoapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CalendarController {

    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {
        LocalDate today = LocalDate.now();
        YearMonth current = YearMonth.of(
                year == null ? today.getYear() : year,
                month == null ? today.getMonthValue() : month);

        LocalDate firstDay = current.atDay(1);
        LocalDate lastDay = current.atEndOfMonth();
        List<List<LocalDate>> calendarWeeks = createCalendarWeeks(firstDay, lastDay);

        YearMonth previous = current.minusMonths(1);
        YearMonth next = current.plusMonths(1);
        model.addAttribute("calendarYear", current.getYear());
        model.addAttribute("calendarMonth", current.getMonthValue());
        model.addAttribute("calendarFrom", firstDay);
        model.addAttribute("calendarTo", lastDay);
        model.addAttribute("calendarWeeks", calendarWeeks);
        model.addAttribute("previousYear", previous.getYear());
        model.addAttribute("previousMonth", previous.getMonthValue());
        model.addAttribute("nextYear", next.getYear());
        model.addAttribute("nextMonth", next.getMonthValue());
        return "calendar";
    }

    private List<List<LocalDate>> createCalendarWeeks(LocalDate firstDay, LocalDate lastDay) {
        int daysBeforeFirst = firstDay.getDayOfWeek().getValue() % 7;
        int daysAfterLast = 6 - (lastDay.getDayOfWeek().getValue() % 7);
        LocalDate gridStart = firstDay.minusDays(daysBeforeFirst);
        LocalDate gridEnd = lastDay.plusDays(daysAfterLast);

        List<List<LocalDate>> weeks = new ArrayList<>();
        List<LocalDate> week = new ArrayList<>();
        for (LocalDate date = gridStart; !date.isAfter(gridEnd); date = date.plusDays(1)) {
            week.add(date.isBefore(firstDay) || date.isAfter(lastDay) ? null : date);
            if (week.size() == 7) {
                weeks.add(week);
                week = new ArrayList<>();
            }
        }
        return weeks;
    }
}
