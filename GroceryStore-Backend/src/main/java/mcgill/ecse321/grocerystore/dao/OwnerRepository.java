package mcgill.ecse321.grocerystore.dao;

import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Owner;

public interface OwnerRepository extends CrudRepository<Owner, String> {
  Owner findByUsername(String username);

  ArrayList<Owner> findAllByOrderByUsername();
}
