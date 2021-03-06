package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.SpecificItem;

public interface SpecificItemRepository extends CrudRepository<SpecificItem, Long> {

  SpecificItem findById(long id);

}
