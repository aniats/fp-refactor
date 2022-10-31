import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Loan{
    public static class LoanUtils {
      public static int hundredPercent = 100;
      public static int monthsInYear = 12;
    }

    public static Date addMonth(LoanInfo loanInfo, int monthsToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(loanInfo.loan_date);
        cal.add(Calendar.MONTH, monthsToAdd);
        return cal.getTime();
    }

    public static Schedule get_next_Schedule(int monthsToAdd, LoanInfo loanInfo) {
        Schedule schedule = new Schedule();

        schedule.setDate(addMonth(loanInfo, monthsToAdd));

        double loan_payment = get_loan_payment(loanInfo);
        loanInfo.remaining = loanInfo.amount - loan_payment * monthsToAdd;

        schedule.setAmount_loan(loan_payment);
        schedule.setAmount_prc(get_percent_payment(loanInfo));

        return schedule;
    }

    private static double get_loan_payment(LoanInfo loanInfo) {
        return loanInfo.amount / loanInfo.months;
    }

    private static double get_percent_payment(LoanInfo loanInfo) {
        double monthly_percent = get_monthly_percent(loanInfo);
        return loanInfo.remaining * monthly_percent;
    }

    private static double get_total_payout_at_once(LoanInfo loanInfo, Date current_date) {
        return loanInfo.amount + ChronoUnit.MONTHS.between(
                YearMonth.from(loanInfo.loan_date.toInstant()) ,
                YearMonth.from(current_date.toInstant())
        ) * loanInfo.payoutFine;
    }

    private static double get_monthly_percent(LoanInfo loanInfo) {
        return loanInfo.percent / LoanUtils.hundredPercent / LoanUtils.monthsInYear;
    }

    public static ArrayList<Schedule> getScheduleList(LoanInfo loanInfo) {
        ArrayList<Schedule> scheduleList = new ArrayList<>();

        for (int month = 0; month < loanInfo.months; ++month) {
            Schedule schedule = get_next_Schedule(month, loanInfo);
            scheduleList.add(schedule);
        }

        return scheduleList;
    }
}
