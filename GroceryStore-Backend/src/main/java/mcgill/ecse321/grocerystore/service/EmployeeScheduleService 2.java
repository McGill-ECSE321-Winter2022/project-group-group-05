package mcgill.ecse321.grocerystore.service;



import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeScheduleRepository;
import mcgill.ecse321.grocerystore.dao.ShiftRepository;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;
import mcgill.ecse321.grocerystore.model.Shift;

public class EmployeeScheduleService {
  @Autowired
  EmployeeScheduleRepository employeeScheduleRepo;
  @Autowired
  ShiftRepository shiftRepo;
  @Autowired
  EmployeeRepository employeeRepo;


  @Transactional
  public EmployeeSchedule createEmployeeSchedule(Date date, String shiftName)
      throws IllegalArgumentException {
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be empty.");
    }
    Shift shift = this.getShift(shiftName);
    EmployeeSchedule aEmployeeSchedule = new EmployeeSchedule();
    aEmployeeSchedule.setDate(date);
    // link the schedule with a Shift.
    aEmployeeSchedule.setShift(shift);
    return employeeScheduleRepo.save(aEmployeeSchedule);
  }


  @Transactional
  public EmployeeSchedule getEmployeeSchedule(long id) throws IllegalArgumentException {

    EmployeeSchedule schedule = this.employeeScheduleRepo.findById(id);
    if (schedule != null) {
      return schedule;
    }
    throw new IllegalArgumentException("EmployeeSchedule with id '" + id + "' does not exist.");

  }

  @Transactional
  // Get all the schedules sorted by date in order
  public List<EmployeeSchedule> getAllSchedulesOrderedByDate() {
    return employeeScheduleRepo.findAllByOrderByDate();
  }



  // Update Schedule with new shift
  @Transactional
  public EmployeeSchedule updateEmployeeSchedule(Long scheduleId, String shiftName, Date date)
      throws IllegalArgumentException {

    EmployeeSchedule schedule = getEmployeeSchedule(scheduleId);
    Shift shift = getShift(shiftName);
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be empty.");
    }
    schedule.setDate(date);
    schedule.setShift(shift);
    return employeeScheduleRepo.save(schedule);

  }

  /**
   * Get the employeeSchedule of a employee given the name
   * 
   * @param userName
   * @return List<EmployeeSchedule>
   */
  @Transactional
  public List<EmployeeSchedule> getAllScheduleByEmployee(String userName) {
    if (userName == null || userName.trim().length() == 0) {
      throw new IllegalArgumentException("Employee username cannot be empty!");
    }
    if (this.employeeRepo.findByUsername(userName) == null) {
      throw new IllegalArgumentException(
          "Employee with username '" + userName + "' does not exist!");
    }
    Employee employee = this.employeeRepo.findByUsername(userName);
    return toList(employee.getEmployeeSchedules());

  }

  /**
   * This method deletes the schedule with the ID,it first checks the invalidty of the shcedule
   * instance then it literates through the employeeRepo to find the emplyee instance that has
   * asscociation with the schedule and removes the association and then saves the edited employee
   * instance.Then it deletes the schedule instance
   * 
   * @param ID
   * @throws IllegalArgumentException
   */
  @Transactional
  public void deleteEmployeeSchedule(long ID) throws IllegalArgumentException {
    // Get the schedule instance
    EmployeeSchedule schedule = this.getEmployeeSchedule(ID);
    // literate throught the employee
    for (Employee employee : this.employeeRepo.findAll()) {
      if (employee.getEmployeeSchedules().contains(schedule)) {
        // remove the association
        this.employeeRepo.findByUsername(employee.getUsername()).removeEmployeeSchedule(schedule);
        // save the edited employee
        this.employeeRepo.save(employeeRepo.findByUsername(employee.getUsername()));
      }
    }
    this.employeeScheduleRepo.delete(schedule);

  }


  private Shift getShift(String name) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Shift name cannot be empty.");
    }
    Shift shift = this.shiftRepo.findByName(name);
    if (shift == null) {
      throw new IllegalArgumentException("Shift with name '" + name + "' does not exist.");
    }

    return shift;
  }

  private <T> List<T> toList(Iterable<T> iterable) {
    List<T> resultList = new ArrayList<T>();
    for (T t : iterable) {
      resultList.add(t);
    }
    return resultList;
  }
}

