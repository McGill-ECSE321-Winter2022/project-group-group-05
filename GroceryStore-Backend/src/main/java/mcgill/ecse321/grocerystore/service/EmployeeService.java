package mcgill.ecse321.grocerystore.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeScheduleRepository;
import mcgill.ecse321.grocerystore.dao.OwnerRepository;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;

/**
 * Implementation of the Employee RESTful services for GroceryStoreApplication.
 * <p>
 * This class provides business methods for RESTful services related to the Employee class. These
 * methods cover the following system requirements/use cases:
 * <ul>
 * <li>RQ12 - The grocery store system shall allow the owner to create and delete employee
 * accounts.</li>
 * <li>RQ13 - The grocery store system shall allow the store owner to assign schedules to store
 * employees.</li>
 * </ul>
 * 
 * @author Harrison Wang
 */
@Service
public class EmployeeService {

  @Autowired
  EmployeeRepository employeeRepository;

  // this repository is referenced for business methods used to fulfill RQ13.
  @Autowired
  EmployeeScheduleRepository scheduleRepository;

  // these repositories are referenced to ensure uniqueness of the Employee username.
  @Autowired
  CustomerRepository customerRepository;
  @Autowired
  OwnerRepository ownerRepository;

  /**
   * Creates a new Employee Account. (RQ12)
   * <ul>
   * <li>username must be unique across all Employee, Customer, and Owner instances.</li>
   * <li>email is valid if it does not contain spaces, contains an '@', contains at least 1 '.'
   * after the '@'. It must also contain something in front of the '@', between the '@' and '.', and
   * after the '.'.</li>
   * </ul>
   * 
   * @param username - the username of the Employee Account
   * @param email - the email of the Employee Account
   * @param password - the password of the Employee Account
   * @return The Employee Instance created with the input parameters
   * @throws IllegalArgumentException when the input parameters are invalid or empty
   */
  @Transactional
  public Employee createEmployee(String username, String email, String password)
      throws IllegalArgumentException {
    String error = "";
    // null checks
    if (username == null || username.trim().length() == 0)
      error += "Employee username cannot be empty! ";
    if (email == null || username.trim().length() == 0)
      error += "Employee email cannot be empty! ";
    if (password == null || password.trim().length() == 0)
      error += "Employee password cannot be empty! ";
    if (error.length() > 0)
      throw new IllegalArgumentException(error.trim());
    // uniqueness of username check
    if (employeeRepository.findByUsername(username) != null
        || customerRepository.findByUsername(username) != null
        || ownerRepository.findByUsername(username) != null)
      throw new IllegalArgumentException("Username is already taken!");
    // valid email check
    if (email.contains(" ") || !email.contains(".") || email.indexOf("@") < 1
        || email.indexOf(".") <= email.indexOf("@") + 1
        || email.lastIndexOf(".") >= email.length() - 1) {
      throw new IllegalArgumentException("Employee email is invalid!");
    }
    Employee newEmployee = new Employee();
    newEmployee.setUsername(username);
    newEmployee.setEmail(email);
    newEmployee.setPassword(password);
    newEmployee = employeeRepository.save(newEmployee);
    return newEmployee;
  }

  /**
   * Deletes the specified Employee (RQ12)
   * 
   * @param username - username of the employee to be deleted
   * @return The Employee Instance that was removed from the database
   * @throws IllegalArgumentException when the employee to be deleted doesn't exist
   */
  @Transactional
  public void deleteEmployee(String username) throws IllegalArgumentException {
    // clear the employee's schedule before deleting
    removeAllSchedules(username);
    employeeRepository.delete(getEmployee(username));
  }

