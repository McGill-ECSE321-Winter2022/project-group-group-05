package mcgill.ecse321.grocerystore.service;

import java.sql.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeScheduleRepository;
import mcgill.ecse321.grocerystore.dao.OwnerRepository;
import mcgill.ecse321.grocerystore.dao.ShiftRepository;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;
import mcgill.ecse321.grocerystore.model.Shift;

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

  // these repositories are referenced for business methods used to fulfill RQ13.
  @Autowired
  EmployeeScheduleRepository scheduleRepository;
  @Autowired
  ShiftRepository shiftRepository;

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
    if (!verifyEmail(email)) {
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
    if (!verifyEmail(newEmail)) {
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
   * Constructs an EmployeeSchedule object from the given parameters and adds it to the specified
   * Employee
   * 
   * @param username - the username of the employee account
   * @param date - the date on which the employee has the shift
   * @param shift - the shift to assign to the employee
   * @throws IllegalArgumentException when the scheduled shift could not be assigned to the
   *         employee, either because the input parameters were invalid or because the employee has
   *         already been assigned the shift on the same date.
   */
  @Transactional
  public void addSchedule(String username, Date date, String shift)
      throws IllegalArgumentException {
    var employee = getEmployee(username);
    if (date == null) {
      throw new IllegalArgumentException("Date must not be null!");
    }
    var shiftToBeAdded = verifyShiftName(shift);
    if (employee.getEmployeeSchedules() != null) {
      // If the employee is already assigned schedules, we need to check them to make sure we aren't
      // assigning the same shift again.
      for (var existingSchedule : employee.getEmployeeSchedules()) {
        if (existingSchedule.getDate().equals(date)
            && existingSchedule.getShift().getName().equals(shift)) {
          throw new IllegalArgumentException(
              "That schedule is already assigned to Employee with username \""
                  + employee.getUsername() + "\"!");
        }
      }
    }
    EmployeeSchedule schedule = new EmployeeSchedule();
    schedule.setDate(date);
    schedule.setShift(shiftToBeAdded);
    scheduleRepository.save(schedule);
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
   * Used to match the email string to a regex which checks for proper email format. The
   * restrictions for an email to be considered valid can be found <a href=
   * "https://www.baeldung.com/java-email-validation-regex#strict-regular-expression-validation">here</a>
   * 
   * @param email - the email string to be checked
   * @return a boolean indicating whether the email conforms to standards or not. True indicates
   *         that the email is valid.
   */
  private boolean verifyEmail(String email) {
    return Pattern.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", email);
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

  /**
   * Used to validate and fetch an instance of Shift given a name
   * 
   * @param name - primary key name of the Shift instance
   * @return Shift instance
   * @throws IllegalArgumentException when the requested Shift is not in the repository
   */
  private Shift verifyShiftName(String name) throws IllegalArgumentException {
    Shift requestedShift = shiftRepository.findByName(name);
    if (requestedShift == null) {
      throw new IllegalArgumentException("Shift with name '" + name + "' does not exist!");
    }
    return requestedShift;
  }

}
