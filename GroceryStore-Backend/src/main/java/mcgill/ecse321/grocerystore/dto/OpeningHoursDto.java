package mcgill.ecse321.grocerystore.dto;

import java.sql.Time;

public class OpeningHoursDto {

  private String daysOfWeek;
  private Time startTime;
  private Time endTime;

  public OpeningHoursDto(String daysOfWeek, Time startTime, Time endTime) {
    this.daysOfWeek = daysOfWeek;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  /**
   * @return the daysOfWeek
   */
  public String getDaysOfWeek() {
    return daysOfWeek;
  }

  /**
   * @return the startTime
   */
  public Time getStartTime() {
    return startTime;
  }

  /**
   * @return the endTime
   */
  public Time getEndTime() {
    return endTime;
  }

}
