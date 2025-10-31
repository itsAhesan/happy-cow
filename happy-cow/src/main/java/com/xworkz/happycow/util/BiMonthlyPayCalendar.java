package com.xworkz.happycow.util;

import lombok.Value;
import java.time.LocalDate;
import java.time.YearMonth;

public final class BiMonthlyPayCalendar {

    private BiMonthlyPayCalendar() {

    }

    @Value
    public static class Window {
        LocalDate start;  // inclusive
        LocalDate end;    // inclusive
        String label;     // e.g., "1–15 Oct 2025" or "16–31 Oct 2025"
    }

    /** 1..15 of a given YearMonth */
    public static Window firstHalf(YearMonth ym) {
        LocalDate s = ym.atDay(1);
        LocalDate e = ym.atDay(15);
        return new Window(s, e, s.getDayOfMonth() + "–" + e.getDayOfMonth() + " " + ym.getMonth() + " " + ym.getYear());
    }

    /** 16..lastDay of a given YearMonth */
    public static Window secondHalf(YearMonth ym) {
        LocalDate s = ym.atDay(16);
        LocalDate e = ym.atEndOfMonth();
        return new Window(s, e, s.getDayOfMonth() + "–" + e.getDayOfMonth() + " " + ym.getMonth() + " " + ym.getYear());
    }

    /**
     * Returns the active "payable window" if today is inside a 3-day notification window.
     * - 16–18 → show 1–15 of SAME month
     * - 1–3   → show 16–end of PREVIOUS month
     * Otherwise: return null (no notifications right now).
     */
    public static Window activePayable(LocalDate today) {
        int d = today.getDayOfMonth();

        if (d >= 16 && d <= 18) {
            // Show 1..15 of this month
            return firstHalf(YearMonth.from(today));
        }

        if (d >= 1 && d <= 3) {
            // Show 16..end of previous month
            YearMonth prev = YearMonth.from(today.minusMonths(1));
            return secondHalf(prev);
        }

        return null; // Not in a notification window
    }
}
