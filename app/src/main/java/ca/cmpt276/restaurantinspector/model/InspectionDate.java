package ca.cmpt276.restaurantinspector.model;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * InspectionDate stores info about date of an inspection.
 * Can
 */
public class InspectionDate {
    private final int year;
    private final Month month;
    private final int day;
    private final LocalDate inspectionDate;
    InspectionDate instance;

    protected InspectionDate(String date) {
        // date format: YYYYMMDD
        year = Integer.parseInt(date.substring(0, 4));
        month = Month.of(Integer.parseInt(date.substring(4, 6)));
        day = Integer.parseInt(date.substring(6));
        inspectionDate = LocalDate.of(year, month, day);
    }

    @NonNull
    @Override
    public String toString() {
        // Help from: https://stackoverflow.com/questions/28177370/how-to-format-localdate-to-string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        return inspectionDate.format(formatter);
    }

    public boolean isWithinThirtyDays(){
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return !inspectionDate.isBefore(thirtyDaysAgo);
    }

    public boolean isWithinLastYear(){
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        return !inspectionDate.isBefore(oneYearAgo);
    }

    public int getYear(){
        return year;
    }

    public String getMonth(){
        return getMonthStandaloneName(month);
    }

    public int getDay(){
        return day;
    }

    public int getDaysAgo(){
        return (int) ChronoUnit.DAYS.between(inspectionDate, LocalDate.now());
    }

    // https://stackoverflow.com/questions/14832151/how-to-get-month-name-from-calendar
    private String getMonthStandaloneName(Month month) {
        return month.getDisplayName(
                TextStyle.SHORT_STANDALONE,
                Locale.getDefault()
        );
    }
    protected int compareTo(InspectionDate otherInspectionDate) {
        return this.inspectionDate.compareTo(otherInspectionDate.inspectionDate);
    }
}
