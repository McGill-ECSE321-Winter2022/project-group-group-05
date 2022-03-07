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
      throw new IllegalArgumentException("");
    }
    if (this.employeeScheduleRepo.findById(id) != null) {
      throw new IllegalArgumentException("EmployeeSchedule already exist.");
    }



    EmployeeSchedule aEmployeeSchedule = new EmployeeSchedule();
    aEmployeeSchedule.setDate(date);


    return employeeScheduleRepo.save(aEmployeeSchedule);
  }

  @Transactional


  public EmployeeSchedule getEmployeeSchedule(long id) throws IllegalArgumentException {

    EmployeeSchedule aEmployeeSchedule = this.employeeScheduleRepo.findById(id);
    if (aEmployeeSchedule != null) {
      return aEmployeeSchedule;
    }
    throw new IllegalArgumentException("Schedule does not exist.");

  }


  @Transactional
  public List<EmployeeSchedule> getAllEmployeeSchedules() throws IllegalArgumentException {
    if (this.employeeScheduleRepo.findAll() == null) {
      throw new IllegalArgumentException("No schedules exist in the repository");
    }
    return toList(employeeScheduleRepo.findAll());
  }

  @Transactional
  public List<EmployeeSchedule> getEmployeeScheduleByDate(Date date) {
    if (this.employeeScheduleRepo.findAllByDate(date) == null) {
      throw new IllegalArgumentException("No schedules exist on this day");
    }
    return toList(employeeScheduleRepo.findAllByDate(date));
  }

  @Transactional
  public EmployeeSchedule updateEmployeeSchedule(EmployeeSchedule schedule, Shift shift)
      throws IllegalArgumentException {
    if (schedule == null) {
      throw new IllegalArgumentException("schedule canot be null.");
    }

    if (shift == null) {
      throw new IllegalArgumentException("shift cannot be null.");
    }
    if (this.employeeScheduleRepo.findById(schedule.getId()) == null) {
      throw new IllegalArgumentException("schedule does not exist in the repo.");
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

  @Transactional
  public Shift getShiftByEmployeeSchedule(EmployeeSchedule schedule)
      throws IllegalArgumentException {
    if (schedule == null) {
      throw new IllegalArgumentException("Schedule cannot be null.");
    }
    if (this.employeeScheduleRepo.findById(schedule.getId()) == null) {
      throw new IllegalArgumentException("Schedule does not exist in the repo.");
    }
    return shiftRepo.findByEmployeeSchedule(schedule);
  }



 /* @Transactional
  public List<Shift> getAllShiftsByEmployee(Employee employee) {
    if (employee == null) {
      throw new IllegalArgumentException("employee cannot be null.");
    }
    return toList(shiftRepo.findAllByEmployee(employee));
  }
  */



  private <T> List<T> toList(Iterable<T> iterable) {
    List<T> resultList = new ArrayList<T>();
    for (T t : iterable) {
      resultList.add(t);
    }
    return resultList;
  }
}
