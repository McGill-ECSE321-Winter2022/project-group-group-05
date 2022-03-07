package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

  private static final String CUSTOMER_KEY = "TestCustomer";
  private static final String OWNER_KEY = "TestOwner";
  private static final String EMPLOYEE_KEY = "TestEmployee";
  private static final String NONEXISTING_KEY = "NotACustomer";
  private static Purchase PURCHASE_ONE = new Purchase();
  private static Purchase PURCHASE_TWO = new Purchase();
  private static Purchase PURCHASE_THREE = new Purchase();

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

  @Test
  public void testCreateCustomer() {
    String username = "test";
    String password = "password";
    String email = "123@gmail.com";
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
    String username = "";
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
  public void testCreateCustomerSpacesUsername() {
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
  public void testCreateCustomerNullPassword() {
    String username = "test";
    String password = null;
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
    assertEquals("Password cannot be empty!", error);
  }

  @Test
  public void testCreateCustomerEmptyPassword() {
    String username = "test";
    String password = "";
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
    assertEquals("Password cannot be empty!", error);
  }

  @Test
  public void testCreateCustomerSpacesPassword() {
    String username = "test";
    String password = "   ";
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
    assertEquals("Password cannot be empty!", error);
  }

  @Test
  public void testCreateCustomerNullEmail() {
    String username = "test";
    String password = "password";
    String email = null;
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
    assertEquals("Email cannot be empty!", error);
  }

  @Test
  public void testCreateCustomerEmptyEmail() {
    String username = "test";
    String password = "password";
    String email = "";
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
    assertEquals("Email cannot be empty!", error);
  }

  @Test
  public void testCreateCustomerSpacesEmail() {
    String username = "test";
    String password = "password";
    String email = "   ";
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
    assertEquals("Email cannot be empty!", error);
  }

  // check if the email address contains "."
  @Test
  public void testCreateCustomerMissingPeriodEmail() {
    String username = "test";
    String password = "password";
    String email = "123@gmailcom";
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
    assertEquals("Email is invalid!", error);
  }

  // check if the email address contains "@"
  @Test
  public void testCreateCustomerMissingAtEmail() {
    String username = "test";
    String password = "password";
    String email = "123gmail.com";
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
    assertEquals("Email is invalid!", error);
  }

  // check if the email address contains "@"
  @Test
  public void createCustomerInvalidEmailDomain() {
    String username = "test";
    String password = "password";
    String email = "123@.com";
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
    assertEquals("Email is invalid!", error);
  }

  // check if the last character isn't "."
  @Test
  public void testCreateCustomerInvalidEmailTopLevelDomain() {
    String username = "test";
    String password = "password";
    String email = "123@gmail.";
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
    assertEquals("Email is invalid!", error);
  }

  @Test
  public void testCreateCustomerNullAddress() {
    String username = "test";
    String password = "password";
    String email = "123@gmail.com";
    String address = null;
    Boolean isLocal = true;
    Customer customer = null;
    String error = null;
    try {
      customer = service.createCustomer(username, password, email, address, isLocal);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(customer);
    assertEquals("Address cannot be empty!", error);
  }

  @Test
  public void testCreateCustomerEmptyAddress() {
    String username = "test";
    String password = "password";
    String email = "123@gmail.com";
    String address = "";
    Boolean isLocal = true;
    Customer customer = null;
    String error = null;
    try {
      customer = service.createCustomer(username, password, email, address, isLocal);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(customer);
    assertEquals("Address cannot be empty!", error);
  }

  @Test
  public void testCreateCustomerSpacesAddress() {
    String username = "test";
    String password = "password";
    String email = "123@gmail.com";
    String address = "   ";
    Boolean isLocal = true;
    Customer customer = null;
    String error = null;
    try {
      customer = service.createCustomer(username, password, email, address, isLocal);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(customer);
    assertEquals("Address cannot be empty!", error);
  }

  @Test
  public void testGetExistingPerson() {
    assertEquals(CUSTOMER_KEY, service.getCustomer(CUSTOMER_KEY).getUsername());
  }

  @Test
  public void testGetNonExistingPerson() {
    String error = null;
    try {
      service.getCustomer(NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("User does not exist!", error);
  }

  @Test
  public void testGetNonExistingPersonNullUserame() {
    String error = null;
    try {
      service.getCustomer(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testGetNonExistingPersonEmptyUserame() {
    String error = null;
    try {
      service.getCustomer("");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testGetNonExistingPersonSpacesUserame() {
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
    List<Purchase> purchases = new ArrayList<Purchase>();
    purchases.add(PURCHASE_THREE);
    purchases.add(PURCHASE_TWO);
    List<Purchase> purchaseList = service.getPurchasesByUsername(CUSTOMER_KEY);
    assertEquals(purchases, purchaseList);
  }

  @Test
  public void testGetPurchasesByNullUsername() {
    String error = null;
    try {
      service.getPurchasesByUsername(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testGetPurchasesByEmptyUsername() {
    String error = null;
    try {
      service.getPurchasesByUsername("");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testGetPurchasesBySpacesUsername() {
    String error = null;
    try {
      service.getPurchasesByUsername("   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testGetPurchasesByNonExistingUsername() {
    String error = null;
    try {
      service.getPurchasesByUsername(NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("User does not exist!", error);
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

  @Test
  public void testDeleteNullCustomer() {
    String error = null;
    try {
      service.deleteCustomer(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(customerDao, times(0)).delete(any());
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testDeleteEmptyCustomer() {
    String error = null;
    try {
      service.deleteCustomer("");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(customerDao, times(0)).delete(any());
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testDeleteSpacesCustomer() {
    String error = null;
    try {
      service.deleteCustomer("   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(customerDao, times(0)).delete(any());
    assertEquals("Username cannot be empty!", error);
  }

  @Test
  public void testDeleteNonExistingCustomer() {
    String error = null;
    try {
      service.deleteCustomer(NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    verify(customerDao, times(0)).delete(any());
    assertEquals("User does not exist!", error);
  }
}
