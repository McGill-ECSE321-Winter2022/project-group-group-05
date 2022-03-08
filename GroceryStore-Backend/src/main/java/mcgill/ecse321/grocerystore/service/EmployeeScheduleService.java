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
  public EmployeeSchedule createEmployeeSchedule(Date date, long id)
      throws IllegalArgumentException {


    if (date == null) {
      throw new IllegalArgumentException("Date cannot be empty");
    }
    
    if (this.employeeScheduleRepo.findById(id) != null) {
      throw new IllegalArgumentException("The id is already used.");
    }



    EmployeeSchedule aEmployeeSchedule = new EmployeeSchedule();
    return employeeScheduleRepo.save(aEmployeeSchedule);
  }

  @Transactional


  public EmployeeSchedule getEmployeeSchedule(long id) throws IllegalArgumentException {

    EmployeeSchedule schedule = this.employeeScheduleRepo.findById(id);
    if (schedule != null) {
      return schedule;
    }
    throw new IllegalArgumentException("Schedule of this id does not exist.");

  }

  @Transactional
  // Get all the schedules sorted by date in order
  public List<EmployeeSchedule> getAllEmployeeSchedulByDateInOrder() {
    return toList(this.employeeScheduleRepo.findAllByOrderByDate());
  }

  @Transactional
  // Get all the schedules sorted by date in order
  public List<EmployeeSchedule> getAllEmployeeSchedulByDateInReverseOrder() {
    return toList(this.employeeScheduleRepo.findAllByOrderByDateDesc());
  }


  @Transactional
  public List<EmployeeSchedule> getEmployeeScheduleByDate(Date date) {
    if (this.employeeScheduleRepo.findAllByDate(date) == null) {
      throw new IllegalArgumentException("No schedules exist on this date.");
    }
    return toList(employeeScheduleRepo.findAllByDate(date));
  }

  @Transactional
  public EmployeeSchedule updateEmployeeSchedule(EmployeeSchedule schedule, Shift shift)
      throws IllegalArgumentException {
    if (schedule == null) {
      throw new IllegalArgumentException("Schedule canot be null.");
    }

    if (shift == null) {
      throw new IllegalArgumentException("Shift cannot be null.");
    }
    if (this.employeeScheduleRepo.findById(schedule.getId()) == null) {
      throw new IllegalArgumentException("Schedule does not exist in the repo.");
    }
    if (this.shiftRepo.findByName(shift.getName()) == null) {
      this.shiftRepo.save(shift);
    }

    schedule.setShift(shift);
    return this.employeeScheduleRepo.save(schedule);
  }
  /*
   * ----------------------------------------------------------------------------
   * -----------------------------------Shifts-----------------------------------
   * ----------------------------------------------------------------------------
   */

  


/**
 * Get the employeeSchedule of a employee given the name
 * @param userName
 * @return List<EmployeeSchedule>
 */
  @Transactional
  public List<EmployeeSchedule> getAllShiftsByEmployee(String userName) {
    if (userName == null || userName.trim().length() == 0) {
      throw new IllegalArgumentException("Employee must have a name.");
    }
    if(this.employeeRepo.findByUsername(userName)==null) {
      throw new IllegalArgumentException("employee cannot be null.");
    }
    Employee employee=this.employeeRepo.findByUsername(userName);
    return toList(employee.getEmployeeSchedules());
   
  }
  
  @Transactional
  public EmployeeSchedule deleteEmployeeSchedule(long ID) throws IllegalArgumentException {
   
      EmployeeSchedule schedule = this.employeeScheduleRepo.findById(ID);
      if (schedule == null) {
          throw new IllegalArgumentException("The specific item with ID \"" + ID + "\" does not exist.");
      }
      this.employeeScheduleRepo.delete(schedule);
      return schedule;
  }



  private <T> List<T> toList(Iterable<T> iterable) {
    List<T> resultList = new ArrayList<T>();
    for (T t : iterable) {
      resultList.add(t);
    }
    return resultList;
  }
}

