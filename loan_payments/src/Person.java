import java.util.ArrayList;
import java.util.List;

public class Person {
  public List<LoanInfo> takenLoans;
  public String name;

  public List<Schedule> getMergedSchedules() {
    List<Schedule> mergedSchedules = new ArrayList<>();
    for (LoanInfo loan : takenLoans) {
      List<Schedule> currentSchedule = Loan.getScheduleList(loan);
      mergedSchedules.addAll(currentSchedule);
    }
    return mergedSchedules;
  }
}
