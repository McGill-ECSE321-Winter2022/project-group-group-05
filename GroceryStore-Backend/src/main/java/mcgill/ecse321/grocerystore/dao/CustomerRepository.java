package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Customer;

public interface CustomerRepository extends CrudRepository<Customer, String> {
  Customer findByUsername(String username);
}
