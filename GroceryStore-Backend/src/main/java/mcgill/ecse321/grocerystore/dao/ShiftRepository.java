package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Shift;

public interface ShiftRepository extends CrudRepository<Shift, String> {

  Shift findByName(String name);

}
