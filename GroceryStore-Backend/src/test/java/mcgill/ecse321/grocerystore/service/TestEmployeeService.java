package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeScheduleRepository;
import mcgill.ecse321.grocerystore.dao.OwnerRepository;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;

/**
 * RESTful service tests for Employee Class.
 * 
 * @author Harrison Wang
 */
@ExtendWith(MockitoExtension.class)
public class TestEmployeeService {

  @Mock
  private EmployeeRepository employeeDao;
  @Mock
  private EmployeeScheduleRepository employeeScheduleDao;
  @Mock
  private CustomerRepository customerDao;
  @Mock
  private OwnerRepository ownerDao;
  @Mock
  private Employee mockEmployee;
  @Mock
  private EmployeeSchedule mockSchedule;
  @Mock
  private EmployeeSchedule existingMockSchedule;

  @InjectMocks
  private EmployeeService service;

  private static final String EMPLOYEE_KEY = "Albert";
  private static final String EMPLOYEE2_KEY = "Bernie";
  private static final String FAKE_EMPLOYEE_KEY = "NotAnEmployee";

  private static final long SCHEDULE_KEY = 20l;
  private static final long EXISTING_SCHEDULE_KEY = 21l;
  private static final long FAKE_SCHEDULE_KEY = 22l;

  @BeforeEach
  public void setMockOutput() {
    // employeeDao mock outputs
    lenient().when(employeeDao.findByUsername(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(EMPLOYEE_KEY)) {
            return mockEmployee;
          } else {
            return null;
          }
        });
    lenient().when(employeeDao.findAll()).thenAnswer((InvocationOnMock invocation) -> {
      List<Employee> employeeList = new ArrayList<Employee>();
      var employeeOne = new Employee();
      employeeOne.setUsername(EMPLOYEE_KEY);
      var employeeTwo = new Employee();
      employeeTwo.setUsername(EMPLOYEE2_KEY);
      employeeList.add(employeeOne);
      employeeList.add(employeeTwo);
      return employeeList;
    });
    lenient().when(employeeDao.findByUsernameIgnoreCaseContainingOrderByUsername(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          List<Employee> employeeList = new ArrayList<Employee>();
          if (EMPLOYEE_KEY.toUpperCase()
              .contains(((String) invocation.getArgument(0)).toUpperCase())) {
            var employee = new Employee();
            employee.setUsername(EMPLOYEE_KEY);
            employeeList.add(employee);
          }
          if (EMPLOYEE2_KEY.toUpperCase()
              .contains(((String) invocation.getArgument(0)).toUpperCase())) {
            var employee = new Employee();
            employee.setUsername(EMPLOYEE2_KEY);
            employeeList.add(employee);
          }
          return employeeList;
        });
    lenient().when(employeeDao.findByUsernameIgnoreCaseContainingOrderByUsernameDesc(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          List<Employee> employeeList = new ArrayList<Employee>();
          if (EMPLOYEE2_KEY.toUpperCase()
              .contains(((String) invocation.getArgument(0)).toUpperCase())) {
            var employee = new Employee();
            employee.setUsername(EMPLOYEE2_KEY);
            employeeList.add(employee);
          }
          if (EMPLOYEE_KEY.toUpperCase()
              .contains(((String) invocation.getArgument(0)).toUpperCase())) {
            var employee = new Employee();
            employee.setUsername(EMPLOYEE_KEY);
            employeeList.add(employee);
          }
          return employeeList;
        });
    // employeeSchedule mock outputs
    lenient().when(employeeScheduleDao.findById(anyLong()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(SCHEDULE_KEY)) {
            return mockSchedule;
          } else if (invocation.getArgument(0).equals(EXISTING_SCHEDULE_KEY)) {
            return existingMockSchedule;
          } else {
            return null;
          }
        });
    // added to allow username uniqueness check to operate properly
    lenient().when(customerDao.findByUsername(anyString())).thenAnswer(e -> {
      return null;
    });
    lenient().when(ownerDao.findByUsername(anyString())).thenAnswer(e -> {
      return null;
    });
    // whenever anything is saved, just return the parameter object
    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };
    lenient().when(employeeDao.save(any(Employee.class))).thenAnswer(returnParameterAsAnswer);
    // mock output for mock object keys
    lenient().when(mockEmployee.getUsername()).thenAnswer((e) -> {
      return EMPLOYEE_KEY;
    });
    lenient().when(mockEmployee.addEmployeeSchedule(any(EmployeeSchedule.class))).thenAnswer(e -> {
      return e.getArgument(0).equals(mockSchedule);
    });
    lenient().when(mockEmployee.removeEmployeeSchedule(any(EmployeeSchedule.class))).thenAnswer(e -> {
      return e.getArgument(0).equals(existingMockSchedule);
    });
  }

  // Tests for createEmployee(String, String, String)
  // ------------------------------------------------

  @Test
  public void testCreateEmployee() {
    String username = "testEmployee";
    String email = "fake@email.com";
    String password = "password";
    Employee employee = null;
    try {
      employee = service.createEmployee(username, email, password);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(employee);
    assertEquals(username, employee.getUsername());
    assertEquals(email, employee.getEmail());
    assertEquals(password, employee.getPassword());
  }

  @Test
  public void testCreateEmployeeNull() {
    Employee employee = null;
    String error = "";
    try {
      employee = service.createEmployee(null, null, null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals(
        "Employee username cannot be empty! Employee email cannot be empty! Employee password cannot be empty!",
        error);
  }

  @Test
  public void testCreateEmployeeEmpty() {
    Employee employee = null;
    String error = "";
    try {
      employee = service.createEmployee("  ", "  ", "  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals(
        "Employee username cannot be empty! Employee email cannot be empty! Employee password cannot be empty!",
        error);
  }

  @Test
  public void testCreateEmployeeExisting() {
    String username = EMPLOYEE_KEY;
    String email = "fake@email.com";
    String password = "password";
    Employee employee = null;
    String error = "";
    try {
      employee = service.createEmployee(username, email, password);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Username is already taken!", error);
  }

  @Test
  public void testCreateEmployeeSpacedEmail() {
    String username = "testEmployee";
    String email = "f ake@email.com";
    String password = "password";
    Employee employee = null;
    String error = "";
    try {
      employee = service.createEmployee(username, email, password);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Employee email is invalid!", error);
  }

  @Test
  public void testCreateEmployeeNoDomainEmail() {
    String username = "testEmployee";
    String email = "fake@emailcom";
    String password = "password";
    Employee employee = null;
    String error = "";
    try {
      employee = service.createEmployee(username, email, password);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Employee email is invalid!", error);
  }

  @Test
  public void testCreateEmployeeNoAtEmail() {
    String username = "testEmployee";
    String email = "fakeemail.com";
    String password = "password";
    Employee employee = null;
    String error = "";
    try {
      employee = service.createEmployee(username, email, password);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Employee email is invalid!", error);
  }

  @Test
  public void testCreateEmployeeNoSubjectEmail() {
    String username = "testEmployee";
    String email = "@email.com";
    String password = "password";
    Employee employee = null;
    String error = "";
    try {
      employee = service.createEmployee(username, email, password);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Employee email is invalid!", error);
  }

  @Test
  public void testCreateEmployeeNoSecondaryDomainEmail() {
    String username = "testEmployee";
    String email = "fake@.com";
    String password = "password";
    Employee employee = null;
    String error = "";
    try {
      employee = service.createEmployee(username, email, password);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Employee email is invalid!", error);
  }

  @Test
  public void testCreateEmployeeNoTopLevelDomainEmail() {
    String username = "testEmployee";
    String email = "fake@email.";
    String password = "password";
    Employee employee = null;
    String error = "";
    try {
      employee = service.createEmployee(username, email, password);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Employee email is invalid!", error);
  }

  // Tests for deleteEmployee(String)
  // --------------------------------
  // Note: tests for invalid usernames are not included because this method uses getEmployee(String)
  // internally, which throws errors for invalid username inputs.

  @Test
  public void testDeleteEmployee() {
    Employee employee = null;
    try {
      employee = service.deleteEmployee(EMPLOYEE_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(employeeDao, times(1)).deleteById(anyString());
    assertNotNull(employee);
    assertEquals(EMPLOYEE_KEY, employee.getUsername());
  }

  @Test
  public void testDeleteEmployeeNonExistent() {
    Employee employee = null;
    String error = "";
    try {
      employee = service.deleteEmployee(FAKE_EMPLOYEE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(employeeDao, times(0)).deleteById(anyString());
    assertNull(employee);
    assertEquals("Employee with username \"" + FAKE_EMPLOYEE_KEY + "\" does not exist!", error);
  }

  // Tests for addSchedule(String, long)
  // -----------------------------------
  // Note: tests for invalid usernames are not included because this method uses getEmployee(String)
  // internally, which throws errors for invalid username inputs.

  @Test
  public void testAddSchedule() {
    try {
      service.addSchedule(EMPLOYEE_KEY, SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(mockEmployee, times(1)).addEmployeeSchedule(mockSchedule);
  }
  
  @Test
  public void testAddScheduleExisting() {
    String error = "";
    try {
      service.addSchedule(EMPLOYEE_KEY, EXISTING_SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployee, times(1)).addEmployeeSchedule(existingMockSchedule);
    assertEquals("EmployeeSchedule with id '" + EXISTING_SCHEDULE_KEY
          + "' could not be assigned to Employee with username \"" + EMPLOYEE_KEY + "\"", error);
  }
  
  @Test
  public void testAddScheduleNonExistent() {
    String error = "";
    try {
      service.addSchedule(EMPLOYEE_KEY, FAKE_SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("EmployeeSchedule with id '" + FAKE_SCHEDULE_KEY + "' does not exist!", error);
  }

  // Note: tests for addSchedules(String, long...) are not included because this method uses
  // addSchedule(String, long) internally.

  // Tests for removeSchedule(String, long)
  // -----------------------------------
  // Note: tests for invalid usernames are not included because this method uses getEmployee(String)
  // internally, which throws errors for invalid username inputs.

  @Test
  public void testRemoveSchedule() {
    try {
      service.removeSchedule(EMPLOYEE_KEY, EXISTING_SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(mockEmployee, times(1)).removeEmployeeSchedule(existingMockSchedule);
  }
  
  @Test
  public void testRemoveScheduleMissing() {
    String error = "";
    try {
      service.removeSchedule(EMPLOYEE_KEY, SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployee, times(1)).removeEmployeeSchedule(mockSchedule);
    assertEquals("EmployeeSchedule with id '" + SCHEDULE_KEY
          + "' could not be removed from Employee with username \"" + EMPLOYEE_KEY + "\"", error);
  }
  
  @Test
  public void testRemoveScheduleNonExistent() {
    String error = "";
    try {
      service.removeSchedule(EMPLOYEE_KEY, FAKE_SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("EmployeeSchedule with id '" + FAKE_SCHEDULE_KEY + "' does not exist!", error);
  }
  
  // Note: tests for removeSchedules(String, long...) are not included because this method uses
  // removeSchedule(String, long) internally.

  // Tests for removeAllSchedules(String)
  // -----------------------------------
  // Note: tests for invalid usernames are not included because this method uses getEmployee(String)
  // internally, which throws errors for invalid username inputs.

  @Test
  public void testRemoveAllSchedules() {
    try {
      service.removeAllSchedules(EMPLOYEE_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(mockEmployee, times(1)).setEmployeeSchedules(new HashSet<EmployeeSchedule>());
  }

  // Tests for getEmployee(String)
  // -----------------------------

  @Test
  public void testGetEmployee() {
    Employee employee = null;
    try {
      employee = service.getEmployee(EMPLOYEE_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(employee);
    assertEquals(EMPLOYEE_KEY, employee.getUsername());
  }

  @Test
  public void testGetEmployeeNull() {
    Employee employee = null;
    String error = "";
    try {
      employee = service.getEmployee(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Employee username cannot be empty!", error);
  }

  @Test
  public void testGetEmployeeEmpty() {
    Employee employee = null;
    String error = "";
    try {
      employee = service.getEmployee("  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Employee username cannot be empty!", error);
  }

  @Test
  public void testGetEmployeeNonExistent() {
    Employee employee = null;
    String error = "";
    try {
      employee = service.getEmployee(FAKE_EMPLOYEE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employee);
    assertEquals("Employee with username \"" + FAKE_EMPLOYEE_KEY + "\" does not exist!", error);
  }

  // Tests for getAllEmployees()
  // ---------------------------

  @Test
  public void testGetAllEmployees() {
    List<Employee> employeeList = service.getAllEmployees();
    assertEquals(2, employeeList.size());
    assertEquals(EMPLOYEE_KEY, employeeList.get(0).getUsername());
    assertEquals(EMPLOYEE2_KEY, employeeList.get(1).getUsername());
  }

  // Tests for searchEmployeesAscending(String)
  // ------------------------------------------

  @Test
  public void testSearchEmployeesAscending() {
    // Both Mock Employees should appear in the list, in the order: one, two (Albert, Bernie)
    List<Employee> employeeList = null;
    try {
      employeeList = service.searchEmployeesAscending("be");
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(employeeList);
    assertEquals(2, employeeList.size());
    assertEquals(EMPLOYEE_KEY, employeeList.get(0).getUsername());
    assertEquals(EMPLOYEE2_KEY, employeeList.get(1).getUsername());
  }

  @Test
  public void testSearchEmployeesAscendingNull() {
    List<Employee> employeeList = null;
    String error = "";
    try {
      service.searchEmployeesAscending(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employeeList);
    assertEquals("Search Query must not be empty!", error);
  }

  @Test
  public void testSearchEmployeesAscendingEmpty() {
    List<Employee> employeeList = null;
    String error = "";
    try {
      service.searchEmployeesAscending("  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employeeList);
    assertEquals("Search Query must not be empty!", error);
  }

  // Tests for searchEmployeesDescending(String)
  // ------------------------------------------

  @Test
  public void testSearchEmployeesDescending() {
    // Both Mock Employees should appear in the list, in the order: two, one (Bernie, Albert)
    List<Employee> employeeList = null;
    try {
      employeeList = service.searchEmployeesDescending("be");
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(employeeList);
    assertEquals(2, employeeList.size());
    assertEquals(EMPLOYEE2_KEY, employeeList.get(0).getUsername());
    assertEquals(EMPLOYEE_KEY, employeeList.get(1).getUsername());
  }

  @Test
  public void testSearchEmployeesDescendingNull() {
    List<Employee> employeeList = null;
    String error = "";
    try {
      service.searchEmployeesDescending(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employeeList);
    assertEquals("Search Query must not be empty!", error);
  }

  @Test
  public void testSearchEmployeesDescendingEmpty() {
    List<Employee> employeeList = null;
    String error = "";
    try {
      service.searchEmployeesDescending("  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(employeeList);
    assertEquals("Search Query must not be empty!", error);
  }

}
