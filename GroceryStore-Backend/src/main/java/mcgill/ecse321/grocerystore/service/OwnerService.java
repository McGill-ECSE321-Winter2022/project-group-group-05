package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.EmployeeRepository;
import mcgill.ecse321.grocerystore.dao.OwnerRepository;
import mcgill.ecse321.grocerystore.model.Owner;

/**
 * Service methods for Owner
 * 
 * @author Sibo Huang
 */
@Service
public class OwnerService {
  @Autowired
  OwnerRepository ownerRepository;
  @Autowired
  CustomerRepository customerRepository;
  @Autowired
  EmployeeRepository employeeRepository;

  /**
   * Creates a new owner account with the given parameters
   * 
   * @param username username of the new account
   * @param password password of the new account
   * @param email email of the new account
   * @return the new Owner instance created
   */
  @Transactional
  public Owner createOwner(String username, String password, String email) {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    if ((ownerRepository.findByUsername(username) != null)
        || (customerRepository.findByUsername(username) != null)
        || (employeeRepository.findByUsername(username) != null)) {
      throw new IllegalArgumentException("Username is already taken!");
    }
    if (password == null || password.trim().length() == 0) {
      throw new IllegalArgumentException("Password cannot be empty!");
    }
    if (email == null || email.trim().length() == 0) {
      throw new IllegalArgumentException("Email cannot be empty!");
    }
    if (!verifyEmail(email)) {
      throw new IllegalArgumentException("Email is invalid!");
    }

    Owner owner = new Owner();
    owner.setUsername(username);
    owner.setPassword(password);
    owner.setEmail(email);
    return ownerRepository.save(owner);
  }

  /**
   * Returns the owner account with the given username
   * 
   * @param username username of the account
   * @return the Owner instance with the given username
   * @throws IllegalArgumentException
   */
  @Transactional
  public Owner getOwner(String username) throws IllegalArgumentException {
    if (username == null || username.trim().length() == 0) {
      throw new IllegalArgumentException("Username cannot be empty!");
    }
    Owner owner = ownerRepository.findByUsername(username);
    if (owner == null) {
      throw new IllegalArgumentException("Owner does not exist!");
    }
    return owner;
  }

  /**
   * Deletes the owner account with the given username
   * 
   * @param username username of the account
   * @throws IllegalArgumentException
   */
  @Transactional
  public void deleteOwner(String username) throws IllegalArgumentException {
    Owner owner = getOwner(username);
    ownerRepository.delete(owner);
  }

  /**
   * Changes the specific owner account detail
   * 
   * @param username username of the account
   * @param password new password of the account
   * @param email new email of the account
   * @return the Owner instance after the change
   */
  @Transactional
  public Owner updateOwner(String username, String password, String email) {
    if (password == null || password.trim().length() == 0) {
      throw new IllegalArgumentException("Password cannot be empty!");
    }
    if (email == null || email.trim().length() == 0) {
      throw new IllegalArgumentException("Email cannot be empty!");
    }
    if (!verifyEmail(email)) {
      throw new IllegalArgumentException("Email is invalid!");
    }
    Owner owner = getOwner(username);
    owner.setPassword(password);
    owner.setEmail(email);
    return this.ownerRepository.save(owner);
  }

  /**
   * Returns the list of all owners sorted by alphabetic order
   * 
   * @return a sorted list of owners
   */
  @Transactional
  public List<Owner> getAllOwners() {
    ArrayList<Owner> ownerList = ownerRepository.findAllByOrderByUsername();
    return ownerList;
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
