package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
    item.setcanDeliver(canDeliver);
    item.setcanPickUp(canPickUp);
    item.setisDiscontinued(isDiscontinued);
    itemRepository.save(item);

    // delete item instance
    item = null;
    // fetching item instance
    item = itemRepository.findItemByName(itemname);
    // asserts
    assertNotNull(item);
    assertEquals(itemname, item.getName());
    assertEquals(price, item.getPrice());
    assertEquals(inventory, item.getInventory());
    assertEquals(canDeliver, item.getcanDeliver());
    assertEquals(canPickUp, item.getcanPickUp());
    assertEquals(isDiscontinued, item.getisDiscontinued());
  }

  @Test
  public void testAtributeItem() {
    // Creating item instance
    Item item = new Item();
    String name = "coke";
    item.setName(name);
    item.setPrice(10);
    item.setcanDeliver(false);
    item.setcanPickUp(false);
    item.setInventory(5);
    item.setisDiscontinued(false);

    // Saving item instance
    itemRepository.save(item);
    // delete item instance
    item = null;

    /// fetching item instance
    item = itemRepository.findItemByName(name);
    item.setcanDeliver(true);
    item.setcanPickUp(true);
    item.setInventory(3);
    item.setisDiscontinued(true);
    item.setPrice(4);
    // Saving item instance
    itemRepository.save(item);
    // delete item instance
    item = null;
    // fetching item instance
    item = itemRepository.findItemByName(name);
    // asserts
    assertNotNull(item);
    assertEquals(name, item.getName());
    assertEquals(3, item.getInventory());
    assertEquals(4, item.getPrice());
    assertEquals(true, item.getcanDeliver());
    assertEquals(true, item.getcanPickUp());
    assertEquals(true, item.getisDiscontinued());


  }

}

