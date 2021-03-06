package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import java.sql.Date;
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

  private static final Date DATE_KEY = Date.valueOf("2022-02-02");

  @BeforeEach
  public void setMockOutput() {
    lenient().when(holidayDao.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
      if (invocation.getArgument(0).equals(HOLIDAY_KEY)) {
        Holiday holiday = new Holiday();
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
    Date date = Date.valueOf("2022-02-02");
    Holiday holiday = null;
    try {
      holiday = service.createHoliday(name, date);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(holiday);
    assertEquals(name, holiday.getName());
    assertEquals(date, holiday.getDate());
  }

  @Test
  public void testCreateHolidayNullName() {
    String name = null;
    Date date = Date.valueOf("2022-02-02");
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, date);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testCreateHolidayEmptyName() {
    String name = "  ";
    Date date = Date.valueOf("2022-02-02");
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, date);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testCreateHolidayExistingHoliday() {
    Date date = Date.valueOf("2022-02-02");
    Holiday holiday = null;
    String error = null;
    holiday = null;
    try {
      holiday = service.createHoliday(HOLIDAY_KEY, date);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Holiday already exists!", error);
  }

  @Test
  public void testCreateHolidayNullDate() {
    String name = "Christmas";
    Date date = null;
    Holiday holiday = null;
    String error = null;
    try {
      holiday = service.createHoliday(name, date);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Date cannot be empty!", error);
  }

  @Test
  public void testGetHoliday() {
    Holiday holiday = null;
    try {
      holiday = service.getHoliday(HOLIDAY_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(holiday);
    assertEquals(HOLIDAY_KEY, holiday.getName());
  }

  @Test
  public void testGetNonExistingHoliday() {
    String error = null;
    try {
      service.getHoliday(NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Holiday does not exist!", error);
  }

  @Test
  public void testGetHolidayNullName() {
    String error = null;
    try {
      service.getHoliday(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testGetHolidayEmptyName() {
    String error = null;
    try {
      service.getHoliday("  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Name cannot be empty!", error);
  }

  @Test
  public void testUpdateHoliday() {
    Holiday holiday = null;
    try {
      holiday = service.updateHoliday(HOLIDAY_KEY, DATE_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(holiday);
    assertEquals(HOLIDAY_KEY, holiday.getName());
  }

  @Test
  public void testUpdateHolidayNullDate() {
    Holiday holiday = null;
    String error = "";
    try {
      holiday = service.updateHoliday(HOLIDAY_KEY, null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(holiday);
    assertEquals("Date cannot be empty!", error);
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

}


