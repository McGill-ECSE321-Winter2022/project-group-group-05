package mcgill.ecse321.grocerystore.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
   * Scans through the Employees schedule set and removes the one with the same date and shift
   * fields as the input parameters.
   * 
   * @param username - the username of the employee account
   * @param date - the date on which the employee has the shift
   * @param shift - the shift to delete from the employee
   * @throws IllegalArgumentException when the scheduled shift could not be removed, either because
   *         the input parameters were invalid or because the employee hasn't been assigned a
   *         schedule matching the input parameters.
   */
  @Transactional
  public void removeSchedule(String username, Date date, String shift)
      throws IllegalArgumentException {
    var employee = getEmployee(username);
    if (date == null) {
      throw new IllegalArgumentException("Date must not be null!");
    }
    EmployeeSchedule scheduleToBeRemoved = null;
    if (employee.getEmployeeSchedules() != null) {
      for (var existingSchedule : employee.getEmployeeSchedules()) {
        if (existingSchedule.getDate().equals(date)
            && existingSchedule.getShift().getName().equals(shift)) {
          scheduleToBeRemoved = existingSchedule;
          break;
        }
      }
    }
    if (scheduleToBeRemoved == null) {
      throw new IllegalArgumentException(
          "No EmployeeSchedule object found with the provided date and Shift");
    }
    employee.removeEmployeeSchedule(scheduleToBeRemoved);
    scheduleRepository.delete(scheduleToBeRemoved);
    employeeRepository.save(employee);
  }

  /**
   * Clears all schedules assigned to the Employee. Generates a list of all the EmployeeSchedule
   * objects associated with the Employee, then removes them from the schedule set and deletes them
   * one by one.
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
    EmployeeSchedule[] schedules = new EmployeeSchedule[employee.getEmployeeSchedules().size()];
    int i = 0;
    for (var schedule : employee.getEmployeeSchedules()) {
      schedules[i] = schedule;
      i++;
    }
    for (EmployeeSchedule scheduleToBeRemoved : schedules) {
      employee.removeEmployeeSchedule(scheduleToBeRemoved);
      scheduleRepository.delete(scheduleToBeRemoved);
      employeeRepository.save(employee);
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

  @Transactional
  public List<EmployeeSchedule> getEmployeeScheduleSorted(String username)
      throws IllegalArgumentException {
    Employee employee = getEmployee(username);
    ArrayList<EmployeeSchedule> schedules =
        new ArrayList<EmployeeSchedule>(employee.getEmployeeSchedules());
    Collections.sort(schedules, new Comparator<EmployeeSchedule>() {
      @Override
      public int compare(EmployeeSchedule p1, EmployeeSchedule p2) {
        if (p1.getDate().equals(p2.getDate())) {
          return p1.getShift().getStartTime().before(p2.getShift().getStartTime()) ? -1 : 1;
        }
        return p1.getDate().before(p2.getDate()) ? -1 : 1;
      }
    });
    return schedules;
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
