
package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)


public class TestItemCategoryPersistence{

  @Autowired
  private ItemCategoryRepository ItemCategoryRepo;

  @Autowired
  private ItemRepository ItemRepo;

  @BeforeEach
  @AfterAll
  public void clearDatabase() {
	  ItemCategoryRepo.deleteAll();
      ItemRepo.deleteAll();
  }

   @Test
  public void testPersistAndLoadItemCategory() {
   String name="Food";
   ItemCategory category=new ItemCategory();
   category.setName(name);
   ItemCategoryRepo.save(category);
   
   //Delete instance of ItemCategory
   category=null;
   
    // assert
  category=ItemCategoryRepo.findItemCategoryByName(name);
   assertEquals(name,category.getName());
   
  }

  @Test
  public void testAssociationItemCategory() {
	 //creating ItemCategory and Item instance
    ItemCategory category = new ItemCategory();
    Item coke=new Item();
    Item sprite=new Item();
    coke.setName("coke");
    sprite.setName("sprite");
    category.setName("food");
    //adding item into ItemCategory
    category.addSItem(sprite);
    category.addSItem(coke);
    //save the itemcategory instance
    ItemCategoryRepo.save(category);
    
    //Delete instance ofItemCategory
   category=null;
    //fetching the ItemCategory instance
    category=ItemCategoryRepo.findItemCategoryByName ("food");
  
    /*
     * Cascading: if ItemCategory is saved then its Item should be saved
     */
    assertNotNull(category);
    assertEquals(2,category.getItem().size());
  }
 
	    
	  
		   
	    
	  
}
