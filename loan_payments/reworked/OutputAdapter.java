import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputAdapter {
    private static Workbook initializeExcelBook() {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Выплаты");

        Row titleRow = sheet.createRow(0);
        Cell dateCell = titleRow.createCell(0);
        dateCell.setCellValue("Дата");
        Cell loanPaymentCell = titleRow.createCell(1);
        loanPaymentCell.setCellValue("Выплаты по основному долгу");
        Cell percentPaymentCell = titleRow.createCell(2);
        percentPaymentCell.setCellValue("Выплаты по основному долгу");
        Cell sumPaymentCell = titleRow.createCell(3);
        sumPaymentCell.setCellValue("Суммарная выплата по кредиту");

        return book;
    }

    public static void dumpPersonalScheduleToFile(Person person) throws IOException {
        String queryPrompt = "Enter file name to save " + person.name + "'s schedules (should be <filename>.xls): ";
        OutputStream ostream = OutputAdapter.queryUserForOutput(queryPrompt);

        Workbook book = initializeExcelBook();
        Sheet sheet = book.getSheet("Выплаты");

        int rowNum = 1;

        DataFormat format = book.createDataFormat();
        CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));

        for(Schedule subSchedule : person.getMergedSchedules()) {
            Row row = sheet.createRow(rowNum);

            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(subSchedule.getDate());
            dateCell.setCellStyle(dateStyle);

            Cell loanPaymentCell = row.createCell(1);
            loanPaymentCell.setCellValue(subSchedule.getAmount_loan());

            Cell percentPaymentCell = row.createCell(2);
            percentPaymentCell.setCellValue(subSchedule.getAmount_prc());

            Cell sumPaymentCell = row.createCell(3);
            sumPaymentCell.setCellFormula("$B$" + (rowNum + 1) + "+$C$" + (rowNum + 1));

            rowNum++;
        }

        for(int col = 0; col < 5; ++col) {
            sheet.autoSizeColumn(col);
        }

        book.write(ostream);
        book.close();
    }

    public static LoanInfo parseInfo(InputStream inputStream, Optional<OutputStream> outputStream) throws ParseException, IOException {
        Scanner in = new Scanner(inputStream);

        if (outputStream.isPresent()) {
            outputStream.get().write("Enter day of loan: ".getBytes());
        }

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
        Date loan_date = null;
        try {
            loan_date = df.parse(in.next());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (outputStream.isPresent()) {
            outputStream.get().write("Enter term in months: ".getBytes());
        }
        int months = in.nextInt();

        if (outputStream.isPresent()) {
            outputStream.get().write("Enter amount of loan: ".getBytes());
        }
        double amount = Double.parseDouble(in.next());

        if (outputStream.isPresent()) {
            outputStream.get().write("Enter percent: ".getBytes());
        }
        double percent = Double.parseDouble(in.next());

        if (outputStream.isPresent()) {
            outputStream.get().write("Enter payout fine: ".getBytes());
        }
        double payoutFine = 0;

        return new LoanInfo(months, loan_date, percent, amount, payoutFine);
    }

    public static void writeScheduleIntoExcel(OutputStream oStream, ArrayList<Schedule> lst) throws IOException {
        Workbook book = initializeExcelBook();

        DataFormat format = book.createDataFormat();
        CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));

        Sheet sheet = book.createSheet("Выплаты");

        int rowNum = 1;
        for(Schedule sch : lst) {
            Row row = sheet.createRow(rowNum);

            Cell dateCell = row.createCell(0);
            dateCell.setCellValue(sch.getDate());
            dateCell.setCellStyle(dateStyle);

            Cell loanPaymentCell = row.createCell(1);
            loanPaymentCell.setCellValue(sch.getAmount_loan());

            Cell percentPaymentCell = row.createCell(2);
            percentPaymentCell.setCellValue(sch.getAmount_prc());

            Cell sumPaymentCell = row.createCell(3);
            sumPaymentCell.setCellFormula("$B$" + Integer.toString(rowNum+1)+ "+$C$" + Integer.toString(rowNum+1));

            rowNum++;
        }

        for(int col = 0; col < 5; ++col) {
            sheet.autoSizeColumn(col);
        }

        book.write(oStream);
        book.close();
    }

    public static OutputStream queryUserForOutput(String prompt) {
        try {
            Scanner in = new Scanner(System.in);
            System.out.print(prompt);
            String fileName = in.nextLine();

            Pattern p = Pattern.compile(".+\\.xls");
            Matcher m = p.matcher(fileName);
            boolean b = m.matches();

            if(!b) {
                throw new InputMismatchException("File name format should be <filename>.xls");
            }

            return new FileOutputStream(fileName);
        } catch (IOException | InputMismatchException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void main(String[] args) throws ParseException, IOException {
        LoanInfo loanInfo = parseInfo(System.in, Optional.of(System.out));

        ArrayList<Schedule> scheduleList = Loan.getScheduleList(loanInfo);

        String queryPrompt = "Enter file name to save loan schedule (should be <filename>.xls): ";
        OutputStream ostream = queryUserForOutput(queryPrompt);

        writeScheduleIntoExcel(ostream, scheduleList);
    }
}
