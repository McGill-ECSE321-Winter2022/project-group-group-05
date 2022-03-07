package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.model.Customer;

@Service
public class CustomerService {
  @Autowired
  CustomerRepository customerRepository;

  @Transactional
  public Customer createCustomer(String username, String password, String email, String address,
      Boolean isLocal) {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    if (customerRepository.findByUsername(username) != null) {
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
    customerRepository.save(customer);
    return customer;
  }

  @Transactional
  public Customer getCustomer(String username) {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    Customer customer = customerRepository.findByUsername(username);
    return customer;
  }

  @Transactional
  public List<Customer> getAllCustomers() {
    return toList(customerRepository.findAll());
  }

  private <T> List<T> toList(Iterable<T> iterable) {
    List<T> resultList = new ArrayList<T>();
    for (T t : iterable) {
      resultList.add(t);
    }
    return resultList;
  }
}
