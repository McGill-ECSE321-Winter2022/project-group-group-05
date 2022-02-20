package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mcgill.ecse321.grocerystore.model.Holiday;


/**
 * Persistence Tests for Holiday Class.
 * 
 * @author Oliver Sibo Huang
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestHolidayPersistence {

  @Autowired
  private HolidayRepository holidayRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    holidayRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadHoliday() {
    Holiday testHoliday = new Holiday();
    String name = "Christmas";
    int month = 12;
    int day = 25;
    testHoliday.setName(name);
    testHoliday.setMonth(month);
    testHoliday.setDay(day);
    holidayRepository.save(testHoliday);

    testHoliday = null;

    testHoliday = holidayRepository.findByName(name);
    assertNotNull(testHoliday);
    assertEquals(name, testHoliday.getName());
    assertEquals(month, testHoliday.getMonth());
    assertEquals(day, testHoliday.getDay());

  }


  @Test
  public void testAtributeHoliday() {
    Holiday testHoliday = new Holiday();
    String name = "Christmas";
    testHoliday.setName(name);
    testHoliday.setMonth(11);
    testHoliday.setDay(24);
    holidayRepository.save(testHoliday);

    testHoliday = null;

    int month = 12;
    int day = 25;
    testHoliday = holidayRepository.findByName(name);
    testHoliday.setName(name);
    testHoliday.setMonth(month);
    testHoliday.setDay(day);
    holidayRepository.save(testHoliday);

    testHoliday = null;

    testHoliday = holidayRepository.findByName(name);
    assertEquals(name, testHoliday.getName());
    assertEquals(month, testHoliday.getMonth());
    assertEquals(day, testHoliday.getDay());


  }

}
