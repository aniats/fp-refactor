import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

public class LoanTest{

    private InputStream storedInput;

    @Before
    public void setUp() throws Exception {
        storedInput = System.in;
    }

    @After
    public void tearDown() throws Exception {
        System.setIn(storedInput);
    }

    @Test
    public void parseInfoTest() throws Exception {
        String input = "10.10.2017\n24 10000.0 10.0\n";

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());

        LoanInfo info = OutputAdapter.parseInfo(inputStream, Optional.empty());

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Assert.assertEquals(df.parse("10.10.2017"), info.loan_date);

        Assert.assertEquals(24, info.months);
        Assert.assertEquals(10000.0, info.amount, 0.001);
        Assert.assertEquals(10.0, info.percent, 0.001);
    }

    @Test
    public void get_next_Schedule() throws Exception {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        LoanInfo loanInfo = new LoanInfo(24, df.parse("11.05.2017"), 12.0, 24000.0, 100);

        Schedule schedule = Loan.get_next_Schedule(4, loanInfo);

        Assert.assertEquals(df.parse("11.09.2017"), schedule.getDate());
        Assert.assertEquals(1000.0, schedule.getAmount_loan(), 0.001);
        Assert.assertEquals(200.0, schedule.getAmount_prc(), 0.001);
    }

    @Test
    public void get_next_ScheduleTest() throws Exception {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        LoanInfo loanInfo = new LoanInfo(6, df.parse("11.05.2017"), 12.0, 24000.0, 100);

        ArrayList<Schedule> schedules = Loan.getScheduleList(loanInfo);

        ArrayList<Schedule> idealSchedule = new ArrayList<>();

        idealSchedule.add(new Schedule(df.parse("11.05.2017"), 4000.0, 240.0));
        idealSchedule.add(new Schedule(df.parse("11.06.2017"), 4000.0, 200.0));
        idealSchedule.add(new Schedule(df.parse("11.07.2017"), 4000.0, 160.0));
        idealSchedule.add(new Schedule(df.parse("11.08.2017"), 4000.0, 120.0));
        idealSchedule.add(new Schedule(df.parse("11.09.2017"), 4000.0, 80.0));
        idealSchedule.add(new Schedule(df.parse("11.10.2017"), 4000.0, 40.0));

        Assert.assertEquals(idealSchedule.size(), schedules.size());
        for(int i = 0; i < schedules.size(); ++i) {
            Assert.assertEquals(idealSchedule.get(i).getDate(), schedules.get(i).getDate());
            Assert.assertEquals(idealSchedule.get(i).getAmount_loan(), schedules.get(i).getAmount_loan(), 0.001);
            Assert.assertEquals(idealSchedule.get(i).getAmount_prc(), schedules.get(i).getAmount_prc(), 0.001);
        }
    }
}
