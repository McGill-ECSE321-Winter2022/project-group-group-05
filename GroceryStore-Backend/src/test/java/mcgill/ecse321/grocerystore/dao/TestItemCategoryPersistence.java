package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestItemCategoryPersistence {

  @Autowired
  private ItemCategoryRepository itemCategoryRepo;

  @Autowired
  private ItemRepository itemRepo;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    itemCategoryRepo.deleteAll();
    itemRepo.deleteAll();
  }

  @Test
  public void testPersistAndLoadItemCategory() {
    String name = "Food";
    ItemCategory category = new ItemCategory();
    category.setName(name);
    itemCategoryRepo.save(category);

    // Delete instance of ItemCategory
    category = null;

    // assert
    category = itemCategoryRepo.findByName(name);
    assertEquals(name, category.getName());

  }

  @Test
  public void testAssociationItemCategory() {
    // creating prerequisite items
    Item coke = new Item();
    Item sprite = new Item();
    coke.setName("coke");
    sprite.setName("sprite");
    coke = itemRepo.save(coke);
    sprite = itemRepo.save(sprite);
    // creating ItemCategory and Item instance
    ItemCategory category = new ItemCategory();
    category.setName("food");
    // adding item into ItemCategory;
    assertTrue(category.addItem(sprite));
    assertTrue(category.addItem(coke));
    // save the itemcategory instance
    itemCategoryRepo.save(category);

    // creating second ItemCategory instance
    ItemCategory category2 = new ItemCategory();
    category2.setName("drink");
    // adding item into ItemCategory;
    assertTrue(category2.addItem(sprite));
    assertTrue(category2.addItem(coke));
    // save the itemcategory instance
    itemCategoryRepo.save(category2);


    // Delete instance of ItemCategory
    category = null;
    // fetching the ItemCategory instance
    category = itemCategoryRepo.findByName("food");
    assertNotNull(category);
    assertEquals(2, category.getItems().size());
    assertTrue(category.removeItem(coke));
    assertTrue(category.removeItem(sprite));

    // Delete second instance of ItemCategory
    category2 = null;
    // fetch second instance of Item Category
    category2 = itemCategoryRepo.findByName("drink");
    assertNotNull(category2);
    assertEquals(2, category2.getItems().size());
    assertTrue(category2.removeItem(coke));
    assertTrue(category2.removeItem(sprite));
  }

}
