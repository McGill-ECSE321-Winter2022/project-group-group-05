package mcgill.ecse321.grocerystore.model;

import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Implementation of the EmployeeSchedule Class for GroceryStoreApplication.
 * <p>
 * This class represents the assignment of a particular employee to a particular shift in
 * GroceryStoreApplication; it functions as an association class between the Employee Class and the
 * Shift Class. It facilitates the implementation of the following requirements:
 * <ul>
 * <li>RQ13 - Instances of the EmployeeSchedule class represents the assignment of a shift to a
 * store employee. The Owner can assign schedules to employees by assigning EmployeeSchedule
 * instances.</li>
 * </ul>
 * 
 * @author Harrison Wang
 */
@Entity
public class EmployeeSchedule {

  // EmployeeSchedule Attributes
  @Id
  @GeneratedValue
  private long id;
  private Date date;

  // EmployeeSchedule Associations
  @ManyToOne(optional = false)
  private Shift shift;

  // Getter Methods
  // --------------
  public long getId() {
    return id;
  }

  public Date getDate() {
    return date;
  }

  public Shift getShift() {
    return shift;
  }

  // Setter Methods
  // --------------
  public void setDate(long date) {
    this.date = new Date(date);
  }

  public void setDate(String date) {
    this.date = Date.valueOf(date);
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setShift(Shift shift) {
    this.shift = shift;
  }

}
