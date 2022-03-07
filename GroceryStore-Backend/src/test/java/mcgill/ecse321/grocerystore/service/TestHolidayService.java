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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import mcgill.ecse321.grocerystore.dao.HolidayRepository;
import mcgill.ecse321.grocerystore.model.Holiday;


@ExtendWith(MockitoExtension.class)
public class TestHolidayService {
  @Mock
  private HolidayRepository holidayDao;

  @InjectMocks
  private HolidayService service;

  private static final String HOLIDAY_KEY = "TestHoliday";
  private static final String NONEXISTING_KEY = "NotAHoliday";

  @BeforeEach
  public void setMockOutput() {
    lenient().when(holidayDao.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
      if (invocation.getArgument(0).equals(HOLIDAY_KEY)) {
        Holiday holiday = new Holiday();;
        holiday.setName(HOLIDAY_KEY);
        return holiday;
      } else {
        return null;
      }
    });

    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };
    lenient().when(holidayDao.save(any(Holiday.class))).thenAnswer(returnParameterAsAnswer);
  }

  @Test
  public void testCreateHoliday() {
    String name = "Christmas";
    int month = 12;
    int day = 25;
    Holiday holiday = null;
    try {
      holiday = service.createHoliday(name, month, day);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(holiday);
    assertEquals(name, holiday.getName());
    assertEquals(month, holiday.getMonth());
    assertEquals(day, holiday.getDay());
  }

  @Test
  public void testCreateHolidayNullName() {
    String name = null;
    int month = 12;
    int day = 25;
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, month, day);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testCreateHolidayEmptyName() {
    String name = "";
    int month = 12;
    int day = 25;
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, month, day);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testCreateHolidaySpacesName() {
    String name = "  ";
    int month = 12;
    int day = 25;
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, month, day);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testCreateHolidayExistingHoliday() {
    int month = 12;
    int day = 25;
    Holiday holiday = null;
    String error = null;
    holiday = null;
    try {
      holiday = service.createHoliday(HOLIDAY_KEY, month, day);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Holiday already exists!", error);
  }

  @Test
  public void testCreateHolidayZeroMonth() {
    String name = "Christmas";
    int month = 0;
    int day = 25;
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, month, day);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Month is out of range!", error);
  }

  @Test
  public void testCreateHolidayLargeMonth() {
    String name = "Christmas";
    int month = 88;
    int day = 25;
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, month, day);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Month is out of range!", error);
  }

  @Test
  public void testCreateHolidayZeroDay() {
    String name = "Christmas";
    int month = 12;
    int day = 0;
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, month, day);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Day is out of range!", error);
  }

  @Test
  public void testCreateHolidayLargeDay() {
    String name = "Christmas";
    int month = 12;
    int day = 88;
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, month, day);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Day is out of range!", error);
  }

  @Test
  public void testDeleteHoliday() {
    try {
      service.deleteHoliday(HOLIDAY_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(holidayDao).delete(any());
  }

  @Test
  public void testDeleteNullHoliday() {
    String error = null;
    try {
      service.deleteHoliday(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(holidayDao, times(0)).delete(any());
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testDeleteEmptyHoliday() {
    String error = null;
    try {
      service.deleteHoliday("");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(holidayDao, times(0)).delete(any());
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testDeleteSpacesHoliday() {
    String error = null;
    try {
      service.deleteHoliday("  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(holidayDao, times(0)).delete(any());
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testDeleteNonExistingHoliday() {
    String error = null;
    try {
      service.deleteHoliday(NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(holidayDao, times(0)).delete(any());
    assertEquals("Holiday does not exist!", error);
  }



}


