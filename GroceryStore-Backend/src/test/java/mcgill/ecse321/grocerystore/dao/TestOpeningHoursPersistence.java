package mcgill.ecse321.grocerystore.dao;

import java.sql.Time;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mcgill.ecse321.grocerystore.model.OpeningHours;

public class TestOpeningHoursPersistence {
  @ExtendWith(SpringExtension.class)
  @SpringBootTest
  @TestInstance(Lifecycle.PER_CLASS)
  public class TestOwnerPersistence {
    @Autowired
    private OwnerRepository openingHoursRepository;
    @BeforeEach
    @AfterEach
    public void clearDatabase() {
      openingHoursRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadOwner() {
      OpeningHours openingH = new OpeningHours();
      String daysOfWeek = "Monday";
      openingH.setDaysOfWeek(daysOfWeek);
      openingH.setStartTime(new Time(1200000000));
      openingH.setEndTime(new Time(1200000000));
      
    }
    }
}
