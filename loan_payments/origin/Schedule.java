import java.util.Currency;
import java.util.Date;

public class Schedule {
    private Date date;
    private double amount_loan, amount_prc;

    public Schedule()
    {
        date = new Date();
        amount_loan = amount_prc = 0;
    }

    public Schedule(Date date, double amount_loan, double amount_prc)
    {
        this.date = date;
        this.amount_loan = amount_loan;
        this.amount_prc = amount_prc;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setAmount_loan(double amount_loan) {
        this.amount_loan = amount_loan;
    }

    public double getAmount_loan() {
        return amount_loan;
    }

    public void setAmount_prc(double amount_prc) {
        this.amount_prc = amount_prc;
    }

    public double getAmount_prc() {
        return amount_prc;
    }
}
