package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class OpeningHours {

  private String daysOfWeek;
  private String startTime;
  private String endTime;

  @Id
  public String getDaysOfWeek() {
    return this.daysOfWeek;
  }

  public void setDaysOfWeek(String value) {
    this.daysOfWeek = value;
  }

  public String getStartTime() {
    return this.startTime;
  }

  public void setStartTime(String value) {
    this.startTime = value;
  }

  public String getEndTime() {
    return this.endTime;
  }

  public void setEndTime(String value) {
    this.endTime = value;
  }
}
