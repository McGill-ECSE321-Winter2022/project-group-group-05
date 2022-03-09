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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeScheduleRepository;
import mcgill.ecse321.grocerystore.dao.ShiftRepository;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;
import mcgill.ecse321.grocerystore.model.Shift;


@ExtendWith(MockitoExtension.class)

public class TestEmployeeScheduleService {

  @Mock
  private EmployeeScheduleRepository scheduleDao;
  @Mock
  private ShiftRepository shiftDao;
  @Mock
  private EmployeeRepository employeeDao;



  @InjectMocks
  private EmployeeScheduleService service;


  private static final Date TEST_DATE = Date.valueOf("2020-12-20");
  private static final Date TEST_DATE1 = Date.valueOf("2021-12-20");
  // private static final Date TEST_DATE2 = Date.valueOf("2022-12-30");
  private static final Date MOCK_DATE1 = Date.valueOf("2023-12-20");
  private static final Date MOCK_DATE2 = Date.valueOf("2024-12-30");
  private static final String TEST_SHIFT_NAME = "mockShiftName";
  private static final String TEST_SHIFT_NAME1 = "testShiftName";
  // private static final String MOCK_SHIFT_NAME = "MockShiftName";
  private static final String MOCK_SHIFT_NAME1 = "mockShiftName1";
  private static final String MOCK_SHIFT_NAME2 = "mockShiftName2";
  private static final String TEST_FAKE_SHIFT_NAME = "fakeShiftName";

  private static final Long TEST_ID = (long) 10;
  // private static final Long TEST_ID1 = (long) 100;
  // private static final Long TEST_ID2 = (long) 200;
  private static final Long MOCK_ID1 = (long) 1000;
  private static final Long MOCK_ID2 = (long) 2000;
  private static final Long TEST_FAKE_ID = (long) 8080;

  private static final String TEST_EMPLOYEE_NAME = "TEST";
  private static final String TEST_FAKE_EMPLOYEE_NAME = "FAKE";

