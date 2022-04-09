
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

@Service
public class ShiftService {

  @Autowired
  ShiftRepository shiftRepo;
  @Autowired
  EmployeeScheduleRepository scheduleRepo;
  @Autowired
  EmployeeRepository employeeRepo;

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

  @Transactional
  // Get the shift with the specific name
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

  @Transactional
  // Get all the shifts sorted by name in order
  public List<Shift> getAllShifts() {
    return shiftRepo.findAllByOrderByName();
  }

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
