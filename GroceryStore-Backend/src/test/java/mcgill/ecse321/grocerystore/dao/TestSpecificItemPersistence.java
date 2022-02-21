package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.SpecificItem;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TestSpecificItemPersistence {

  @Autowired
  private SpecificItemRepository specificItemRepo;

  @Autowired
  private ItemRepository itemRepo;

  @BeforeEach
  @AfterEach
  public void clearDatabase() {
    specificItemRepo.deleteAll();
    itemRepo.deleteAll();
  }

  @Test
  public void saveSpecificItem() {
    Item pfgt = new Item("Passion Fruit Green Tea", 6.75, 100);
    pfgt = itemRepo.save(pfgt);
    SpecificItem pfgtInCart = new SpecificItem(pfgt, 2);
    pfgtInCart = specificItemRepo.save(pfgtInCart);
    long spId = pfgtInCart.getId();
    // retrieve
    SpecificItem spItem = specificItemRepo.findSpecificItemById(spId);
    assertEquals(pfgt, spItem.getItem());
    assertEquals(2, spItem.getPurchaseQuantity());
    assertEquals(6.75, spItem.getPurchasePrice());
  }

  @Test
  public void modifySpecificItem() {
    Item mt = new Item("Brown Sugar Milk Tea", 5.0, 132, true, true);
    mt = itemRepo.save(mt);
    SpecificItem mtInCart = new SpecificItem(mt, 3);
    mtInCart = specificItemRepo.save(mtInCart);
    long mtInCartId = mtInCart.getId();
    // modify
    Item item = itemRepo.findItemByName("Brown Sugar Milk Tea");
    item.setPrice(7.65);
    item = itemRepo.save(item);
    // check current stored info
    SpecificItem spItem = specificItemRepo.findSpecificItemById(mtInCartId);
    assertEquals(5.0, spItem.getPurchasePrice());
    // verify
    assertEquals(7.65, spItem.updatePurchasePrice());
    spItem = specificItemRepo.save(spItem);
    // verify check 2
    SpecificItem sp = specificItemRepo.findSpecificItemById(mtInCartId);
    assertEquals(7.65, sp.getPurchasePrice());
  }

}
