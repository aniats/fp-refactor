import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class Loan_process {
    public static class LoanUtils {
      public static int hundredPercent = 100;
      public static int monthsInYear = 12;
    }

    public static Date addMonth(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    public static Schedule get_next_Schedule(int month, LoanInfo loanInfo) {
        Schedule schedule = new Schedule();

        schedule.setDate(addMonth(loanInfo.loan_date, month));

        double loan_payment = get_loan_payment(loanInfo.amount, loanInfo.months);
        double sum = loanInfo.amount - loan_payment*(month);

        schedule.setAmount_loan(loan_payment);
        schedule.setAmount_prc(get_percent_payment(sum, loanInfo.percent));

        return schedule;
    }

    private static double get_loan_payment(double amount, int months) {
        return amount/months;
    }

    private static double get_total_payout_at_once(double amount, double payout, Date initial_date, Date current_date) {
        return amount + ChronoUnit.MONTHS.between(
                YearMonth.from(initial_date.toInstant()) ,
                YearMonth.from(current_date.toInstant())
        ) * payout;
    }

    private static double get_percent_payment(double amount, double percent) {
        double monthly_percent = get_monthly_percent(percent);
        return amount * monthly_percent;
    }

    private static double get_monthly_percent(double percent) {
        return percent / LoanUtils.hundredPercent / LoanUtils.monthsInYear;
    }

    public static ArrayList<Schedule> getScheduleList(LoanInfo loanInfo) {
        ArrayList<Schedule> scheduleList = new ArrayList<>();

        for (int month = 0; month < loanInfo.months; ++month) {
            Schedule schedule =get_next_Schedule(month, loanInfo);
            scheduleList.add(schedule);
        }

        return scheduleList;
    }
}
