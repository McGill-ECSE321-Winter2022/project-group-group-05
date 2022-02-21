package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mcgill.ecse321.grocerystore.model.Item;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestItemPersistence {
  @Autowired
  private ItemRepository itemRepository;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    itemRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadItem() {
    // Creating item instance
    Item item = new Item();
    String itemname = "Coke";
    Double price = 3.99;
    int inventory = 3;
    boolean canDeliver = true;
    boolean canPickUp = true;
    boolean isDiscontinued = true;
    item.setName(itemname);
    item.setPrice(price);
    item.setInventory(inventory);
    item.setCanDeliver(canDeliver);
    item.setCanPickUp(canPickUp);
    item.setIsDiscontinued(isDiscontinued);
    itemRepository.save(item);

    // delete item instance
    item = null;
    // fetching item instance
    item = itemRepository.findByName(itemname);
    // asserts
    assertNotNull(item);
    assertEquals(itemname, item.getName());
    assertEquals(price, item.getPrice());
    assertEquals(inventory, item.getInventory());
    assertEquals(canDeliver, item.getCanDeliver());
    assertEquals(canPickUp, item.getCanPickUp());
    assertEquals(isDiscontinued, item.getIsDiscontinued());
  }

  @Test
  public void testAttributeItem() {
    // Creating item instance
    Item item = new Item();
    String name = "Coke";
    item.setName(name);
    item.setPrice(10);
    item.setCanDeliver(false);
    item.setCanPickUp(false);
    item.setInventory(5);
    item.setIsDiscontinued(false);

    // Saving item instance
    itemRepository.save(item);
    // delete item instance
    item = null;

    /// fetching item instance
    item = itemRepository.findByName(name);
    item.setCanDeliver(true);
    item.setCanPickUp(true);
    item.setInventory(3);
    item.setIsDiscontinued(true);
    item.setPrice(4);
    // Saving item instance
    itemRepository.save(item);
    // delete item instance
    item = null;
    // fetching item instance
    item = itemRepository.findByName(name);
    // asserts
    assertNotNull(item);
    assertEquals(name, item.getName());
    assertEquals(3, item.getInventory());
    assertEquals(4, item.getPrice());
    assertEquals(true, item.getCanDeliver());
    assertEquals(true, item.getCanPickUp());
    assertEquals(true, item.getIsDiscontinued());


  }

}