  @BeforeEach
  public void setMockOutput() {

    lenient().when(scheduleDao.findById(anyLong())).thenAnswer((InvocationOnMock invocation) -> {
      if (invocation.getArgument(0).equals(TEST_ID)) {

        EmployeeSchedule mockSchedule = new EmployeeSchedule();
        Shift mockShift = new Shift();
        mockSchedule.setId(TEST_ID);
        mockSchedule.setDate(TEST_DATE);
        mockShift.setName(TEST_SHIFT_NAME);
        mockSchedule.setShift(mockShift);
        return mockSchedule;
      } else {
        return null;
      }
    });
    lenient().when(scheduleDao.findAllByOrderByDate()).thenAnswer((InvocationOnMock invocation) -> {
      List<EmployeeSchedule> scheduleList = new ArrayList<EmployeeSchedule>();
      EmployeeSchedule schedule1 = new EmployeeSchedule();
      EmployeeSchedule schedule2 = new EmployeeSchedule();
      Shift shift1 = new Shift();
      Shift shift2 = new Shift();
      schedule1.setId(MOCK_ID1);
      schedule1.setDate(MOCK_DATE1);

      shift1.setName(MOCK_SHIFT_NAME1);
      schedule1.setShift(shift1);
      scheduleList.add(schedule1);
      schedule2.setId(MOCK_ID2);
      schedule2.setDate(MOCK_DATE2);
      shift2.setName(MOCK_SHIFT_NAME2);
      schedule2.setShift(shift2);
      scheduleList.add(schedule2);

      return scheduleList;
    });
    lenient().when(shiftDao.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
      if (invocation.getArgument(0).equals(TEST_SHIFT_NAME)) {
        Shift mockShift = new Shift();
        mockShift.setName(TEST_SHIFT_NAME);
        return mockShift;
      } else if (invocation.getArgument(0).equals(TEST_SHIFT_NAME1)) {
        Shift mockShift1 = new Shift();
        mockShift1.setName(TEST_SHIFT_NAME1);
        return mockShift1;
      } else {
        return null;
      }
    });
    lenient().when(employeeDao.findByUsername(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(TEST_EMPLOYEE_NAME)) {
            Employee employee = new Employee();
            employee.setUsername(TEST_EMPLOYEE_NAME);
            Shift shift1 = new Shift();
            Shift shift2 = new Shift();
            EmployeeSchedule schedule1 = new EmployeeSchedule();
            EmployeeSchedule schedule2 = new EmployeeSchedule();
            schedule1.setId(MOCK_ID1);
            schedule1.setDate(MOCK_DATE1);
            shift1.setName(MOCK_SHIFT_NAME1);
            schedule1.setShift(shift1);
            schedule2.setId(MOCK_ID2);
            schedule2.setDate(MOCK_DATE2);
            shift2.setName(MOCK_SHIFT_NAME2);
            schedule2.setShift(shift2);
            employee.addEmployeeSchedule(schedule1);
            employee.addEmployeeSchedule(schedule2);
            return employee;
          } else {
            return null;
          }
        });
    lenient().when(employeeDao.findAll()).thenAnswer((InvocationOnMock invocation) -> {
      List<Employee> employeeList = new ArrayList<Employee>();
      Employee employee1 = new Employee();
      Employee employee2 = new Employee();
      Employee employee3 = new Employee();

      Shift shift1 = new Shift();
      EmployeeSchedule schedule1 = new EmployeeSchedule();
      schedule1.setId(MOCK_ID1);
      schedule1.setDate(MOCK_DATE1);
      shift1.setName(MOCK_SHIFT_NAME1);
      schedule1.setShift(shift1);


      employee1.addEmployeeSchedule(schedule1);
      employee2.addEmployeeSchedule(schedule1);
      employee3.addEmployeeSchedule(schedule1);

      return employeeList;


    });



    // Whenever anything is saved, just return the parameter object
    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };

    lenient().when(scheduleDao.save(any(EmployeeSchedule.class)))
        .thenAnswer(returnParameterAsAnswer);

  }



  @Test
  public void testCreateEmployeeSchedule() {
    EmployeeSchedule schedule = null;
    try {
      schedule = this.service.createEmployeeSchedule(TEST_DATE, TEST_SHIFT_NAME);
    } catch (Exception e) {
      fail();
    }
    // I think those assertchecks are enough?
    assertNotNull(schedule);
    assertNotNull(schedule.getShift());
    assertEquals(TEST_DATE, schedule.getDate());
    assertEquals(TEST_SHIFT_NAME, schedule.getShift().getName());
  }

  @Test
  public void testCreateScheduleDateNull() {

    EmployeeSchedule schedule = null;
    Date date = null;
    String error = "";
    try {
      schedule = this.service.createEmployeeSchedule(date, TEST_SHIFT_NAME1);
    } catch (Exception e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("Date cannot be empty.", error);
  }

  @Test
  public void testCreateScheduleShiftNull() {

    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.createEmployeeSchedule(TEST_DATE1, null);
    } catch (Exception e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testCreateScheduleShiftEmpty() {

    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.createEmployeeSchedule(TEST_DATE1, "");
    } catch (Exception e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testCreateScheduleShiftSpaces() {

    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.createEmployeeSchedule(TEST_DATE1, " ");
    } catch (Exception e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("Shift name cannot be empty.", error);
  }


  @Test
  public void testCreateScheduleShiftNonExistent() {

    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.createEmployeeSchedule(TEST_DATE1, TEST_FAKE_SHIFT_NAME);
    } catch (Exception e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("Shift with name '" + TEST_FAKE_SHIFT_NAME + "' does not exist.", error);
  }

  @Test
  public void testGetEmployeeSchedule() {
    EmployeeSchedule schedule = null;
    try {
      schedule = this.service.getEmployeeSchedule(TEST_ID);
    } catch (Exception e) {
      fail();
    }
    assertNotNull(schedule);
    assertNotNull(schedule.getShift());
    assertEquals(TEST_ID, schedule.getId());
    assertEquals(TEST_DATE, schedule.getDate());
    assertEquals(TEST_DATE, schedule.getDate());
    assertEquals(TEST_SHIFT_NAME, schedule.getShift().getName());
  }



  @Test
  public void testGetEmployeeScheduleNonExistent() {
    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.getEmployeeSchedule(TEST_FAKE_ID);
    } catch (Exception e) {
      error = e.getMessage();
    }
    assertNull(schedule);
    assertEquals("EmployeeSchedule with id '" + TEST_FAKE_ID + "' does not exist.", error);
  }



  @Test
  public void testGetAllEmployeeSchedule() {
    List<EmployeeSchedule> scheduleList = this.service.getAllSchedulesOrderedByDate();
    assertEquals(2, scheduleList.size());
    assertEquals(MOCK_ID1, scheduleList.get(0).getId());
    assertEquals(MOCK_ID2, scheduleList.get(1).getId());
    assertEquals(MOCK_DATE1, scheduleList.get(0).getDate());
    assertEquals(MOCK_DATE2, scheduleList.get(1).getDate());
    assertEquals(MOCK_SHIFT_NAME1, scheduleList.get(0).getShift().getName());
    assertEquals(MOCK_SHIFT_NAME2, scheduleList.get(1).getShift().getName());


  }

  @Test
  public void testGetAllScheduleByEmployee() {
    List<EmployeeSchedule> scheduleList = null;
    try {
      scheduleList = this.service.getAllScheduleByEmployee(TEST_EMPLOYEE_NAME);
      assertEquals(2, scheduleList.size());
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(scheduleList);
    assertEquals(2, scheduleList.size());
  }

  @Test
  public void testGetAllScheduleByEmployeeNull() {
    List<EmployeeSchedule> scheduleList = null;
    String error = "";
    try {
      scheduleList = this.service.getAllScheduleByEmployee(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(scheduleList);
    assertEquals("Employee username cannot be empty!", error);
  }

  @Test
  public void testGetAllScheduleByEmployeeEmpty() {
    List<EmployeeSchedule> scheduleList = null;
    String error = "";
    try {
      scheduleList = this.service.getAllScheduleByEmployee("");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(scheduleList);
    assertEquals("Employee username cannot be empty!", error);
  }

  @Test
  public void testGetAllScheduleByEmployeeSpaces() {
    List<EmployeeSchedule> scheduleList = null;
    String error = "";
    try {
      scheduleList = this.service.getAllScheduleByEmployee(" ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(scheduleList);
    assertEquals("Employee username cannot be empty!", error);
  }

  @Test
  public void testGetAllScheduleByEmployeeNonExistent() {
    List<EmployeeSchedule> scheduleList = null;
    String error = "";
    try {
      scheduleList = this.service.getAllScheduleByEmployee(TEST_FAKE_EMPLOYEE_NAME);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(scheduleList);
    assertEquals("Employee with username '" + TEST_FAKE_EMPLOYEE_NAME + "' does not exist!", error);
  }

  @Test
  public void testUpdateEmployeeSchedule() {
    EmployeeSchedule schedule = null;
    try {
      schedule = this.service.updateEmployeeSchedule(TEST_ID, TEST_SHIFT_NAME1, TEST_DATE1);

    } catch (IllegalArgumentException e) {
      fail();
    }

    assertNotNull(schedule);
    assertEquals(TEST_ID, schedule.getId());
    assertEquals(TEST_DATE1, schedule.getDate());
    assertEquals(TEST_SHIFT_NAME1, schedule.getShift().getName());


  }

  @Test
  public void testUpdateScheduleDateNull() {
    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.updateEmployeeSchedule(TEST_ID, TEST_SHIFT_NAME1, null);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("Date cannot be empty.", error);
  }

  @Test
  public void testUpdateScheduleNonExistent() {
    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.updateEmployeeSchedule(TEST_FAKE_ID, TEST_SHIFT_NAME1, TEST_DATE1);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("EmployeeSchedule with id '" + TEST_FAKE_ID + "' does not exist.", error);
  }

  @Test
  public void testUpdateScheduleShiftNull() {
    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.updateEmployeeSchedule(TEST_ID, null, TEST_DATE1);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testUpdateScheduleShiftEmpty() {
    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.updateEmployeeSchedule(TEST_ID, "", TEST_DATE1);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testUpdateScheduleShiftSpaces() {
    EmployeeSchedule schedule = null;
    String error = "";
    try {
      schedule = this.service.updateEmployeeSchedule(TEST_ID, " ", TEST_DATE1);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }

    assertNull(schedule);
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testDeleteEmployeeSchedule() {

    // String error = "";
    try {
      this.service.deleteEmployeeSchedule(TEST_ID);

    } catch (IllegalArgumentException e) {
      fail();
    }
    // verify that the schedule is deleted from the Dao
    verify(this.scheduleDao, times(1)).delete(any());
    // assertNull(schedule);
  }



}
