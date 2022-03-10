package mcgill.ecse321.grocerystore.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import mcgill.ecse321.grocerystore.dto.EmployeeDto;
import mcgill.ecse321.grocerystore.dto.EmployeeScheduleDto;
import mcgill.ecse321.grocerystore.dto.ShiftDto;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;
import mcgill.ecse321.grocerystore.model.Shift;
import mcgill.ecse321.grocerystore.service.EmployeeService;

/**
 * RESTful API for Employee service methods
 * 
 * @author Harrison Wang
 */
@CrossOrigin(origins = "*")
@RestController
public class EmployeeController {

  @Autowired
  private EmployeeService service;

  @PostMapping(value = {"/employee/{username}", "/employee/{username}/"})
  public EmployeeDto createEmployee(@PathVariable("username") String username,
      @RequestParam String email, @RequestParam String password) throws IllegalArgumentException {
    return convertToDto(service.createEmployee(username, email, password));
  }

  @DeleteMapping(value = {"/employee/{username}", "/employee/{username}/"})
  @ResponseStatus(value = HttpStatus.OK)
  public void deleteEmployee(@PathVariable("username") String username)
      throws IllegalArgumentException {
    service.deleteEmployee(username);
  }

  // Patch Mappings

  @PatchMapping(value = {"/employee/{username}", "/employee/{username}/"})
  @ResponseStatus(value = HttpStatus.OK)
  public void updateEmployee(@PathVariable("username") String username,
      @RequestParam(required = false) String newEmail,
      @RequestParam(required = false) String newPassword) throws IllegalArgumentException {
    if (newEmail != null)
      service.setEmployeeEmail(username, newEmail);
    if (newPassword != null)
      service.setEmployeePassword(username, newPassword);
  }

  @PatchMapping(value = {"/employee/{username}/addSchedule", "/employee/{username}/addSchedule/"})
  @ResponseStatus(value = HttpStatus.OK)
  public void addSchedule(@PathVariable("username") String username,
      @RequestParam("date") Date date, @RequestParam String shift) throws IllegalArgumentException {
    service.addSchedule(username, date, shift);
  }

  @PatchMapping(
      value = {"/employee/{username}/removeSchedules", "/employee/{username}/removeSchedules/"})
  @ResponseStatus(value = HttpStatus.OK)
  public void removeSchedule(@PathVariable("username") String username,
      @RequestParam("date") Date date, @RequestParam String shift) throws IllegalArgumentException {
    service.removeSchedule(username, date, shift);
  }

  @PatchMapping(value = {"/employee/{username}/removeAllSchedules",
      "/employee/{username}/removeAllSchedules/"})
  @ResponseStatus(value = HttpStatus.OK)
  public void removeAllSchedules(@PathVariable("username") String username)
      throws IllegalArgumentException {
    service.removeAllSchedules(username);
  }

  // Get Mappings

  @GetMapping(value = {"/employee/{username}", "/employee/{username}/"})
  public EmployeeDto getEmployee(@PathVariable("username") String username)
      throws IllegalArgumentException {
    return convertToDto(service.getEmployee(username));
  }

  @GetMapping(value = {"/employee/{username}/getSchedules", "employee/{username}/getSchedules/"})
  public List<EmployeeScheduleDto> getEmployeeScheduleSorted(
      @PathVariable("username") String username) {
    List<EmployeeScheduleDto> schedules = new ArrayList<EmployeeScheduleDto>();
    // Convert list of EmployeeSchedule to list of EmployeeScheduleDto
    for (EmployeeSchedule schedule : service.getEmployeeScheduleSorted(username)) {
      Shift scheduleShift = schedule.getShift();
      ShiftDto shiftDto = new ShiftDto(scheduleShift.getName(), scheduleShift.getStartTime(),
          scheduleShift.getEndTime());
      EmployeeScheduleDto scheduleDto =
          new EmployeeScheduleDto(schedule.getId(), schedule.getDate(), shiftDto);
      schedules.add(scheduleDto);
    }
    return schedules;
  }

  @GetMapping(value = {"/employee/getAll", "/employee/getAll/"})
  public List<EmployeeDto> getAllEmployees() {
    ArrayList<EmployeeDto> employees = new ArrayList<EmployeeDto>();
    for (var employee : service.getAllEmployees()) {
      employees.add(convertToDto(employee));
    }
    return employees;
  }

  @GetMapping(value = {"/employee/searchAscending", "/employee/searchAscending/"})
  public List<EmployeeDto> searchEmployeesAscending(@RequestParam String searchQuery)
      throws IllegalArgumentException {
    ArrayList<EmployeeDto> employees = new ArrayList<EmployeeDto>();
    for (var employee : service.searchEmployeesAscending(searchQuery)) {
      employees.add(convertToDto(employee));
    }
    return employees;
  }

  @GetMapping(value = {"/employee/searchDescending", "/employee/searchDescending/"})
  public List<EmployeeDto> searchEmployeesDescending(@RequestParam String searchQuery)
      throws IllegalArgumentException {
    ArrayList<EmployeeDto> employees = new ArrayList<EmployeeDto>();
    for (var employee : service.searchEmployeesDescending(searchQuery)) {
      employees.add(convertToDto(employee));
    }
    return employees;
  }

  /**
   * Converts domain objects to data transfer objects
   * 
   * @param employee - employee instance to be converted
   * @return employee parameter as a DTO
   */
  private EmployeeDto convertToDto(Employee employee) {
    ArrayList<EmployeeScheduleDto> scheduleList = new ArrayList<EmployeeScheduleDto>();
    if (employee.getEmployeeSchedules() != null) {
      for (var schedule : employee.getEmployeeSchedules()) {
        Shift scheduleShift = schedule.getShift();
        ShiftDto shiftDto = new ShiftDto(scheduleShift.getName(), scheduleShift.getStartTime(),
            scheduleShift.getEndTime());
        EmployeeScheduleDto scheduleDto =
            new EmployeeScheduleDto(schedule.getId(), schedule.getDate(), shiftDto);
        scheduleList.add(scheduleDto);
      }
    }
    return new EmployeeDto(employee.getUsername(), employee.getEmail(), employee.getPassword(),
        scheduleList);
  }
}
