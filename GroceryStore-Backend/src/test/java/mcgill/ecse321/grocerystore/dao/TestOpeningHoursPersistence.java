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
import mcgill.ecse321.grocerystore.model.OpeningHours;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestOpeningHoursPersistence {
  @Autowired
  private OpeningHoursRepository openingHoursRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    openingHoursRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadOpeningHours() {
    OpeningHours openingH = new OpeningHours();
    String daysOfWeek = "Monday";
    String startH = "09:00:00";
    String endH = "22:00:00";
    openingH.setDaysOfWeek(daysOfWeek);
    openingH.setStartTime(startH);
    openingH.setEndTime(endH);
    openingHoursRepository.save(openingH);

    openingH = null;

    openingH = openingHoursRepository.findByDaysOfWeek(daysOfWeek);
    assertNotNull(openingH);
    assertEquals(daysOfWeek, openingH.getDaysOfWeek());
    assertEquals(startH, openingH.getStartTime().toString());
    assertEquals(endH, openingH.getEndTime().toString());
  }

  @Test
  public void testAtributeOpeningHours() {
    OpeningHours openingH = new OpeningHours();
    String daysOfWeek = "Monday";
    openingH.setDaysOfWeek(daysOfWeek);
    openingH.setStartTime("08:00:00");
    openingH.setEndTime("21:00:00");
    openingHoursRepository.save(openingH);

    openingH = null;

    String startH = "09:00:00";
    String endH = "22:00:00";
    openingH = openingHoursRepository.findByDaysOfWeek(daysOfWeek);
    openingH.setDaysOfWeek(daysOfWeek);
    openingH.setStartTime(startH);
    openingH.setEndTime(endH);
    openingHoursRepository.save(openingH);

    openingH = null;

    openingH = openingHoursRepository.findByDaysOfWeek(daysOfWeek);
    assertEquals(daysOfWeek, openingH.getDaysOfWeek());
    assertEquals(startH, openingH.getStartTime().toString());
    assertEquals(endH, openingH.getEndTime().toString());
  }
}