  /**
   * Changes the email of the specified Employee
   * 
   * @param username - username of the employee to be modified
   * @param newEmail - the new email value to change to
   * @throws IllegalArgumentException when the employee to be modified doesn't exist
   */
  @Transactional
  public void setEmployeeEmail(String username, String newEmail) throws IllegalArgumentException {
    var employee = getEmployee(username);
    if (newEmail.contains(" ") || !newEmail.contains(".") || newEmail.indexOf("@") < 1
        || newEmail.indexOf(".") <= newEmail.indexOf("@") + 1
        || newEmail.lastIndexOf(".") >= newEmail.length() - 1) {
      throw new IllegalArgumentException("Employee email is invalid!");
    }
    employee.setEmail(newEmail);
    employeeRepository.save(employee);
  }

  /**
   * Changes the password of the specified Employee
   * 
   * @param username - username of the employee to be modified
   * @param newPassword - the new password value to change to
   * @throws IllegalArgumentException when the employee to be modified doesn't exist
   */
  @Transactional
  public void setEmployeePassword(String username, String newPassword)
      throws IllegalArgumentException {
    var employee = getEmployee(username);
    if (newPassword == null || newPassword.trim().length() == 0) {
      throw new IllegalArgumentException("Employee password cannot be empty!");
    }
    employee.setPassword(newPassword);
    employeeRepository.save(employee);
  }

  /**
   * Assigns a schedule(s) to an Employee. (RQ13)
   * 
   * @param username - username of the employee to add schedules to
   * @param scheduleIds - ids for all the schedules to be added
   * @throws IllegalArgumentException if a schedule could not be added
   */
  @Transactional
  public void addSchedules(String username, long... scheduleIds) throws IllegalArgumentException {
    for (long scheduleId : scheduleIds) {
      addSchedule(username, scheduleId);
    }
  }

  /**
   * helper method for addSchedules(String, long...)
   * <p>
   * Fails if one of the following is true:
   * <ul>
   * <li>schedule already exists in employee</li>
   * <li>employee contains a schedule with the same time and shift</li>
   * </ul>
   * 
   * @param username - username of the Employee to assign the schedule to
   * @param scheduleId - id of the schedule to be assigned
   * @throws IllegalArgumentException when an input parameter is invalid or corresponds to
   *         non-existent objects, or when the schedule is already assigned.
   */
  @Transactional
  private void addSchedule(String username, long scheduleId) throws IllegalArgumentException {
    var employee = getEmployee(username);
    EmployeeSchedule schedule = verifyScheduleId(scheduleId);
    if (employee.getEmployeeSchedules() != null) {
      for (var existingSchedule : employee.getEmployeeSchedules()) {
        if (schedule.equals(existingSchedule)
            || (schedule.getDate().toString().equals(existingSchedule.getDate().toString())
                && schedule.getShift().getName().equals(existingSchedule.getShift().getName()))) {
          throw new IllegalArgumentException(
              "That schedule is already assigned to Employee with username \""
                  + employee.getUsername() + "\"!");
        }
      }
    }
    employee.addEmployeeSchedule(schedule);
    employeeRepository.save(employee);
  }

  /**
   * Removes a schedule(s) to an Employee. (RQ13)
   * 
   * @param username - username of the employee to remove schedules from
   * @param scheduleIds - ids for all the schedules to be removed
   * @throws IllegalArgumentException if a schedule could not be removed
   */
  @Transactional
  public void removeSchedules(String username, long... scheduleIds)
      throws IllegalArgumentException {
    for (long scheduleId : scheduleIds) {
      removeSchedule(username, scheduleId);
    }
  }

  /**
   * Clears all schedules assigned to the Employee. Generates a list of all the EmployeeSchedule
   * ids, then calls removeSchedule on all of them
   *
   * @param username - username of the employee to clear
   * @throws IllegalArgumentException when the username does not correspond to an employee
   */
  @Transactional
  public void removeAllSchedules(String username) throws IllegalArgumentException {
    var employee = getEmployee(username);
    if (employee.getEmployeeSchedules() == null) {
      // if the schedule set is null, all schedules have already been removed.
      return;
    }
    long[] scheduleIds = new long[employee.getEmployeeSchedules().size()];
    int i = 0;
    for (var schedule : employee.getEmployeeSchedules()) {
      scheduleIds[i] = schedule.getId();
      i++;
    }
    removeSchedules(username, scheduleIds);
  }

