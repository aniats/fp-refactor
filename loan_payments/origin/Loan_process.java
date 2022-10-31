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

    public static LoanInfo get_info(InputStream inputStream, Optional<OutputStream> outputStream) throws ParseException, IOException {
        LoanInfo info = new LoanInfo();

        Scanner in = new Scanner(inputStream);

        if (outputStream.isPresent()) {
            outputStream.get().write("Enter day of loan: ".getBytes());
        }
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        try {
            info.loan_date = df.parse(in.next());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (outputStream.isPresent()) {
            outputStream.get().write("Enter term in months: ".getBytes());
        }
        info.months = in.nextInt();

        if (outputStream.isPresent()) {
            outputStream.get().write("Enter amount of loan: ".getBytes());
        }
        info.amount = Double.parseDouble(in.next());

        if (outputStream.isPresent()) {
            outputStream.get().write("Enter percent: ".getBytes());
        }
        info.percent = Double.parseDouble(in.next());

        return info;
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
        return percent / 100 / 12;
    }

    public static ArrayList<Schedule> getScheduleList(LoanInfo loanInfo) {
        ArrayList<Schedule> scheduleList = new ArrayList<>();

        for (int month = 0; month < loanInfo.months; ++month) {
            Schedule schedule =get_next_Schedule(month, loanInfo);
            scheduleList.add(schedule);
        }

        return scheduleList;
    }

    public static void writeScheduleIntoExcel(OutputStream oStream, ArrayList<Schedule> lst) throws IOException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Кредит");

        Row titleRow = sheet.createRow(0);
        Cell dateCell = titleRow.createCell(0);
        dateCell.setCellValue("Дата");
        Cell loanPaymentCell = titleRow.createCell(1);
        loanPaymentCell.setCellValue("Выплаты по основному долгу");
        Cell percentPaymentCell = titleRow.createCell(2);
        percentPaymentCell.setCellValue("Выплаты по основному долгу");
        Cell sumPaymentCell = titleRow.createCell(3);
        sumPaymentCell.setCellValue("Суммарная выплата по кредиту");

        DataFormat format = book.createDataFormat();
        CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));

        int rowNum = 1;
        for(Schedule sch : lst) {
            Row row = sheet.createRow(rowNum);

            dateCell = row.createCell(0);
            dateCell.setCellValue(sch.getDate());
            dateCell.setCellStyle(dateStyle);

            loanPaymentCell = row.createCell(1);
            loanPaymentCell.setCellValue(sch.getAmount_loan());

            percentPaymentCell = row.createCell(2);
            percentPaymentCell.setCellValue(sch.getAmount_prc());

            sumPaymentCell = row.createCell(3);
            sumPaymentCell.setCellFormula("$B$" + Integer.toString(rowNum+1)+ "+$C$" + Integer.toString(rowNum+1));

            rowNum++;
        }

        for(int col = 0; col < 5; ++col)
            sheet.autoSizeColumn(col);

        book.write(oStream);
        book.close();
    }

    public static void main(String[] args) throws ParseException, IOException {
        LoanInfo loanInfo = get_info(System.in, Optional.of(System.out));

        ArrayList<Schedule> scheduleList = getScheduleList(loanInfo);

        try {
            Scanner in = new Scanner(System.in);
            System.out.print("Enter file name to save (should be <filename>.xls): ");
            String fileName = in.nextLine();

            Pattern p = Pattern.compile(".+\\.xls");
            Matcher m = p.matcher(fileName);
            boolean b = m.matches();

            if(!b)
                throw new InputMismatchException("File name format should be <filename>.xls");

            FileOutputStream fs = new FileOutputStream(fileName);
            writeScheduleIntoExcel(fs, scheduleList);
        } catch (IOException | InputMismatchException e) {
            e.printStackTrace();
        }
    }
}
