package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.ItemCategory;


public interface ItemCategoryRepository extends CrudRepository<ItemCategory, String> {
	 
	ItemCategory findItemCategoryByName(String name);
	 

}
