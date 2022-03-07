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
import mcgill.ecse321.grocerystore.model.Customer;

@ExtendWith(MockitoExtension.class)
public class TestCustomerService {
  @Mock
  private CustomerRepository customerDao;

  @InjectMocks
  private CustomerService service;

  private static final String CUSTOMER_KEY = "TestCustomer";
  private static final String NONEXISTING_KEY = "NotACustomer";

  @BeforeEach
  public void setMockOutput() {
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

    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };
    lenient().when(customerDao.save(any(Customer.class))).thenAnswer(returnParameterAsAnswer);
  }

  @Test
  public void testCreateCustomer() {
    assertEquals(0, service.getAllCustomers().size());

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
  public void testCreateCustomerExistingUsername() {
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
  
  @Test
  public void testCreateCustomerInvalidEmail1() {
    String username = "test";
    String password = "password";
    String email = "12 3@gmail.com";
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
  public void testCreateCustomerInvalidEmail2() {
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

  @Test
  public void testCreateCustomerInvalidEmail3() {
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

  @Test
  public void testCreateCustomerInvalidEmail4() {
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

  @Test
  public void testCreateCustomerInvalidEmail5() {
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
    assertNull(service.getCustomer(NONEXISTING_KEY));
  }
}
