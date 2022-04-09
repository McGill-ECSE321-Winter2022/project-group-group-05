package mcgill.ecse321.grocerystore.dao;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Item;

public interface ItemRepository extends CrudRepository<Item, String> {

  Item findByName(String name);

  List<Item> findAllByOrderByName();

  List<Item> findByNameIgnoreCaseContainingOrderByName(String nameFragment);

  List<Item> findByNameIgnoreCaseContainingOrderByNameDesc(String nameFragment);

}
