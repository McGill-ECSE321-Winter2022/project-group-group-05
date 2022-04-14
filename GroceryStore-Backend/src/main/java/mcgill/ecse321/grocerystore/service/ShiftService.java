
package mcgill.ecse321.grocerystore.service;


import java.sql.Time;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeScheduleRepository;
import mcgill.ecse321.grocerystore.dao.ShiftRepository;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;
import mcgill.ecse321.grocerystore.model.Shift;

/**
 * RESTful Service methods for Shift
 * 
 * @author Yida Pan
 */
@Service
public class ShiftService {

  @Autowired
  ShiftRepository shiftRepo;
  @Autowired
  EmployeeScheduleRepository scheduleRepo;
  @Autowired
  EmployeeRepository employeeRepo;

  /**
   * Creates a new shift with the given name and time
   * 
   * @param name name of the new shift
   * @param startTime start time of the new shift
   * @param endTime end time of the new shift
   * @return the new Shift instance created
   * @throws IllegalArgumentException
   */
  @Transactional
  public Shift createShift(String name, Time startTime, Time endTime)
      throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Shift name cannot be empty.");
    }
    if (this.shiftRepo.findByName(name) != null) {
      throw new IllegalArgumentException("Name is already taken.");
    }
    if (startTime == null) {
      throw new IllegalArgumentException("Shift start time cannot be null.");
    }
    if (endTime == null) {
      throw new IllegalArgumentException("Shift end time cannot be null.");
    }

    if (endTime.before(startTime)) {
      throw new IllegalArgumentException("Shift end time cannot be before its start time.");
    }
    Shift shift = new Shift();
    shift.setName(name);
    shift.setStartTime(startTime);
    shift.setEndTime(endTime);
    return shiftRepo.save(shift);
  }

  /**
   * Returns the shift with the given name
   * 
   * @param name name of the shift
   * @return the Shift instance with the given name
   * @throws IllegalArgumentException
   */
  @Transactional
  public Shift getShift(String name) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Shift name cannot be empty.");
    }
    Shift shift = this.shiftRepo.findByName(name);
    if (shift == null) {
      throw new IllegalArgumentException("Shift with name '" + name + "' does not exist.");
    }
    return shift;
  }

  /**
   * Returns the list of all shifts sorted by name
   * 
   * @return the list of all Shift instances
   */
  @Transactional
  public List<Shift> getAllShifts() {
    return shiftRepo.findAllByOrderByName();
  }

  /**
   * Deletes the shift with the given name
   * 
   * @param shiftName name of the shift
   */
  @Transactional
  public void deleteShiftByName(String shiftName) {
    Shift shift = this.getShift(shiftName);
    // remove associations
    Iterator<Employee> employeeIter = employeeRepo.findAll().iterator();
    while (employeeIter.hasNext()) {
      Employee employee = employeeIter.next();
      Iterator<EmployeeSchedule> scheduleIter = employee.getEmployeeSchedules().iterator();
      while (scheduleIter.hasNext()) {
        EmployeeSchedule schedule = scheduleIter.next();
        if (schedule.getShift().equals(shift)) {
          scheduleIter.remove();
          scheduleRepo.delete(schedule);
        }
      }
      employeeRepo.save(employee);
    }
    shiftRepo.delete(shift);
  }

  /**
   * Change the time of the specific shift
   * 
   * @param name name of the shift
   * @param startTime new start time of the shift
   * @param endTime new end time of the shift
   * @return the Shift instance after the change
   */
  @Transactional
  public Shift updateShift(String name, Time startTime, Time endTime) {
    if (startTime == null) {
      throw new IllegalArgumentException("Shift start time cannot be null.");
    }
    if (endTime == null) {
      throw new IllegalArgumentException("Shift end time cannot be null.");
    }
    if (endTime.before(startTime)) {
      throw new IllegalArgumentException("Shift end time cannot be before its start time.");
    }
    // get the shift with the name
    Shift shift = getShift(name);
    // update the shift
    shift.setEndTime(endTime);
    shift.setStartTime(startTime);
    // save the updated shift
    return this.shiftRepo.save(shift);
  }

}
