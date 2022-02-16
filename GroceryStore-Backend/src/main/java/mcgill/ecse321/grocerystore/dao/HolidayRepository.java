package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Holiday;

public interface HolidayRepository extends CrudRepository<Holiday, String> {
  
  Holiday findByName(String name);

}
