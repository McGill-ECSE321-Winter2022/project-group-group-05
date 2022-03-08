package mcgill.ecse321.grocerystore.service;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import mcgill.ecse321.grocerystore.dao.ShiftRepository;
import mcgill.ecse321.grocerystore.model.Shift;


@ExtendWith(MockitoExtension.class)

public class TestShiftService {

  @Mock
  private ShiftRepository shiftDao;
  @Mock
  private Shift mockShift;



  @InjectMocks
  private ShiftService shiftService;

  private static final String TEST_NAME = "Test";
  private static final String TEST_NAME1 = "Testname";
  private static final String TEST_NAME2 = "Someone";
  private static final String FAKE_NAME = "Fakename";



  private static final Time TEST_START_TIME = Time.valueOf("11:00:00");
  private static final Time TEST_END_TIME = Time.valueOf("12:00:00");
  private static final Time TEST_START_TIME1 = Time.valueOf("13:00:00");
  private static final Time TEST_END_TIME1 = Time.valueOf("14:00:00");;


  @BeforeEach
  public void setMockOutput() {
    mockShift.setName(TEST_NAME);
    lenient().when(shiftDao.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
      if (invocation.getArgument(0).equals(TEST_NAME)) {

        Shift shift = new Shift();
        shift.setName(TEST_NAME);
        shift.setStartTime(TEST_START_TIME);
        shift.setEndTime(TEST_END_TIME);
        return shift;
      } else {
        return null;
      }
    });
    lenient().when(shiftDao.findAllByOrderByName()).thenAnswer((InvocationOnMock invocation) -> {
      List<Shift> shiftList = new ArrayList<Shift>();
      var shiftOne = new Shift();
      shiftOne.setName(TEST_NAME1);
      shiftOne.setStartTime(TEST_START_TIME);
      shiftOne.setEndTime(TEST_END_TIME);
      var shiftTwo = new Shift();
      shiftTwo.setName(TEST_NAME2);
      shiftTwo.setStartTime(TEST_START_TIME);
      shiftTwo.setEndTime(TEST_END_TIME);
      shiftList.add(shiftOne);
      shiftList.add(shiftTwo);
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
    assertEquals("Shift name cannot be empty!", error);
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
    assertEquals("Shift name cannot be empty!", error);
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
    assertEquals("Shift name cannot be empty!", error);
  }

  @Test
  public void testCreateShiftExisting() {
    String name = TEST_NAME;
    Time startTime = Time.valueOf("14:00:00");
    Time endTime = Time.valueOf("15:00:00");

    Shift shift = null;
    String error = "";
    try {
      shift = shiftService.createShift(name, startTime, endTime);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(shift);
    assertEquals("Name is already taken!", error);
  }



  @Test
  public void testCreateShiftTimeConflict() {
    String name = "TestName5";
    Time startTime = Time.valueOf("15:00:00");
    Time endTime = Time.valueOf("14:00:00");

    Shift shift = null;
    String error = "";
    try {
      shift = shiftService.createShift(name, startTime, endTime);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(shift);
    assertEquals("Shift end-time cannot be before its start-time!", error);
  }



  @Test
  public void testDeleteShift() {
    Shift shift = null;
    try {
      shift = this.shiftService.deleteShiftByName(TEST_NAME);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(shiftDao, times(1)).deleteById(anyString());
    assertNotNull(shift);
    assertEquals(TEST_NAME, shift.getName());
  }

  @Test
  public void testDeleteShiftNull() {
    Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.deleteShiftByName(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(shiftDao, times(0)).deleteById(anyString());
    assertNull(shift);
    assertEquals("Shift name cannot be empty!", error);
  }
  @Test
  public void testDeleteShiftEmpty() {
    Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.deleteShiftByName("");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(shiftDao, times(0)).deleteById(anyString());
    assertNull(shift);
    assertEquals("Shift name cannot be empty!", error);
  }
  @Test
  public void testDeleteShiftSpaces() {
    Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.deleteShiftByName(" ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(shiftDao, times(0)).deleteById(anyString());
    assertNull(shift);
    assertEquals("Shift name cannot be empty!", error);
  }
  @Test
  public void testDeleteShiftNonExistent() {
    Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.deleteShiftByName(FAKE_NAME);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(shiftDao, times(0)).deleteById(anyString());
    assertNull(shift);
    assertEquals("Shift with name '" + FAKE_NAME + "' does not exist!", error);
  }



  @Test
  public void testGetShift() {
    Shift shift = null;
    try {
      shift = shiftService.getShift(TEST_NAME);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(shift);
    assertEquals(TEST_NAME, shift.getName());
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
    assertEquals("Shift name cannot be empty!", error);
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
    assertEquals("Shift name cannot be empty!", error);
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
    assertEquals("Shift with name '" + FAKE_NAME + "' does not exist!", error);
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

  @Test
  public void testUpdateShift() {
    Shift shift = null;
    try {
      shift = this.shiftService.updateShift(TEST_NAME, TEST_START_TIME1, TEST_END_TIME1);

    } catch (IllegalArgumentException e) {
      fail();
    }

    verify(shiftDao, times(0)).deleteById(anyString());
    assertNotNull(shift);
    assertEquals(TEST_NAME, shift.getName());
    assertEquals(TEST_START_TIME1, shift.getStartTime());
    assertEquals(TEST_END_TIME1, shift.getEndTime());


  }

  @Test
  public void testUpdateShiftNull() {
    Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.updateShift(null, TEST_START_TIME1, TEST_END_TIME1);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(shiftDao, times(0)).deleteById(anyString());
    assertNull(shift);
    assertEquals("Shift name cannot be empty!", error);

  }
  @Test
  public void testUpdateShiftEmpty() {
    Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.updateShift("", TEST_START_TIME1, TEST_END_TIME1);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(shiftDao, times(0)).deleteById(anyString());
    assertNull(shift);
    assertEquals("Shift name cannot be empty!", error);

  }
  @Test
  public void testUpdateShiftSpaces() {
    Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.updateShift(" ", TEST_START_TIME1, TEST_END_TIME1);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(shiftDao, times(0)).deleteById(anyString());
    assertNull(shift);
    assertEquals("Shift name cannot be empty!", error);

  }
  @Test
  public void testUpdateShiftNonExistent() {
    Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.updateShift(FAKE_NAME, TEST_START_TIME1, TEST_END_TIME1);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(shiftDao, times(0)).deleteById(anyString());
    assertNull(shift);
    assertEquals("Shift with name '" + FAKE_NAME + "' does not exist!", error);

  }
  @Test
  public void testUpdateShiftTimeConflict() {
    Shift shift = null;
    String error = "";
    try {
      shift = this.shiftService.updateShift(TEST_NAME, TEST_END_TIME1,TEST_START_TIME1);

    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(shiftDao, times(0)).deleteById(anyString());
    assertNull(shift);
    assertEquals("Shift end-time cannot be before its start-time!", error);

  }
  
 /* @Test
  public void testGetShiftBefore() {
    List<Shift> shiftList= null;
   
    try {
      this.shiftService.createShift(TEST_NAME1, TEST_START_TIME, TEST_END_TIME);
      this.shiftService.createShift(TEST_NAME2, TEST_START_TIME1, TEST_END_TIME1);
      shiftList=shiftService.getAllShiftsBefore(TEST_END_TIME1);

    } catch (IllegalArgumentException e) {
      fail();
    }
  
    assertNotNull(shiftList);
    assertEquals(2, shiftList.size());

  }
  @Test
  public void testGetShiftAfter() {
    List<Shift> shiftList= null;
   
    try {
      this.shiftService.createShift(TEST_NAME1, TEST_START_TIME, TEST_END_TIME);
      this.shiftService.createShift(TEST_NAME2, TEST_START_TIME1, TEST_END_TIME1);
      shiftList=shiftService.getAllShiftsAfter(TEST_START_TIME);

    } catch (IllegalArgumentException e) {
      fail();
    }
  
    assertNotNull(shiftList);
    System.out.println(shiftList.get(0).getName());
    assertEquals(2, shiftList.size());

  }
  */
 



}


