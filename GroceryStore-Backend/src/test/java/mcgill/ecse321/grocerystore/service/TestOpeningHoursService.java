package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import java.sql.Time;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import mcgill.ecse321.grocerystore.dao.OpeningHoursRepository;
import mcgill.ecse321.grocerystore.model.OpeningHours;

@ExtendWith(MockitoExtension.class)
public class TestOpeningHoursService {
  @Mock
  private OpeningHoursRepository openingHoursDao;

  @InjectMocks
  private OpeningHoursService service;

  private static final String OPENINGHOURS_KEY = "Monday";
  private static final String NONEXISTING_KEY = "NotAOpeningHours";

  private static final Time START_KEY = Time.valueOf("12:00:00");
  private static final Time END_KEY = Time.valueOf("20:00:00");

  @BeforeEach
  public void setMockOutput() {
    lenient().when(openingHoursDao.findByDaysOfWeek(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(OPENINGHOURS_KEY)) {
            OpeningHours openingH = new OpeningHours();
            openingH.setDaysOfWeek(OPENINGHOURS_KEY);
            return openingH;
          } else {
            return null;
          }
        });

    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };
    lenient().when(openingHoursDao.save(any(OpeningHours.class)))
        .thenAnswer(returnParameterAsAnswer);
  }


  @Test
  public void testCreateOpeningHours() {
    String daysOfWeek = "Friday";
    Time startH = Time.valueOf("12:00:00");
    Time endH = Time.valueOf("20:00:00");
    OpeningHours openingH = null;
    try {
      openingH = service.createOpeningHours(daysOfWeek, startH, endH);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(openingH);
    assertEquals(daysOfWeek, openingH.getDaysOfWeek());
    assertEquals(startH, openingH.getStartTime());
    assertEquals(endH, openingH.getEndTime());
  }

  @Test
  public void testCreateOpeningHoursNullDaysOfWeek() {
    String daysOfWeek = null;
    Time startH = Time.valueOf("12:00:00");
    Time endH = Time.valueOf("20:00:00");
    OpeningHours openingH = null;
    String error = null;
    try {
      openingH = service.createOpeningHours(daysOfWeek, startH, endH);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("Day of week cannot be empty!", error);
  }

  @Test
  public void testCreateOpeningHoursEmptyDaysOfWeek() {
    String daysOfWeek = "  ";
    Time startH = Time.valueOf("12:00:00");
    Time endH = Time.valueOf("20:00:00");
    OpeningHours openingH = null;
    String error = null;
    try {
      openingH = service.createOpeningHours(daysOfWeek, startH, endH);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("Day of week cannot be empty!", error);
  }

  @Test
  public void testCreateOpeningHoursExistingOpeningHours() {
    Time startH = Time.valueOf("12:00:00");
    Time endH = Time.valueOf("20:00:00");
    OpeningHours openingH = null;
    String error = null;
    openingH = null;
    try {
      openingH = service.createOpeningHours(OPENINGHOURS_KEY, startH, endH);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("This opening hour already exist!", error);
  }

  @Test
  public void testCreateOpeningHoursNullStart() {
    String daysOfWeek = "Friday";
    Time startH = null;
    Time endH = Time.valueOf("20:00:00");
    OpeningHours openingH = null;
    String error = null;
    try {
      openingH = service.createOpeningHours(daysOfWeek, startH, endH);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("Start time cannot be empty!", error);
  }

  @Test
  public void testCreateOpeningHoursNullEnd() {
    String daysOfWeek = "Friday";
    Time startH = Time.valueOf("10:00:00");
    Time endH = null;
    OpeningHours openingH = null;
    String error = null;
    try {
      openingH = service.createOpeningHours(daysOfWeek, startH, endH);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("End time cannot be empty!", error);
  }

  @Test
  public void testCreateOpeningHoursEndBeforeStart() {
    String daysOfWeek = "Friday";
    Time startH = Time.valueOf("10:00:00");
    Time endH = Time.valueOf("09:00:00");
    OpeningHours openingH = null;
    String error = null;
    try {
      openingH = service.createOpeningHours(daysOfWeek, startH, endH);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("Start time must be earlier than end time!", error);
  }

  @Test
  public void testCreateOpeningHoursInvalidDaysOfWeek() {
    String daysOfWeek = "Robert";
    Time startH = Time.valueOf("09:00:00");
    Time endH = Time.valueOf("10:00:00");
    OpeningHours openingH = null;
    String error = null;
    try {
      openingH = service.createOpeningHours(daysOfWeek, startH, endH);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("Day of week is not valid!", error);
  }


  @Test
  public void testGetOpeningHours() {
    OpeningHours openingH = null;
    try {
      openingH = service.getOpeningHours(OPENINGHOURS_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(openingH);
    assertEquals(OPENINGHOURS_KEY, openingH.getDaysOfWeek());
  }

  @Test
  public void testGetNonExistingOpeningHours() {
    String error = null;
    try {
      service.getOpeningHours(NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("This opening hour does not exist!", error);
  }

  @Test
  public void testGetOpeningHoursNullDaysOfWeek() {
    String error = null;
    try {
      service.getOpeningHours(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Day of week cannot be empty!", error);
  }

  @Test
  public void testGetOpeningHoursEmptyDaysOfWeek() {
    String error = null;
    try {
      service.getOpeningHours("  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Day of week cannot be empty!", error);
  }

  @Test
  public void testUpdateOpeningHours() {
    OpeningHours openingH = null;
    try {
      openingH = service.updateOpeningHours(OPENINGHOURS_KEY, START_KEY, END_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(openingH);
    assertEquals(OPENINGHOURS_KEY, openingH.getDaysOfWeek());
  }

  @Test
  public void testUpdateOpeningHoursNullStartTime() {
    OpeningHours openingH = null;
    String error = "";
    try {
      openingH = service.updateOpeningHours(OPENINGHOURS_KEY, null, END_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("Start time cannot be empty!", error);
  }

  @Test
  public void testUpdateOpeningHoursNullEndTime() {
    OpeningHours openingH = null;
    String error = "";
    try {
      openingH = service.updateOpeningHours(OPENINGHOURS_KEY, START_KEY, null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("End time cannot be empty!", error);
  }

  @Test
  public void testUpdateOpeningHoursEndBeforeStart() {
    OpeningHours openingH = null;
    String error = "";
    try {
      openingH = service.updateOpeningHours(OPENINGHOURS_KEY, END_KEY, START_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(openingH);
    assertEquals("Start time must be earlier than end time!", error);
  }

  @Test
  public void testDeleteOpeningHours() {
    try {
      service.deleteOpeningHours(OPENINGHOURS_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(openingHoursDao).delete(any());
  }

}


