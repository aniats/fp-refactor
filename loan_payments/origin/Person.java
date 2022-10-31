import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Person {
  public List<LoanInfo> takenLoans;
  public String name;

  public void dumpTotalScheduleToFile() {
    try {
      Scanner in = new Scanner(System.in);
      System.out.print("Enter file name to save " + name + "'s schedules (should be <filename>.xls): ");
      String fileName = in.nextLine();

      Pattern p = Pattern.compile(".+\\.xls");
      Matcher m = p.matcher(fileName);
      boolean b = m.matches();

      if(!b) {
        throw new InputMismatchException("File name format should be <filename>.xls");
      }

      FileOutputStream fs = new FileOutputStream(fileName);
    } catch (IOException | InputMismatchException e) {
      e.printStackTrace();
    }

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

    DataFormat format = book.createDataFormat();
    CellStyle dateStyle = book.createCellStyle();
    dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));

    int rowNum = 1;

    for (LoanInfo loan : takenLoans) {
      List<Schedule> currentSchedule = Loan.getScheduleList(loan);
      for(Schedule subSchedule : currentSchedule) {
        Row row = sheet.createRow(rowNum);

        dateCell = row.createCell(0);
        dateCell.setCellValue(subSchedule.getDate());
        dateCell.setCellStyle(dateStyle);

        loanPaymentCell = row.createCell(1);
        loanPaymentCell.setCellValue(subSchedule.getAmount_loan());

        percentPaymentCell = row.createCell(2);
        percentPaymentCell.setCellValue(subSchedule.getAmount_prc());

        sumPaymentCell = row.createCell(3);
        sumPaymentCell.setCellFormula("$B$" + Integer.toString(rowNum+1)+ "+$C$" + Integer.toString(rowNum+1));

        rowNum++;
      }
    }

    for(int col = 0; col < 5; ++col) {
      sheet.autoSizeColumn(col);
    }

    book.write(fs);
    book.close();
  }
}
