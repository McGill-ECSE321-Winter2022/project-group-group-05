package mcgill.ecse321.grocerystore.dto;

import java.util.List;

public class EmployeeDto {

  private String username;
  private String email;
  private String password;
  private List<EmployeeScheduleDto> employeeSchedules;

  public EmployeeDto(String username, String email, String password,
      List<EmployeeScheduleDto> employeeSchedules) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.employeeSchedules = employeeSchedules;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @return the employeeSchedules
   */
  public List<EmployeeScheduleDto> getEmployeeSchedules() {
    return employeeSchedules;
  }

  /**
   * @param employeeSchedules the employeeSchedules to set
   */
  public void setEmployeeSchedules(List<EmployeeScheduleDto> employeeSchedules) {
    this.employeeSchedules = employeeSchedules;
  }

}