  /**
   * helper method for removeSchedules(String, long...)
   * 
   * @param username - username of the Employee to remove the schedule from
   * @param scheduleId - id of the schedule to be removed
   * @throws IllegalArgumentException when an input parameter is invalid or corresponds to
   *         non-existent objects, or when the schedule could not be removed.
   */
  @Transactional
  private void removeSchedule(String username, long scheduleId) throws IllegalArgumentException {
    var employee = getEmployee(username);
    EmployeeSchedule schedule = verifyScheduleId(scheduleId);
    if (employee.removeEmployeeSchedule(schedule)) {
      scheduleRepository.delete(schedule);
      employeeRepository.save(employee);
    } else {
      throw new IllegalArgumentException("EmployeeSchedule with id '" + scheduleId
          + "' could not be removed from Employee with username \"" + username + "\"");
    }
  }

  /**
   * Fetches the Employee instance with the given username
   *
   * @param username - username of the employee to clear
   * @throws IllegalArgumentException when the username does not correspond to an employee
   */
  @Transactional
  public Employee getEmployee(String username) throws IllegalArgumentException {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Employee username cannot be empty!");
    }
    Employee requestedEmployee = employeeRepository.findByUsername(username);
    if (requestedEmployee == null) {
      throw new IllegalArgumentException(
          "Employee with username \"" + username + "\" does not exist!");
    }
    return requestedEmployee;
  }

  /**
   * Implemented using .findByUsernameIgnoreCaseContainingOrderByUsername() to automatically sort
   * the list. An empty string is passed to the above method, because all usernames in the database
   * will contain an empty string.
   * 
   * @return sorted list (ascending lexicographical order) of employees
   */
  @Transactional
  public List<Employee> getAllEmployees() {
    return employeeRepository.findByUsernameIgnoreCaseContainingOrderByUsername("");
  }

  /**
   * Fetches a list of all the employees whose usernames correspond to the search query.
   * 
   * @param searchQuery
   * @return list of all employees containing the searchQuery in their username, sorted
   *         lexicographically in ascending order
   * @throws IllegalArgumentException when searchQuery is null or empty
   */
  @Transactional
  public List<Employee> searchEmployeesAscending(String searchQuery)
      throws IllegalArgumentException {
    if (searchQuery == null || searchQuery.trim().length() == 0) {
      throw new IllegalArgumentException("Search Query must not be empty!");
    }
    return employeeRepository.findByUsernameIgnoreCaseContainingOrderByUsername(searchQuery);
  }

  /**
   * Fetches a list of all the employees whose usernames correspond to the search query.
   * 
   * @param searchQuery
   * @return list of all employees containing the searchQuery in their username, sorted
   *         lexicographically in descending order
   * @throws IllegalArgumentException when searchQuery is null or empty
   */
  @Transactional
  public List<Employee> searchEmployeesDescending(String searchQuery)
      throws IllegalArgumentException {
    if (searchQuery == null || searchQuery.trim().length() == 0) {
      throw new IllegalArgumentException("Search Query must not be empty!");
    }
    return employeeRepository.findByUsernameIgnoreCaseContainingOrderByUsernameDesc(searchQuery);
  }

  /**
   * Used to validate and fetch an instance of EmployeeSchedule given an id
   * 
   * @param scheduleId - auto-generated id of the EmployeeSchedule instance
   * @return EmployeeSchedule instance
   * @throws IllegalArgumentException when the requested EmployeeSchedule is not in the repository
   */
  private EmployeeSchedule verifyScheduleId(long scheduleId) throws IllegalArgumentException {
    EmployeeSchedule requestedSchedule = scheduleRepository.findById(scheduleId);
    if (requestedSchedule == null) {
      throw new IllegalArgumentException(
          "EmployeeSchedule with id '" + scheduleId + "' does not exist!");
    }
    return requestedSchedule;
  }

}
