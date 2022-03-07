package mcgill.ecse321.grocerystore.dao;

import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.ItemCategory;


public interface ItemCategoryRepository extends CrudRepository<ItemCategory, String> {

  ItemCategory findByName(String name);

  ArrayList<ItemCategory> findAllByOrderByName();
}
