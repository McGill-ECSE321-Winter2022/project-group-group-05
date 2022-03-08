package mcgill.ecse321.grocerystore.dto;

import java.sql.Date;

public class HolidayDto {
  private String name;
  private Date date;

  public HolidayDto(String name, Date date) {
    this.name = name;
    this.date = date;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return date;
  }


}
