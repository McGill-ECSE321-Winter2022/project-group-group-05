package mcgill.ecse321.grocerystore.dto;

import java.sql.Date;

public class EmployeeScheduleDto {

  private long id;
  private Date date;
  private ShiftDto shift;

  public EmployeeScheduleDto(long id, Date date, ShiftDto shift) {
    this.id = id;
    this.date = date;
    this.shift = shift;
  }

  /**
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * @return the shift
   */
  public ShiftDto getShift() {
    return shift;
  }

}
