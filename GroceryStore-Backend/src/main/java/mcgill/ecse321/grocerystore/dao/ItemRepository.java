package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Item;

public interface ItemRepository extends CrudRepository<Item, String> {


}
