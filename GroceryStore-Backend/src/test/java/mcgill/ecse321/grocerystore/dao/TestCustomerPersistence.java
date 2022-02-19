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
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Purchase;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestCustomerPersistence {
  @Autowired
  private CustomerRepository customerRepository;
  @Autowired
  private PurchaseRepository purchaseRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    customerRepository.deleteAll();
    purchaseRepository.deleteAll();
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
  public void testAttributeCustomer() {
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
    customerRepository.save(customer);

    customer = null;

    customer = customerRepository.findCustomerByUsername(username);
    assertEquals(username, customer.getUsername());
    assertEquals(password, customer.getPassword());
    assertEquals(email, customer.getEmail());
    assertEquals(address, customer.getAddress());
    assertEquals(isLocal, customer.getIsLocal());
  }

  @Test
  public void testAsssociationCustomer() {
    String username = "TestCustomer";
    Purchase p1 = new Purchase();
    Purchase p2 = new Purchase();
    purchaseRepository.save(p1);
    purchaseRepository.save(p2);
    Customer customer = new Customer();
    customer.setUsername(username);
    customer.addPurchase(p1);
    customer.addPurchase(p2);
    customerRepository.save(customer);
    assertEquals(2, customer.getPurchases().size());

    customer = null;

    Purchase p3 = new Purchase();
    purchaseRepository.save(p3);
    customer = customerRepository.findCustomerByUsername(username);
    customer.addPurchase(p3);
    assertEquals(3, customer.getPurchases().size());
    customer.removePurchase(p3);
    purchaseRepository.delete(p3);
    assertEquals(2, customer.getPurchases().size());
  }

}
