package mcgill.ecse321.grocerystore.dao;

import java.util.ArrayList;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.ItemCategory;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.Purchase.PurchaseState;

public interface ItemCategoryRepository extends CrudRepository<ItemCategory, String> {
	 
	ItemCategory findItemCategoryByName(String name);
	 

}
