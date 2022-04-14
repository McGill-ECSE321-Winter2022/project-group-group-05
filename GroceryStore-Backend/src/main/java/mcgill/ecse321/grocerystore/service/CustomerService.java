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

/**
 * RESTful Service methods for Customer
 * 
 * @author Peini Cheng
 */
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

  /**
   * Creates a new Customer account
   * 
   * @param username the username of the account
   * @param password the password of the account
   * @param email the email of the account
   * @param address the address of the account
   * @param isLocal whether the address of the account is a local address
   * @return the Customer instance created wit the input parameters
   * @throws IllegalArgumentException
   */
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

  /**
   * Return the Customer instance with the given username
   * 
   * @param username username of the account
   * @return the Customer instance with given username if there exists one
   * @throws IllegalArgumentException
   */
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

  /**
   * Changes the password of the specific Customer account
   * 
   * @param username username of the account
   * @param password new password of the account
   * @return the Customer instance after the password is changed
   * @throws IllegalArgumentException
   */
  @Transactional
  public Customer setCustomerPassword(String username, String password)
      throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    return setPassword(customer, password);
  }

  /**
   * Changes the email of the specific Customer account
   * 
   * @param username username of the account
   * @param email new email of the account
   * @return the Customer instance after the email is changed
   * @throws IllegalArgumentException
   */
  @Transactional
  public Customer setCustomerEmail(String username, String email) throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    return setEmail(customer, email);
  }

  /**
   * Changes the address of the specific Customer account
   * 
   * @param username username of the account
   * @param address new address of the account
   * @return the Customer instance after the address is changed
   * @throws IllegalArgumentException
   */
  @Transactional
  public Customer setCustomerAddress(String username, String address)
      throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    return setAddress(customer, address);
  }

  /**
   * Changes whether the current address is local
   * 
   * @param username username of the account
   * @param isLocal whether the current address is local
   * @return the Customer instance after the isLocal field is changed
   * @throws IllegalArgumentException
   */
  @Transactional
  public Customer setCustomerIsLocal(String username, boolean isLocal)
      throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    return setIsLocal(customer, isLocal);
  }

  /**
   * Returns a list of purchases sorted by purchase time from newest to oldest
   *
   * @param username username of the account
   * @return a sorted list of purchases
   * @throws IllegalArgumentException
   */
  @Transactional
  public List<Purchase> getPurchasesByUsername(String username) throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    List<Purchase> purchaseList = new ArrayList<>();
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

  /**
   * Deletes the specific Customer account
   * 
   * @param username username of the account
   * @throws IllegalArgumentException
   */
  @Transactional
  public void deleteCustomer(String username) throws IllegalArgumentException {
    Customer customer = getCustomer(username);
    customerRepository.delete(customer);
  }

  /**
   * Returns the list of all Customer accounts
   * 
   * @return a sorted list (ascending lexicographical order) of Customer instances
   */
  @Transactional
  public List<Customer> getAllCustomers() {
    ArrayList<Customer> customerList = customerRepository.findAllByOrderByUsername();
    return customerList;
  }

  private Customer setPassword(Customer customer, String password) throws IllegalArgumentException {
    if (password == null || password.trim().length() == 0) {
      throw new IllegalArgumentException("Password cannot be empty!");
    }
    customer.setPassword(password);
    return customerRepository.save(customer);
  }

  private Customer setEmail(Customer customer, String email) throws IllegalArgumentException {
    if (email == null || email.trim().length() == 0) {
      throw new IllegalArgumentException("Email cannot be empty!");
    }
    if (!verifyEmail(email)) {
      throw new IllegalArgumentException("Email is invalid!");
    }
    customer.setEmail(email);
    return customerRepository.save(customer);
  }

  private Customer setAddress(Customer customer, String address) throws IllegalArgumentException {
    if (address == null || address.trim().length() == 0) {
      throw new IllegalArgumentException("Address cannot be empty!");
    }
    customer.setAddress(address);
    return customerRepository.save(customer);
  }

  private Customer setIsLocal(Customer customer, boolean isLocal) {
    customer.setIsLocal(isLocal);
    return customerRepository.save(customer);
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
