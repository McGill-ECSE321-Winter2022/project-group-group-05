package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.OwnerRepository;
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.Purchase.PurchaseState;

@Service
public class CustomerService {
  @Autowired
  CustomerRepository customerRepository;
  @Autowired
  OwnerRepository ownerRepository;
  @Autowired
  EmployeeRepository employeeRepository;
  @Autowired
  PurchaseRepository purchaseRepository;

  @Transactional
  public Customer createCustomer(String username, String password, String email, String address,
      Boolean isLocal) throws IllegalArgumentException {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    if (customerRepository.findByUsername(username) != null
        || ownerRepository.findByUsername(username) != null
        || employeeRepository.findByUsername(username) != null) {
      throw new IllegalArgumentException("Username is already taken!");
    }
    Customer customer = new Customer();
    customer.setUsername(username);
    setPassword(customer, password);
    setEmail(customer, email);
    setAddress(customer, address);
    setIsLocal(customer, isLocal);
    return customerRepository.save(customer);
  }

  @Transactional
  public Customer getCustomer(String username) throws IllegalArgumentException {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    Customer customer = customerRepository.findByUsername(username);
    if (customer == null) {
      throw new IllegalArgumentException("User does not exist!");
    }
    return customer;
  }

  @Transactional
  public void setCustomerPassword(String username, String password)
      throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    setPassword(customer, password);
  }

  @Transactional
  public void setCustomerEmail(String username, String email) throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    setEmail(customer, email);
  }

  @Transactional
  public void setCustomerAddress(String username, String address) throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    setAddress(customer, address);
  }

  @Transactional
  public void setCustomerIsLocal(String username, boolean isLocal) throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    setIsLocal(customer, isLocal);
  }

  /**
   * return a list of purchases sorted by purchase time from newest to oldest
   * 
   * @param username
   * @return a sorted list of purchases
   * @throws IllegalArgumentException
   */
  @Transactional
  public List<Purchase> getPurchasesByUsername(String username) throws IllegalArgumentException {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    Customer customer = getCustomer(username);
    List<Purchase> purchaseList = new ArrayList<Purchase>();
    for (Purchase purchase : customer.getPurchases()) {
      if (purchase.getState() != PurchaseState.Cart)
        purchaseList.add(purchase);
    }
    Collections.sort(purchaseList, new Comparator<Purchase>() {
      @Override
      public int compare(Purchase p1, Purchase p2) {
        return (int) (p2.getTimeOfPurchaseMillis() - p1.getTimeOfPurchaseMillis());
      }
    });
    return purchaseList;
  }

  @Transactional
  public void deleteCustomer(String username) throws IllegalArgumentException {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    Customer customer = getCustomer(username);
    customerRepository.delete(customer);
  }

  @Transactional
  public List<Customer> getAllCustomers() {
    ArrayList<Customer> customerList = customerRepository.findAllByOrderByUsername();
    return customerList;
  }

  private void setPassword(Customer customer, String password) throws IllegalArgumentException {
    if (password == null || password.trim().length() == 0) {
      throw new IllegalArgumentException("Password cannot be empty!");
    }
    customer.setPassword(password);
  }

  private void setEmail(Customer customer, String email) throws IllegalArgumentException {
    if (email == null || email.trim().length() == 0) {
      throw new IllegalArgumentException("Email cannot be empty!");
    }
    if (!verifyEmail(email)) {
      throw new IllegalArgumentException("Email is invalid!");
    }
    customer.setEmail(email);
  }

  private void setAddress(Customer customer, String address) throws IllegalArgumentException {
    if (address == null || address.trim().length() == 0) {
      throw new IllegalArgumentException("Address cannot be empty!");
    }
    customer.setAddress(address);
  }

  private void setIsLocal(Customer customer, boolean isLocal) {
    customer.setIsLocal(isLocal);
  }

  /**
   * Used to match the email string to a regex which checks for proper email format. The
   * restrictions for an email to be considered valid can be found <a href=
   * "https://www.baeldung.com/java-email-validation-regex#strict-regular-expression-validation">here</a>
   * 
   * @param email - the email string to be checked
   * @return a boolean indicating whether the email conforms to standards or not. True indicates
   *         that the email is valid.
   */
  private boolean verifyEmail(String email) {
    return Pattern.matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", email);
  }

}
