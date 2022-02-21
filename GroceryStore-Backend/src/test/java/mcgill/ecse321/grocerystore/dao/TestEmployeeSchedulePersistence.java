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
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;
import mcgill.ecse321.grocerystore.model.Shift;

/**
 * Persistence Tests for EmployeeSchedule Class.
 * 
 * @author Harrison Wang
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestEmployeeSchedulePersistence {

  @Autowired
  private EmployeeScheduleRepository employeeScheduleRepository;
  @Autowired
  private ShiftRepository shiftRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    employeeScheduleRepository.deleteAll();
    shiftRepository.deleteAll();
  }

  // Test EmployeeSchedule Object
  @Test
  public void testPersistAndLoadEmployeeSchedule() {
    // Create Test Stub Instance of Shift.
    String shiftStubName = "testShiftStub";
    Shift shiftStub = new Shift();
    shiftStub.setName(shiftStubName);
    shiftRepository.save(shiftStub);

    // Create Instance of EmployeeSchedule and save it to database.
    EmployeeSchedule testSchedule = new EmployeeSchedule();
    testSchedule.setShift(shiftStub);
    employeeScheduleRepository.save(testSchedule);
    long testId = testSchedule.getId();

    // Delete Instance of EmployeeSchedule.
    testSchedule = null;

    // Fetch previously saved EmployeeSchedule.
    testSchedule = employeeScheduleRepository.findById(testId);

    // Check validity of attributes.
    assertNotNull(testSchedule);
    assertEquals(testId, testSchedule.getId());
  }

  // Test EmployeeSchedule Attribute
  @Test
  public void testAttributeEmployeeSchedule() {
    // Create Test Stub Instance of Shift.
    String shiftStubName = "testShiftStub";
    Shift shiftStub = new Shift();
    shiftStub.setName(shiftStubName);
    shiftRepository.save(shiftStub);

    // Create Instance of EmployeeSchedule and save it to database.
    EmployeeSchedule testSchedule = new EmployeeSchedule();
    testSchedule.setShift(shiftStub);
    employeeScheduleRepository.save(testSchedule);
    long testId = testSchedule.getId();

    // Change date.
    String newDate = "2000-03-15";
    testSchedule.setDate(newDate);
    employeeScheduleRepository.save(testSchedule);

    // Reload testSchedule from database.
    testSchedule = null;
    testSchedule = employeeScheduleRepository.findById(testId);

    // Check that attribute was changed.
    assertEquals(newDate, testSchedule.getDate().toString());
  }

  /*
   * Test EmployeeSchedule Reference
   * 
   * Because the reference to shift in EmployeeSchedule is immutable as defined by the Class
   * Diagram, testing modifications to this reference is irrelevant. Instead, this method ensures
   * that changes made to an EmployeSchedule's shift reference are preserved in the database.
   */
  @Test
  public void testReferenceEmployeeSchedule() {
    // Create Test Stub Instances of Shift.
    String shiftStubName = "testShiftStub";
    Shift shiftStub = new Shift();
    shiftStub.setName(shiftStubName);
    shiftRepository.save(shiftStub);

    // Create Instance of EmployeeSchedule and save it to database.
    EmployeeSchedule testSchedule = new EmployeeSchedule();
    testSchedule.setShift(shiftStub);
    employeeScheduleRepository.save(testSchedule);
    long testId = testSchedule.getId();

    // Change Shift reference.
    String startTime = "08:00:00";
    shiftStub.setStartTime(startTime);
    shiftRepository.save(shiftStub);

    // Reload testSchedule.
    testSchedule = null;
    testSchedule = employeeScheduleRepository.findById(testId);

    // Verify that the change to shiftStub was preserved
    assertNotNull(testSchedule.getShift());
    assertEquals(startTime, testSchedule.getShift().getStartTime().toString());


  }

}
