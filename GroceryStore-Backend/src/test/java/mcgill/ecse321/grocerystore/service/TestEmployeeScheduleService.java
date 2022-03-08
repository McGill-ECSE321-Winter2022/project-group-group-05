
/*
package mcgill.ecse321.grocerystore.service;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import java.sql.Date;
import java.sql.Time;
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
import mcgill.ecse321.grocerystore.dao.EmployeeScheduleRepository;
import mcgill.ecse321.grocerystore.dao.ShiftRepository;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;
import mcgill.ecse321.grocerystore.model.Shift;


@ExtendWith(MockitoExtension.class)

public class TestEmployeeScheduleService {

  @Mock
  private EmployeeScheduleRepository service;
  @Mock
  private ShiftRepository shiftDao;
  @Mock
  private EmployeeSchedule schedule;
  @Mock
  private Shift shift;




  @InjectMocks
  private ShiftService shiftService;


  private static final Date TEST_DATE1 = Date.valueOf("2020-12-20");
  private static final Date TEST_DATE2 = Date.valueOf("2020-12-30");
  private static final Long TEST_ID1=(long) 100 ;
  private static final Long TEST_ID2=(long) 200 ;

  @BeforeEach
  public void setMockOutput() {

    lenient().when(service.findById(anyLong()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(TEST_DATE1)) {
            EmployeeSchedule  aSchedule = new EmployeeSchedule();
            aSchedule.setId( TEST_ID1);
            aSchedule.setDate(TEST_DATE1);
          
            return aSchedule;
          } else {
            return null;
          }
        });
    lenient().when(service.findAll()).thenAnswer((InvocationOnMock invocation) -> {
      List <EmployeeSchedule> scheduleList = new ArrayList<EmployeeSchedule>();
      var scheduleOne = new EmployeeSchedule ();
      scheduleOne.setId(TEST_ID1);
      scheduleOne.setDate(TEST_DATE1);
      scheduleList.add( scheduleOne);
      var scheduleTwo = new EmployeeSchedule();
      scheduleOne.setId(TEST_ID2);
      scheduleOne.setDate(TEST_DATE2);
      scheduleList.add( scheduleTwo);
      return scheduleList;
    });

    lenient().when(service.findAllByDate(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          List<Shift> shiftList = new ArrayList<Shift>();
          if (TEST_NAME2.toUpperCase()
              .contains(((String) invocation.getArgument(0)).toUpperCase())) {
            var shift2 = new Shift();
            shift2.setName(TEST_NAME2);
            shiftList.add(shift2);
          }
          if (TEST_NAME1.toUpperCase()
              .contains(((String) invocation.getArgument(0)).toUpperCase())) {
            var shift1 = new Shift();
            shift1.setName(TEST_NAME1);
            shiftList.add(shift1);
          }
          return shiftList;
        });
    // shift
    lenient().when(shiftDao.findByNameIgnoreCaseContainingOrderByName(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          List<Shift> shiftList = new ArrayList<Shift>();
          if (TEST_NAME1.toUpperCase()
              .contains(((String) invocation.getArgument(0)).toUpperCase())) {
            var shift1 = new Shift();
            shift1.setName(TEST_NAME1);
            shiftList.add(shift1);
          }
          if (TEST_NAME2.toUpperCase()
              .contains(((String) invocation.getArgument(0)).toUpperCase())) {
            var shift2 = new Shift();
            shift2.setName(TEST_NAME2);
            shiftList.add(shift2);
          }

          return shiftList;
        });

    // Whenever anything is saved, just return the parameter object
    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };

    lenient().when(shiftDao.save(any(Shift.class))).thenAnswer(returnParameterAsAnswer);

  }

  
 
  @Test
  public void testCreateShift() {
    Shift shift = null;
    String name = "Testshift";
    Time startTime = Time.valueOf("14:00:00");
    Time endTime = Time.valueOf("15:00:00");



    try {
      shift = shiftService.createShift(name, startTime, endTime);
    } catch (Exception e) {
      fail();
    }
   
    assertNotNull(shift);
  }

  @Test
  public void testCreateShiftNull() {
    String name = null;
    Time startTime = Time.valueOf("14:00:00");
    Time endTime = Time.valueOf("15:00:00");
    String error = null;
    Shift shift = null;
    try {
      shift = shiftService.createShift(name, startTime, endTime);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }

    assertNull(shift);
    // check error
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testCreateShiftEmpty() {
    String name = "";
    Time startTime = Time.valueOf("14:00:00");
    Time endTime = Time.valueOf("15:00:00");
    String error = null;
    Shift shift = null;
    try {
      shift = shiftService.createShift(name, startTime, endTime);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(shift);
    // check error
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testCreateShiftSpaces() {
    String name = " ";
    Time startTime = Time.valueOf("14:00:00");
    Time endTime = Time.valueOf("15:00:00");
    String error = null;
    Shift shift = null;
    try {
      shift = shiftService.createShift(name, startTime, endTime);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }

    assertNull(shift);
    // check error
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testCreateShiftExisting() {
    String name = TEST_NAME1;
    Time startTime = Time.valueOf("14:00:00");
    Time endTime = Time.valueOf("15:00:00");

    Shift shift = null;
    String error = "";
    try {
     shift = shiftService.createShift(name, startTime,endTime);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(shift);
    assertEquals("Name is already taken.", error);
  }


  @Test
  public void testDeleteEmployeeNonExistent() {
    Shift shift = null;
    String error = "";
    try {
     shift = shiftService.deleteShiftByName(FAKE_NAME);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(shift);
    assertEquals("Shift with this name does not exist.", error);
  }


  @Test
  public void testDeleteShift() {
    Shift shift = new Shift();
    Shift shiftNew = new Shift();


    try {
      shiftNew = shiftService.deleteShift(shift);
    } catch (Exception e) {
      fail();
    }
    assertEquals(shift, shiftNew);
  }
  @Test
  public void testGetShift() {
    Shift shift = null;
    try {
      shift = shiftService.getShift(TEST_NAME1);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(shift);
    assertEquals(TEST_NAME1, shift.getName());
    assertEquals(TEST_START_TIME, shift.getStartTime());
    assertEquals(TEST_END_TIME, shift.getEndTime());
  }

  @Test
  public void testGetShiftNull() {
   Shift shift = null;
    String error = "";
    try {
     shift = this.shiftService.getShift(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(shift);
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testGetShiftEmpty() {
  Shift shift = null;
    String error = "";
    try {
     shift = this.shiftService.getShift(" ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(shift);
    assertEquals("Shift name cannot be empty.", error);
  }

  @Test
  public void testGetShiftExistent() {
  Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.getShift(FAKE_NAME);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(shift);
    assertEquals("Shift with this name does not exist.", error);
  }

  // Tests for getAllEmployees()
  // ---------------------------

  @Test
  public void testGetAllShifts() {
    List<Shift> shiftList = this.shiftService.getAllShifts();
    assertEquals(2, shiftList.size());
    assertEquals(TEST_NAME1, shiftList.get(0).getName());
    assertEquals(TEST_NAME2, shiftList.get(1).getName());
    
  }
  //utility method
  private void verifyTime(Shift shift) throws IllegalArgumentException {
    if (shift.getEndTime().before(TEST_START_TIME) ) {
      throw new IllegalArgumentException("Shift end time cannot be before its start time.");
    }
  }



}
*/



