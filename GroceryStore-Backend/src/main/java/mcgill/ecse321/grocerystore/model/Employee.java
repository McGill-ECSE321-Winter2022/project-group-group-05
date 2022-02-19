package mcgill.ecse321.grocerystore.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Implementation of the Employee Class for GroceryStoreApplication.
 * <p>
 * This class represents the Employee user in GroceryStoreApplication. It facilitates the
 * implementation of the following requirements:
 * <ul>
 * <li>RQ8, RQ9, RQ10 - Instances of the Employee class identify the user as an employee, allowing
 * these permissions to be granted.</li>
 * <li>RQ12 - Instances of the Employee Class represent employee accounts, allowing the owner to
 * create and delete employee accounts.</li>
 * <li>RQ13 - Instances of the Employee Class represent employee accounts, allowing the owner to
 * assign schedules to store employees.</li>
 * </ul>
 * 
 * @author Harrison Wang
 */
@Entity
public class Employee {

  // Employee Attributes
  @Id
  private String username;
  private String email;
  private String password;

  // Employee Associations
  @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  private Set<EmployeeSchedule> employeeSchedules;

  // Getter Methods
  // --------------
  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public Set<EmployeeSchedule> getEmployeeSchedules() {
    return employeeSchedules;
  }

  // Setter Methods
  // --------------
  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setEmployeeSchedules(Set<EmployeeSchedule> employeeSchedules) {
    this.employeeSchedules = employeeSchedules;
  }

  /**
   * @param employeeSchedule - The EmployeeSchedule instance to add
   * @return boolean - True if the EmployeeSchedule instance was added successfully.
   */
  public boolean addEmployeeSchedule(EmployeeSchedule employeeSchedule) {
    if (employeeSchedules == null) {
      employeeSchedules = new HashSet<EmployeeSchedule>();
    }
    return employeeSchedules == null ? false : employeeSchedules.add(employeeSchedule);
  }

  /**
   * The .remove(Object o) method isn't helpful when classes are loaded from the database. Instead,
   * this method will search for an instance of EmployeeSchedule with the specified ID and remove it
   * if found.
   * 
   * @param scheduleId - The id of the EmployeeSchedule to be removed.
   * @return boolean - True if the EmployeeSchedule was successfully removed.
   */
  public boolean removeEmployeeSchedule(long scheduleId) {
    EmployeeSchedule tempSchedule = null;
    for (var schedule : employeeSchedules) {
      if (schedule.getId() == scheduleId) {
        tempSchedule = schedule;
        break;
      }
    }
    return tempSchedule == null ? false : employeeSchedules.remove(null);
  }

}
