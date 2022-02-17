package mcgill.ecse321.grocerystore.model;

import java.sql.Time;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Implementation of the Shift Class for GroceryStoreApplication.
 * <p>
 * This class represents a particular shift for a store employee to take in GroceryStoreApplication.
 * It facilitates the implementation of the following requirements:
 * <ul>
 * <li>RQ13 - Instances of the Shift class represent employee shifts outlined by the Owner. These
 * shifts can then be assigned to store employees.</li>
 * </ul>
 * 
 * @author Harrison Wang
 */
@Entity
public class Shift {

  // Shift Attributes
  @Id
  private String name;
  private Time startTime;
  private Time endTime;

  // Getter Methods
  // --------------
  public String getName() {
    return name;
  }

  public Time getStartTime() {
    return startTime;
  }

  public Time getEndTime() {
    return endTime;
  }

  // Setter Methods
  // --------------
  public void setName(String name) {
    this.name = name;
  }

  public void setStartTime(long startTime) {
    this.startTime = new Time(startTime);
  }

  public void setStartTime(String startTime) {
    this.startTime = Time.valueOf(startTime);
  }

  public void setStartTime(Time startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = new Time(endTime);
  }

  public void setEndTime(String endTime) {
    this.endTime = Time.valueOf(endTime);
  }

  public void setEndTime(Time endTime) {
    this.endTime = endTime;
  }

}
