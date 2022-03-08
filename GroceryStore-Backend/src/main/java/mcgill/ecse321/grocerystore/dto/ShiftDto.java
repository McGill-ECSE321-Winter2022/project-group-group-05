package mcgill.ecse321.grocerystore.dto;

import java.sql.Time;

public class ShiftDto {

  private String name;
  private Time startTime;
  private Time endTime;

  public ShiftDto(String name, Time startTime, Time endTime) {
    this.name = name;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
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
