package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mcgill.ecse321.grocerystore.model.Shift;

/**
 * Persistence Tests for Shift Class.
 * 
 * @author Harrison Wang
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestShiftPersistence {

  @Autowired
  private ShiftRepository shiftRepository;

  @BeforeEach
  public void clearDatabase() {
    shiftRepository.deleteAll();
  }

  // Test Shift Object
  @Test
  public void testPersistAndLoadShift() {
    // Create an instance of Shift and save it to database.
    String name = "TestShift";
    Shift testShift = new Shift();
    testShift.setName(name);
    shiftRepository.save(testShift);

    // Delete instance of Shift.
    testShift = null;

    // Fetch previously saved Employee.
    testShift = shiftRepository.findByName(name);

    // Check validity of attributes.
    assertNotNull(testShift);
    assertEquals(name, testShift.getName());
  }

  // Test Shift Attribute
  @Test
  public void testAttributeShift() {
    // Create an instance of Shift and save it to database.
    String name = "TestShift";
    Shift testShift = new Shift();
    testShift.setName(name);
    shiftRepository.save(testShift);

    // Change startTime.
    String newStartTime = "09:00:00";
    testShift.setStartTime(newStartTime);
    shiftRepository.save(testShift);

    // Reload testShift from database.
    testShift = null;
    testShift = shiftRepository.findByName(name);

    // Check that attribute was changed.
    assertEquals(newStartTime, testShift.getStartTime().toString());
  }

  // Shift does not contain a reference to any other classes.

}
