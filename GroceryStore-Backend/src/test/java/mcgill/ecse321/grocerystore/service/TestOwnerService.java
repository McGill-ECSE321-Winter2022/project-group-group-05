package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.OwnerRepository;
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.Owner;

@ExtendWith(MockitoExtension.class)
public class TestOwnerService {
  @Mock
  private OwnerRepository ownerDao;
  @Mock
  private CustomerRepository customerDao;
  @Mock
  private EmployeeRepository employeeDao;

  @InjectMocks
  private OwnerService service;

  private static final String OWNER_KEY = "TestOwner";
  private static final String CUSTOMER_KEY = "TestCustomer";
  private static final String EMPLOYEE_KEY = "TestEmployee";
  private static final String NONEXISTING_KEY = "NotAnOwner";


  @BeforeEach
  public void setMockOutput() {
    lenient().when(ownerDao.findByUsername(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(OWNER_KEY)) {
            Owner owner = new Owner();
            owner.setUsername(OWNER_KEY);
            return owner;
          } else {
            return null;
          }
        });

    lenient().when(customerDao.findByUsername(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(CUSTOMER_KEY)) {
            Customer customer = new Customer();
            customer.setUsername(CUSTOMER_KEY);
            return customer;
          } else {
            return null;
          }
        });

    lenient().when(employeeDao.findByUsername(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(EMPLOYEE_KEY)) {
            Employee employee = new Employee();
            employee.setUsername(EMPLOYEE_KEY);
            return employee;
          } else {
            return null;
          }
        });

    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };
    lenient().when(ownerDao.save(any(Owner.class))).thenAnswer(returnParameterAsAnswer);
  }


  @Test
  public void testCreateOwner() {
    String username = "bruno";
    String password = "mars";
    String email = "brunomars@gmail.com";
    Owner owner = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(owner);
    assertEquals(username, owner.getUsername());
    assertEquals(password, owner.getPassword());
    assertEquals(email, owner.getEmail());
  }

  @Test
  public void testCreateOwnerNullUsername() {
    String username = null;
    String password = "mars";
    String email = "brunomars@gmail.com";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testCreateOwnerEmptyUsername() {
    String username = "";
    String password = "mars";
    String email = "brunomars@gmail.com";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testCreateOwnerSpacesUsername() {
    String username = "  ";
    String password = "mars";
    String email = "brunomars@gmail.com";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testCreateOwnerExistingOwnerUsername() {
    String password = "mars";
    String email = "brunomars@gmail.com";
    Owner owner = null;
    String error = null;
    owner = null;
    try {
      owner = service.createOwner(OWNER_KEY, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Username is already taken!", error);
  }

  @Test
  public void testCreateOwnerExistingCustomerUsername() {
    String password = "mars";
    String email = "brunomars@gmail.com";
    Owner owner = null;
    String error = null;
    owner = null;
    try {
      owner = service.createOwner(CUSTOMER_KEY, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Username is already taken!", error);
  }

  @Test
  public void testCreateOwnerExistingEmployeeUsername() {
    String password = "mars";
    String email = "brunomars@gmail.com";
    Owner owner = null;
    String error = null;
    owner = null;
    try {
      owner = service.createOwner(EMPLOYEE_KEY, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Username is already taken!", error);
  }

  @Test
  public void testCreateOwnerNullPassword() {
    String username = "bruno";
    String password = null;
    String email = "brunomars@gmail.com";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Password cannot be empty!", error);
  }

  @Test
  public void testCreateOwnerSpacesPassword() {
    String username = "bruno";
    String password = "  ";
    String email = "brunomars@gmail.com";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Password cannot be empty!", error);
  }

  @Test
  public void testCreateOwnerNullEmail() {
    String username = "bruno";
    String password = "mars";
    String email = null;
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Email cannot be empty!", error);
  }

  @Test
  public void testCreateOwnerEmptyEmail() {
    String username = "bruno";
    String password = "mars";
    String email = "";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Email cannot be empty!", error);
  }

  @Test
  public void testCreateOwnerSpacesEmail() {
    String username = "bruno";
    String password = "mars";
    String email = "  ";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Email cannot be empty!", error);
  }

  @Test
  public void testCreateOwnerMissingAtEmail() {
    String username = "bruno";
    String password = "mars";
    String email = "brunomars.com";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Email is invalid!", error);
  }

  @Test
  public void testCreateOwnerInvalidEmailDomain1() {
    String username = "bruno";
    String password = "mars";
    String email = "brunomars@gmailcom";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Email is invalid!", error);
  }

  @Test
  public void testCreateOwnerInvalidEmailDomain2() {
    String username = "bruno";
    String password = "mars";
    String email = "brunomars@.com";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Email is invalid!", error);
  }

  @Test
  public void testCreateOwnerInvalidEmailDomain3() {
    String username = "bruno";
    String password = "mars";
    String email = "brunomars@gmail.";
    Owner owner = null;
    String error = null;
    try {
      owner = service.createOwner(username, password, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(owner);
    assertEquals("Email is invalid!", error);
  }

  @Test
  public void testGetExistingPerson() {
    assertEquals(OWNER_KEY, service.getOwner(OWNER_KEY).getUsername());
  }

  @Test
  public void testGetNonExistingPerson() {
    String error = null;
    try {
      service.getOwner(NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("User does not exist!", error);
  }

  @Test
  public void testGetNonExistingPersonNullUsername() {
    String error = null;
    try {
      service.getOwner(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testGetNonExistingPersonEmptyUsername() {
    String error = null;
    try {
      service.getOwner("");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testGetNonExistingPersonSpacesUsername() {
    String error = null;
    try {
      service.getOwner("  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

}


