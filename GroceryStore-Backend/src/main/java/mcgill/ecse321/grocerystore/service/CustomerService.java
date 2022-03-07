package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.OwnerRepository;
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Purchase;

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
    if (password == null || password.trim().length() == 0) {
      throw new IllegalArgumentException("Password cannot be empty!");
    }
    if (email == null || email.trim().length() == 0) {
      throw new IllegalArgumentException("Email cannot be empty!");
    }
    if (email.contains(" ") || !email.contains(".") || email.indexOf("@") < 1
        || email.indexOf(".") <= email.indexOf("@") + 1
        || email.lastIndexOf(".") >= email.length() - 1) {
      throw new IllegalArgumentException("Email is invalid!");
    }
    if (address == null || address.trim().length() == 0) {
      throw new IllegalArgumentException("Address cannot be empty!");
    }
    Customer customer = new Customer();
    customer.setUsername(username);
    customer.setPassword(password);
    customer.setEmail(email);
    customer.setAddress(address);
    customer.setIsLocal(isLocal);
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
  public Set<Purchase> getPurchasesByUsername(String username) throws IllegalArgumentException {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    Customer customer = getCustomer(username);
    Set<Purchase> purchaseList = customer.getPurchases();
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
}
