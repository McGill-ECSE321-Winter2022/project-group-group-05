package mcgill.ecse321.grocerystore.service;



import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
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


  private static final String TEST_NAME1 = "Testname";
  private static final String TEST_NAME2 = "Someone";
  private static final String FAKE_NAME = "Fakename";



  private static final Time TEST_START_TIME = Time.valueOf("11:00:00");
  private static final Time TEST_END_TIME = Time.valueOf("12:00:00");



  @BeforeEach
  public void setMockOutput() {

    lenient().when(shiftDao.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
      if (invocation.getArgument(0).equals(TEST_NAME1)) {
       
        return mockShift;
      } else {
        return null;
      }
    });
    lenient().when(shiftDao.findAll()).thenAnswer((InvocationOnMock invocation) -> {
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
   


    // shift
    lenient().when(shiftDao.findByNameIgnoreCaseContainingOrderByNameDesc(anyString()))
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
      shift = shiftService.createShift(name, startTime, endTime);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(shift);
    assertEquals("Name is already taken.", error);
  }


  
  @Test
  public void testCreateShiftTimeConflict() {
    String name = TEST_NAME1;
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
    assertEquals("Shift end time cannot be before its start time.", error);
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
  @Test
  public void testUpdateShift() {
  this.shiftService.updateShift(mockShift, FAKE_NAME, TEST_START_TIME, TEST_END_TIME);
  assertEquals( FAKE_NAME,mockShift.getName());
  assertEquals( TEST_START_TIME,mockShift.getStartTime());
  assertEquals( TEST_END_TIME,mockShift.getEndTime());
  }



}


