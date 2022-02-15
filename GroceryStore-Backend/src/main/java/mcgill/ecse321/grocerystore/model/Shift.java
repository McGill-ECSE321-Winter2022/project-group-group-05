package mcgill.ecse321.grocerystore.model;

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
  private String startTime;
  private String endTime;

  // Getter Methods
  // --------------
  public String getName() {
    return name;
  }

  public String getStartTime() {
    return startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  // Setter Methods
  // --------------
  public void setName(String name) {
    this.name = name;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

}
