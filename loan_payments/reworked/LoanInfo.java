import java.util.Date;

public class LoanInfo {
    public int months;
    public Date loan_date;
    public double percent;
    public double amount;
    public double payoutFine;

    public double remaining;

    public LoanInfo(int months, Date loan_date, double percent, double amount, double payoutFine) {
        this.months = months;
        this.loan_date = loan_date;
        this.percent = percent;
        this.amount = amount;
        this.payoutFine = payoutFine;

        this.remaining = this.amount;
    }
}
