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
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeScheduleRepository;
import mcgill.ecse321.grocerystore.dao.OwnerRepository;
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;
import mcgill.ecse321.grocerystore.model.Owner;
import mcgill.ecse321.grocerystore.model.Shift;

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
  private Employee mockEmployeeOne;
  @Mock
  private Employee mockEmployeeTwo;
  @Mock
  private EmployeeSchedule mockScheduleOne;
  @Mock
  private EmployeeSchedule mockScheduleTwo;
  @Mock
  private EmployeeSchedule mockScheduleThree;
  @Mock
  private EmployeeSchedule existingMockScheduleOne;
  @Mock
  private EmployeeSchedule existingMockScheduleTwo;

  @InjectMocks
  private EmployeeService service;

  private static final String EMPLOYEE_KEY = "Albert";
  private static final String EMPLOYEE2_KEY = "Bernie";
  private static final String FAKE_EMPLOYEE_KEY = "NotAnEmployee";
  private static final String CUSTOMER_KEY = "Johnny";
  private static final String OWNER_KEY = "Cecil";

  private static final long SCHEDULE_KEY = 20l;
  private static final long SCHEDULE2_KEY = 21l;
  private static final long SCHEDULE3_KEY = 22l;
  private static final long EXISTING_SCHEDULE_KEY = 23l;
  private static final long EXISTING_SCHEDULE2_KEY = 24l;
  private static final long FAKE_SCHEDULE_KEY = 25l;

  @BeforeEach
  public void setMockOutput() {
    mockEmployeeDao();
    mockEmployee();
    mockSchedules();
    // imitate finding two EmployeeSchedule objects in the database
    lenient().when(employeeScheduleDao.findById(anyLong())).thenAnswer(i -> {
      if (i.getArgument(0).equals(SCHEDULE_KEY)) {
        return mockScheduleOne;
      } else if (i.getArgument(0).equals(SCHEDULE2_KEY)) {
        return mockScheduleTwo;
      } else if (i.getArgument(0).equals(SCHEDULE3_KEY)) {
        return mockScheduleThree;
      } else if (i.getArgument(0).equals(EXISTING_SCHEDULE_KEY)) {
        return existingMockScheduleOne;
      } else if (i.getArgument(0).equals(EXISTING_SCHEDULE2_KEY)) {
        return existingMockScheduleTwo;
      } else {
        return null;
      }
    });
    // added to allow username uniqueness check to operate, simulates finding a customer/owner in
    // the database
    lenient().when(customerDao.findByUsername(anyString())).thenAnswer(i -> {
      if (i.getArgument(0).equals(CUSTOMER_KEY)) {
        var customer = new Customer();
        customer.setUsername(CUSTOMER_KEY);
        return customer;
      }
      return null;
    });
    lenient().when(ownerDao.findByUsername(anyString())).thenAnswer(i -> {
      if (i.getArgument(0).equals(OWNER_KEY)) {
        var owner = new Owner();
        owner.setUsername(OWNER_KEY);
        return owner;
      }
      return null;
    });
  }

  /**
   * Imitates an employee table in the database with two employee rows: EMPLOYEE_KEY and
   * EMPLOYEE2_KEY
   */
  private void mockEmployeeDao() {
    // simulates finding EMPLOYEE_KEY in the database
    lenient().when(employeeDao.findByUsername(anyString())).thenAnswer(i -> {
      if (i.getArgument(0).equals(EMPLOYEE_KEY)) {
        return mockEmployeeOne;
      } else if (i.getArgument(0).equals(EMPLOYEE2_KEY)) {
        return mockEmployeeTwo;
      } else {
        return null;
      }
    });
    // simulates finding EMPLOYEE_KEY and EMPLOYEE2_KEY in the database
    lenient().when(employeeDao.findByUsernameIgnoreCaseContainingOrderByUsername(anyString()))
        .thenAnswer(i -> {
          List<Employee> employeeList = new ArrayList<Employee>();
          if (EMPLOYEE_KEY.toUpperCase().contains(((String) i.getArgument(0)).toUpperCase())) {
            var employee = new Employee();
            employee.setUsername(EMPLOYEE_KEY);
            employeeList.add(employee);
          }
          if (EMPLOYEE2_KEY.toUpperCase().contains(((String) i.getArgument(0)).toUpperCase())) {
            var employee = new Employee();
            employee.setUsername(EMPLOYEE2_KEY);
            employeeList.add(employee);
          }
          return employeeList;
        });
    lenient().when(employeeDao.findByUsernameIgnoreCaseContainingOrderByUsernameDesc(anyString()))
        .thenAnswer(i -> {
          List<Employee> employeeList = new ArrayList<Employee>();
          if (EMPLOYEE2_KEY.toUpperCase().contains(((String) i.getArgument(0)).toUpperCase())) {
            var employee = new Employee();
            employee.setUsername(EMPLOYEE2_KEY);
            employeeList.add(employee);
          }
          if (EMPLOYEE_KEY.toUpperCase().contains(((String) i.getArgument(0)).toUpperCase())) {
            var employee = new Employee();
            employee.setUsername(EMPLOYEE_KEY);
            employeeList.add(employee);
          }
          return employeeList;
        });
    // echo parameter back when calling .save()
    lenient().when(employeeDao.save(any(Employee.class))).thenAnswer(i -> {
      return i.getArgument(0);
    });
  }

  /**
   * Imitates employe objects with usernames EMPLOYEE_KEY and EMPLOYEE2_KEY
   */
  private void mockEmployee() {
    lenient().when(mockEmployeeOne.getUsername()).thenReturn(EMPLOYEE_KEY);
    lenient().when(mockEmployeeOne.addEmployeeSchedule(any(EmployeeSchedule.class)))
        .thenAnswer(i -> {
          return i.getArgument(0).equals(mockScheduleOne)
              || i.getArgument(0).equals(mockScheduleTwo);
        });
    lenient().when(mockEmployeeOne.removeEmployeeSchedule(any(EmployeeSchedule.class)))
        .thenAnswer(i -> {
          return i.getArgument(0).equals(existingMockScheduleOne)
              || i.getArgument(0).equals(existingMockScheduleTwo);
        });
    lenient().when(mockEmployeeOne.getEmployeeSchedules()).thenAnswer(i -> {
      HashSet<EmployeeSchedule> mockSet = new HashSet<EmployeeSchedule>();
      mockSet.add(existingMockScheduleOne);
      mockSet.add(existingMockScheduleTwo);
      return mockSet;
    });
    lenient().when(mockEmployeeTwo.getUsername()).thenReturn(EMPLOYEE2_KEY);
    lenient().when(mockEmployeeTwo.addEmployeeSchedule(any(EmployeeSchedule.class)))
        .thenReturn(true);
    lenient().when(mockEmployeeTwo.removeEmployeeSchedule(any(EmployeeSchedule.class)))
        .thenReturn(false);
    lenient().when(mockEmployeeTwo.getEmployeeSchedules()).thenReturn(null);
  }

  /**
   * imitates EmployeeSchedule objects with auto-generated ids
   */
  private void mockSchedules() {
    lenient().when(mockScheduleOne.getId()).thenReturn(SCHEDULE_KEY);
    lenient().when(mockScheduleOne.getDate()).thenReturn(Date.valueOf("2022-01-01"));
    lenient().when(mockScheduleOne.getShift()).thenAnswer(i -> {
      Shift mockShift = new Shift();
      mockShift.setName("mockShift1");
      return mockShift;
    });
    lenient().when(mockScheduleTwo.getId()).thenReturn(SCHEDULE2_KEY);
    lenient().when(mockScheduleTwo.getDate()).thenReturn(Date.valueOf("2022-01-02"));
    lenient().when(mockScheduleTwo.getShift()).thenAnswer(i -> {
      Shift mockShift = new Shift();
      mockShift.setName("mockShift2");
      return mockShift;
    });
    lenient().when(mockScheduleThree.getId()).thenReturn(SCHEDULE3_KEY);
    lenient().when(mockScheduleThree.getDate()).thenReturn(Date.valueOf("2022-01-03"));
    lenient().when(mockScheduleThree.getShift()).thenAnswer(i -> {
      Shift mockShift = new Shift();
      mockShift.setName("mockShift3");
      return mockShift;
    });
    lenient().when(existingMockScheduleOne.getId()).thenReturn(EXISTING_SCHEDULE_KEY);
    lenient().when(existingMockScheduleOne.getDate()).thenReturn(Date.valueOf("2022-01-03"));
    lenient().when(existingMockScheduleOne.getShift()).thenAnswer(i -> {
      Shift mockShift = new Shift();
      mockShift.setName("mockShift3");
      return mockShift;
    });
    lenient().when(existingMockScheduleTwo.getId()).thenReturn(EXISTING_SCHEDULE2_KEY);
    lenient().when(existingMockScheduleTwo.getDate()).thenReturn(Date.valueOf("2022-01-04"));
    lenient().when(existingMockScheduleTwo.getShift()).thenAnswer(i -> {
      Shift mockShift = new Shift();
      mockShift.setName("mockShift4");
      return mockShift;
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
  public void testCreateEmployeeExistingEmployee() {
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
  public void testCreateEmployeeExistingCustomer() {
    String username = CUSTOMER_KEY;
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
  public void testCreateEmployeeExistingOwner() {
    String username = OWNER_KEY;
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
    try {
      service.deleteEmployee(EMPLOYEE_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(employeeScheduleDao, times(1)).delete(existingMockScheduleOne);
    verify(employeeScheduleDao, times(1)).delete(existingMockScheduleTwo);
    verify(employeeDao, times(1)).delete(any(Employee.class));
  }

  @Test
  public void testDeleteEmployeeNonExistent() {
    String error = "";
    try {
      service.deleteEmployee(FAKE_EMPLOYEE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(employeeScheduleDao, times(0)).delete(any(EmployeeSchedule.class));
    verify(employeeDao, times(0)).delete(any(Employee.class));
    assertEquals("Employee with username \"" + FAKE_EMPLOYEE_KEY + "\" does not exist!", error);
  }

  // Tests for setEmployeeEmail(String, String)
  // ----------------------------------
  // Note: tests for invalid usernames are not included because this method uses getEmployee(String)
  // internally, which throws errors for invalid username inputs.

  @Test
  public void testSetEmployeeEmail() {
    try {
      service.setEmployeeEmail(EMPLOYEE_KEY, "new@email.com");
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(mockEmployeeOne, times(1)).setEmail(anyString());
  }

  @Test
  public void testSetEmployeeEmailSpaced() {
    String error = "";
    try {
      service.setEmployeeEmail(EMPLOYEE_KEY, "ne w@email.com");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).setEmail(anyString());
    assertEquals("Employee email is invalid!", error);
  }

  @Test
  public void testSetEmployeeEmailNoDomain() {
    String error = "";
    try {
      service.setEmployeeEmail(EMPLOYEE_KEY, "new@emailcom");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).setEmail(anyString());
    assertEquals("Employee email is invalid!", error);
  }

  @Test
  public void testSetEmployeeEmailNoSubject() {
    String error = "";
    try {
      service.setEmployeeEmail(EMPLOYEE_KEY, "@email.com");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).setEmail(anyString());
    assertEquals("Employee email is invalid!", error);
  }

  @Test
  public void testSetEmployeeEmailNoSecondaryDomain() {
    String error = "";
    try {
      service.setEmployeeEmail(EMPLOYEE_KEY, "new@.com");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).setEmail(anyString());
    assertEquals("Employee email is invalid!", error);
  }

  @Test
  public void testSetEmployeeEmailNoTopLevelDomain() {
    String error = "";
    try {
      service.setEmployeeEmail(EMPLOYEE_KEY, "new@email.");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).setEmail(anyString());
    assertEquals("Employee email is invalid!", error);
  }

  // Tests for setEmployeePassword(String, String)
  // ---------------------------------------------
  // Note: tests for invalid usernames are not included because this method uses getEmployee(String)
  // internally, which throws errors for invalid username inputs.

  @Test
  public void testSetEmployeePassword() {
    try {
      service.setEmployeePassword(EMPLOYEE_KEY, "newPassword");
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(mockEmployeeOne, times(1)).setPassword(anyString());
  }

  @Test
  public void testSetEmployeePasswordNull() {
    String error = "";
    try {
      service.setEmployeePassword(EMPLOYEE_KEY, null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).setPassword(anyString());
    assertEquals("Employee password cannot be empty!", error);
  }

  @Test
  public void testSetEmployeePasswordEmpty() {
    String error = "";
    try {
      service.setEmployeePassword(EMPLOYEE_KEY, "  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).setPassword(anyString());
    assertEquals("Employee password cannot be empty!", error);
  }

  // Tests for addSchedules(String, long...)
  // ---------------------------------------
  // Note: tests for invalid usernames are not included because this method uses getEmployee(String)
  // internally, which throws errors for invalid username inputs.

  @Test
  public void testAddSchedules() {
    try {
      service.addSchedules(EMPLOYEE_KEY, SCHEDULE_KEY, SCHEDULE2_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(mockEmployeeOne, times(2)).addEmployeeSchedule(any());
  }

  @Test
  public void testAddSchedulesFirst() {
    try {
      service.addSchedules(EMPLOYEE2_KEY, SCHEDULE_KEY, SCHEDULE2_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(mockEmployeeTwo, times(2)).addEmployeeSchedule(any());
  }

  @Test
  public void testAddSchedulesExisting() {
    String error = "";
    try {
      service.addSchedules(EMPLOYEE_KEY, EXISTING_SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).addEmployeeSchedule(any());
    assertEquals(
        "That schedule is already assigned to Employee with username \"" + EMPLOYEE_KEY + "\"!",
        error);
  }

  @Test
  public void testAddSchedulesIdentical() {
    String error = "";
    try {
      service.addSchedules(EMPLOYEE_KEY, SCHEDULE3_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).addEmployeeSchedule(any());
    assertEquals(
        "That schedule is already assigned to Employee with username \"" + EMPLOYEE_KEY + "\"!",
        error);
  }

  @Test
  public void testAddSchedulesNonExistent() {
    String error = "";
    try {
      service.addSchedules(EMPLOYEE_KEY, FAKE_SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(mockEmployeeOne, times(0)).addEmployeeSchedule(any());
    assertEquals("EmployeeSchedule with id '" + FAKE_SCHEDULE_KEY + "' does not exist!", error);
  }

  // Tests for removeSchedules(String, long...)
  // ---------------------------------------
  // Note: tests for invalid usernames are not included because this method uses getEmployee(String)
  // internally, which throws errors for invalid username inputs.

  @Test
  public void testRemoveSchedules() {
    try {
      service.removeSchedules(EMPLOYEE_KEY, EXISTING_SCHEDULE_KEY, EXISTING_SCHEDULE2_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(employeeScheduleDao, times(2)).delete(any());
    verify(mockEmployeeOne, times(2)).removeEmployeeSchedule(any());
  }

  @Test
  public void testRemoveSchedulesMissing() {
    String error = "";
    try {
      service.removeSchedules(EMPLOYEE_KEY, SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(employeeScheduleDao, times(0)).delete(existingMockScheduleOne);
    verify(mockEmployeeOne, times(1)).removeEmployeeSchedule(mockScheduleOne);
    assertEquals("EmployeeSchedule with id '" + SCHEDULE_KEY
        + "' could not be removed from Employee with username \"" + EMPLOYEE_KEY + "\"", error);
  }

  @Test
  public void testRemoveSchedulesNonExistent() {
    String error = "";
    try {
      service.removeSchedules(EMPLOYEE_KEY, FAKE_SCHEDULE_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(employeeScheduleDao, times(0)).delete(any());
    verify(mockEmployeeOne, times(0)).removeEmployeeSchedule(any());
    assertEquals("EmployeeSchedule with id '" + FAKE_SCHEDULE_KEY + "' does not exist!", error);
  }

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
    verify(employeeScheduleDao, times(2)).delete(any());
    verify(mockEmployeeOne, times(2)).removeEmployeeSchedule(any());
  }

  @Test
  public void testRemoveAllSchedulesNull() {
    try {
      // Try to remove all schedules from an Employee who never had an schedules added
      // i.e. employee.getEmployeeSchedules() == null
      service.removeAllSchedules(EMPLOYEE2_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(employeeScheduleDao, times(0)).delete(any());
    verify(mockEmployeeTwo, times(0)).removeEmployeeSchedule(any());
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
