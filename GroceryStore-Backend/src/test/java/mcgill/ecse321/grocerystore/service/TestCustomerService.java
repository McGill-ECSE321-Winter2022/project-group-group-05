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
import java.util.ArrayList;
import java.util.List;
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
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Employee;
import mcgill.ecse321.grocerystore.model.Owner;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.Purchase.PurchaseState;

@ExtendWith(MockitoExtension.class)
public class TestCustomerService {
  @Mock
  private CustomerRepository customerDao;
  @Mock
  private OwnerRepository ownerDao;
  @Mock
  private EmployeeRepository employeeDao;
  @Mock
  private PurchaseRepository purchaseDao;

  @InjectMocks
  private CustomerService service;

  private static final String CUSTOMER_KEY = "TestCustomerOne";
  private static final String TEST = "TestCustomerTwo";
  private static final String OWNER_KEY = "TestOwner";
  private static final String EMPLOYEE_KEY = "TestEmployee";
  private static final String NONEXISTING_KEY = "NotACustomer";
  private static Purchase PURCHASE_ONE = new Purchase();
  private static Purchase PURCHASE_TWO = new Purchase();
  private static Purchase PURCHASE_THREE = new Purchase();
  private static Customer CUSTOMER_TEST = new Customer();

  @BeforeEach
  public void setMockOutput() {
    lenient().when(customerDao.findByUsername(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(CUSTOMER_KEY)) {
            Customer customer = new Customer();
            customer.setUsername(CUSTOMER_KEY);
            PURCHASE_TWO.setState(PurchaseState.Paid);
            PURCHASE_THREE.setState(PurchaseState.Completed);
            customer.addPurchase(PURCHASE_ONE);
            customer.addPurchase(PURCHASE_TWO);
            customer.addPurchase(PURCHASE_THREE);
            return customer;
          } else if (invocation.getArgument(0).equals(TEST)) {
            CUSTOMER_TEST.setUsername(TEST);
            return CUSTOMER_TEST;
          } else {
            return null;
          }
        });

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
    lenient().when(customerDao.save(any(Customer.class))).thenAnswer(returnParameterAsAnswer);
  }

  // Tests for createCustomer
  // createCustomer uses setPassword, setEmail, setAddress, and setIsLocal. Tests for invalid
  // passwords, emails, and addresses are covered by tests for those methods instead.
  @Test
  public void testCreateCustomer() {
    String username = "test";
    String password = "password";
    String email = "123.456@gmail.com";
    String address = "McGill";
    Boolean isLocal = true;
    Customer customer = null;
    try {
      customer = service.createCustomer(username, password, email, address, isLocal);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(customer);
    assertEquals(username, customer.getUsername());
    assertEquals(password, customer.getPassword());
    assertEquals(email, customer.getEmail());
    assertEquals(address, customer.getAddress());
    assertEquals(isLocal, customer.getIsLocal());
  }

  @Test
  public void testCreateCustomerNullUsername() {
    String username = null;
    String password = "password";
    String email = "123@gmail.com";
    String address = "McGill";
    Boolean isLocal = true;
    Customer customer = null;
    String error = null;
    try {
      customer = service.createCustomer(username, password, email, address, isLocal);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(customer);
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testCreateCustomerEmptyUsername() {
    String username = "   ";
    String password = "password";
    String email = "123@gmail.com";
    String address = "McGill";
    Boolean isLocal = true;
    Customer customer = null;
    String error = null;
    try {
      customer = service.createCustomer(username, password, email, address, isLocal);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(customer);
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testCreateCustomerExistingCustomerUsername() {
    String password = "password";
    String email = "123@gmail.com";
    String address = "McGill";
    Boolean isLocal = true;
    Customer customer = null;
    String error = null;
    customer = null;
    try {
      customer = service.createCustomer(CUSTOMER_KEY, password, email, address, isLocal);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(customer);
    assertEquals("Username is already taken!", error);
  }

  @Test
  public void testCreateCustomerExistingOwnerUsername() {
    String password = "password";
    String email = "123@gmail.com";
    String address = "McGill";
    Boolean isLocal = true;
    Customer customer = null;
    String error = null;
    customer = null;
    try {
      customer = service.createCustomer(OWNER_KEY, password, email, address, isLocal);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(customer);
    assertEquals("Username is already taken!", error);
  }

  @Test
  public void testCreateCustomerExistingEmployeeUsername() {
    String password = "password";
    String email = "123@gmail.com";
    String address = "McGill";
    Boolean isLocal = true;
    Customer customer = null;
    String error = null;
    customer = null;
    try {
      customer = service.createCustomer(EMPLOYEE_KEY, password, email, address, isLocal);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(customer);
    assertEquals("Username is already taken!", error);
  }

  @Test
  public void testSetCustomerPassword() {
    String password = "password";
    try {
      service.setCustomerPassword(TEST, password);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertEquals(password, CUSTOMER_TEST.getPassword());
  }

  @Test
  public void testSetCustomerNullPassword() {
    String error = null;
    try {
      service.setCustomerPassword(TEST, null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Password cannot be empty!", error);
  }

  @Test
  public void testSetCustomerEmptyPassword() {
    String error = null;
    try {
      service.setCustomerPassword(TEST, "   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Password cannot be empty!", error);
  }

  @Test
  public void testSetCustomerEmail() {
    String email = "abc@gmail.com";
    try {
      service.setCustomerEmail(TEST, email);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertEquals(email, CUSTOMER_TEST.getEmail());
  }

  @Test
  public void testSetCustomerNullEmail() {
    String error = null;
    try {
      service.setCustomerEmail(TEST, null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Email cannot be empty!", error);
  }

  @Test
  public void testSetCustomerEmptyEmail() {
    String error = null;
    try {
      service.setCustomerEmail(TEST, "   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Email cannot be empty!", error);
  }

  // check if the email address contains "."
  @Test
  public void testSetCustomerMissingPeriodEmail() {
    String email = "abc@gmailcom";
    String error = null;
    try {
      service.setCustomerEmail(TEST, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Email is invalid!", error);
  }

  // check if the email address contains "@"
  @Test
  public void testSetCustomerMissingAtEmail() {
    String email = "abcgmail.com";
    String error = null;
    try {
      service.setCustomerEmail(TEST, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Email is invalid!", error);
  }

  // check if the email address contains "@"
  @Test
  public void createSetInvalidEmailDomain() {
    String email = "abc@.com";
    String error = null;
    try {
      service.setCustomerEmail(TEST, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Email is invalid!", error);
  }

  // check if the last character isn't "."
  @Test
  public void testSetCustomerInvalidEmailTopLevelDomain() {
    String email = "abd@gmail.";
    String error = null;
    try {
      service.setCustomerEmail(TEST, email);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Email is invalid!", error);
  }

  @Test
  public void testSetCustomerAddress() {
    String address = "address";
    try {
      service.setCustomerAddress(TEST, address);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertEquals(address, CUSTOMER_TEST.getAddress());
  }

  @Test
  public void testSetCustomerNullAddress() {
    String error = null;
    try {
      service.setCustomerAddress(TEST, null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Address cannot be empty!", error);
  }

  @Test
  public void testSetCustomerEmptyAddress() {
    String error = null;
    try {
      service.setCustomerAddress(TEST, "   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Address cannot be empty!", error);
  }

  @Test
  public void testSetCustomerIsLocal() {
    boolean isLocal = true;
    try {
      service.setCustomerIsLocal(TEST, isLocal);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertEquals(isLocal, CUSTOMER_TEST.getIsLocal());
  }

  @Test
  public void testGetCustomer() {
    assertEquals(CUSTOMER_KEY, service.getCustomer(CUSTOMER_KEY).getUsername());
  }

  @Test
  public void testGetNonExistingCustomer() {
    String error = null;
    try {
      service.getCustomer(NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("User does not exist!", error);
  }

  @Test
  public void testGetCustomerNullUserame() {
    String error = null;
    try {
      service.getCustomer(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testGetCustomerEmptyUserame() {
    String error = null;
    try {
      service.getCustomer("   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testGetPurchasesByUsername() {
    List<Purchase> purchases = new ArrayList<>();
    PURCHASE_TWO.setTimeOfPurchaseMillis(Time.valueOf("09:17:32").getTime());
    PURCHASE_THREE.setTimeOfPurchaseMillis(Time.valueOf("09:18:45").getTime());
    purchases.add(PURCHASE_THREE);
    purchases.add(PURCHASE_TWO);
    List<Purchase> purchaseList = service.getPurchasesByUsername(CUSTOMER_KEY);
    assertEquals(purchases, purchaseList);
  }

  @Test
  public void testDeleteCustomer() {
    try {
      service.deleteCustomer(CUSTOMER_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    verify(customerDao).delete(any());
  }

}
