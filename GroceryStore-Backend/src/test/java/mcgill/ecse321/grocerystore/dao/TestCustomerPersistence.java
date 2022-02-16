package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Purchase;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestCustomerPersistence {
  @Autowired
  private CustomerRepository customerRepository;

  @AfterEach
  public void clearDatabase() {
    customerRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadPerson() {
    String username = "TestCustomer";
    String password = "password";
    String email = "customer@email.ca";
    String address = "12-address";
    boolean isLocal = true;
    Customer customer = new Customer();
    customer.setUsername(username);
    customer.setEmail(email);
    customer.setPassword(password);
    customer.setAddress(address);
    customer.setIsLocal(isLocal);
    customerRepository.save(customer);

    customer = null;

    customer = customerRepository.findCustomerByUsername(username);
    assertNotNull(customer);
    assertEquals(username, customer.getUsername());
    assertEquals(password, customer.getPassword());
    assertEquals(email, customer.getEmail());
    assertEquals(address, customer.getAddress());
    assertEquals(isLocal, customer.getIsLocal());
  }

  @Test
  public void testAttribute() {
    String username = "TestCustomer";
    Customer customer = new Customer();
    customer.setUsername(username);
    customer.setEmail("customer@email.ca");
    customer.setPassword("password");
    customer.setAddress("12-address");
    customer.setIsLocal(true);
    customerRepository.save(customer);

    customer = null;

    customer = customerRepository.findCustomerByUsername(username);
    String password = "strong-password";
    String email = "new-customer@email.ca";
    String address = "14-address";
    boolean isLocal = false;
    customer.setEmail(email);
    customer.setPassword(password);
    customer.setAddress(address);
    customer.setIsLocal(isLocal);
    assertEquals(username, customer.getUsername());
    assertEquals(password, customer.getPassword());
    assertEquals(email, customer.getEmail());
    assertEquals(address, customer.getAddress());
    assertEquals(isLocal, customer.getIsLocal());
  }

  @Test
  public void testPurchase() {
    String username = "TestCustomer";
    Purchase p1 = new Purchase();
    Purchase p2 = new Purchase();
    Customer customer = new Customer();
    customer.setUsername(username);
    customer.addPurchases(p1);
    customer.addPurchases(p2);
    customerRepository.save(customer);
    assertEquals(2, customer.getPurchases().size());
    
    customer = null;
    
    customer = customerRepository.findCustomerByUsername(username);
    customer.removePurchases(p2);
    assertEquals(1, customer.getPurchases().size());
    customerRepository.save(customer);
    customer = null;
    customer = customerRepository.findCustomerByUsername(username);
    customer.addPurchases(p2);
    assertEquals(2, customer.getPurchases().size());
  }

}
