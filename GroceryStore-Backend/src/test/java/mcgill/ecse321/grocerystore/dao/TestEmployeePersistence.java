package mcgill.ecse321.grocerystore.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.EmployeeSchedule;
import mcgill.ecse321.grocerystore.model.Shift;

/**
 * Persistence Tests for Employee Class.
 * 
 * @author Harrison Wang
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestEmployeePersistence {

  @Autowired
  private EmployeeRepository employeeRepository;
  @Autowired
  private EmployeeScheduleRepository employeeScheduleRepository;
  @Autowired
  private ShiftRepository shiftRepository;

  @BeforeEach
  public void clearDatabase() {
    employeeRepository.deleteAll();
    employeeScheduleRepository.deleteAll();
    shiftRepository.deleteAll();
  }

  // Test Employee Object
  @Test
  public void testPersistAndLoadEmployee() {
    // Create an instance of Employee and save it to database.
    String username = "TestEmployee";
    Employee testEmployee = new Employee();
    testEmployee.setUsername(username);
    employeeRepository.save(testEmployee);

    // Delete instance of Employee.
    testEmployee = null;

    // Fetch previously saved Employee.
    testEmployee = employeeRepository.findByUsername(username);

    // Check validity of attributes.
    assertNotNull(testEmployee);
    assertEquals(username, testEmployee.getUsername());
  }

  // Test Employee Attribute
  @Test
  public void testAttributeEmployee() {
    // Create an instance of Employee and save it to database.
    String username = "TestEmployee";
    Employee testEmployee = new Employee();
    testEmployee.setUsername(username);
    employeeRepository.save(testEmployee);

    // Change email.
    String newEmail = "newTestEmail";
    testEmployee.setEmail(newEmail);
    employeeRepository.save(testEmployee);

    // Reload testEmployee from database.
    testEmployee = null;
    testEmployee = employeeRepository.findByUsername(username);

    // Check that attribute was changed.
    assertEquals(newEmail, testEmployee.getEmail());
  }

  // Test Employee Reference
  @Test
  public void testReferenceEmployee() {
    // Create Test Stub Instances of EmployeeSchedule and Shift.
    Shift shiftStub = new Shift();
    shiftStub.setName("shiftStubID");
    shiftRepository.save(shiftStub);
    EmployeeSchedule scheduleStub = new EmployeeSchedule();
    scheduleStub.setShift(shiftStub);
    employeeScheduleRepository.save(scheduleStub);

    // Create Instance of Employee and save it to database.
    String username = "TestEmployee";
    Employee testEmployee = new Employee();
    testEmployee.setUsername(username);
    employeeRepository.save(testEmployee);

    // Add reference to scheduleStub.
    testEmployee.addEmployeeSchedule(scheduleStub);
    employeeRepository.save(testEmployee);

    // Reload testEmployee.
    testEmployee = null;
    testEmployee = employeeRepository.findByUsername(username);

    // Verify that reference to scheduleStub was preserved
    assertNotNull(testEmployee.getEmployeeSchedules());
    assertEquals(1, testEmployee.getEmployeeSchedules().size());
    for (var storedSchedule : testEmployee.getEmployeeSchedules()) {
      assertEquals(scheduleStub.getId(), storedSchedule.getId());
    }
  }

}
