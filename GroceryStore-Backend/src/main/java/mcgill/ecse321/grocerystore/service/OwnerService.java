package mcgill.ecse321.grocerystore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.OwnerRepository;
import mcgill.ecse321.grocerystore.model.Owner;

@Service
public class OwnerService {
  @Autowired
  OwnerRepository ownerRepository;
  @Autowired
  CustomerRepository customerRepository;
  @Autowired
  EmployeeRepository employeeRepository;

  @Transactional
  public Owner createOwner(String username, String password, String email) {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    if (ownerRepository.findByUsername(username) != null) {
      throw new IllegalArgumentException("Username is already taken!");
    }
    if (customerRepository.findByUsername(username) != null) {
      throw new IllegalArgumentException("Username is already taken!");
    }
    if (employeeRepository.findByUsername(username) != null) {
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

    Owner owner = new Owner();
    owner.setUsername(username);
    owner.setPassword(password);
    owner.setEmail(email);
    ownerRepository.save(owner);
    return owner;
  }

  @Transactional
  public Owner getOwner(String username) {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    Owner owner = ownerRepository.findByUsername(username);
    if (owner == null) {
      throw new IllegalArgumentException("User does not exist!");
    }
    return owner;
  }

}
