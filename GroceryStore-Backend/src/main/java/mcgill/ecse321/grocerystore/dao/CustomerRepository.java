package mcgill.ecse321.grocerystore.dao;

import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Customer;

public interface CustomerRepository extends CrudRepository<Customer, String> {
  Customer findByUsername(String username);

  ArrayList<Customer> findAllByOrderByUsername();
}
