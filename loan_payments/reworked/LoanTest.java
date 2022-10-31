import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.*;

public class Loan_processTest{

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
    public void get_infoTest() throws Exception {

        String input = "10.10.2017\n24 10000.0 10.0\n";

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        LoanInfo info = Loan_process.get_info(System.in, Optional.empty());

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Assert.assertEquals(df.parse("10.10.2017"), info.loan_date);

        Assert.assertEquals(24, info.months);
        Assert.assertEquals(10000.0, info.amount, 0.001);
        Assert.assertEquals(10.0, info.percent, 0.001);
    }

    @Test
    public void addMonthTest() throws Exception {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Date date = df.parse("11.05.2017");

        Assert.assertEquals(df.parse("11.08.2017"), Loan_process.addMonth(date, 3));
    }

    @Test
    public void get_next_Schedule() throws Exception {
        LoanInfo loanInfo = new LoanInfo();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        loanInfo.loan_date = df.parse("11.05.2017");
        loanInfo.amount = 24000.0;
        loanInfo.months = 24;
        loanInfo.percent = 12.0;

        Schedule schedule = Loan_process.get_next_Schedule(4, loanInfo);

        Assert.assertEquals(df.parse("11.09.2017"), schedule.getDate());
        Assert.assertEquals(1000.0, schedule.getAmount_loan(), 0.001);
        Assert.assertEquals(200.0, schedule.getAmount_prc(), 0.001);
    }

    @Test
    public void get_next_ScheduleTest() throws Exception {
        LoanInfo loanInfo = new LoanInfo();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        loanInfo.loan_date = df.parse("11.05.2017");
        loanInfo.amount = 24000.0;
        loanInfo.months = 6;
        loanInfo.percent = 12.0;

        ArrayList<Schedule> schedules = Loan_process.getScheduleList(loanInfo);

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
