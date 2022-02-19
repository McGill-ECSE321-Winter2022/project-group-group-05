package mcgill.ecse321.grocerystore.model;

import java.sql.Time;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class OpeningHours {

  private String daysOfWeek;
  private Time startTime;
  private Time endTime;

  @Id
  public String getDaysOfWeek() {
    return this.daysOfWeek;
  }

  public void setDaysOfWeek(String value) {
    this.daysOfWeek = value;
  }

  public Time getStartTime() {
    return this.startTime;
  }

  public void setStartTime(String value) {
    this.startTime = Time.valueOf(value);
  }

  public void setStartTime(Time value) {
    this.startTime = value;
  }

  public Time getEndTime() {
    return this.endTime;
  }

  public void setEndTime(String value) {
    this.endTime = Time.valueOf(value);
  }

  public void setEndTime(Time value) {
    this.endTime = value;
  }
}
