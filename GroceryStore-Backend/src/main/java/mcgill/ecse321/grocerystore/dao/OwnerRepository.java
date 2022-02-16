package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Owner;

public interface OwnerRepository extends CrudRepository<Owner, String> {
  Owner findOwnerByUsername(String username);  
}
