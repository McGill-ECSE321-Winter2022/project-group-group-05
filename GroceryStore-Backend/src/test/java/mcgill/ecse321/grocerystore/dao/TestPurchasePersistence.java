package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Set;
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
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.Purchase.PurchaseState;
import mcgill.ecse321.grocerystore.model.SpecificItem;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class TestPurchasePersistence {

  @Autowired
  private PurchaseRepository purchaseRepo;

  @Autowired
  private SpecificItemRepository specificItemRepo;

  @Autowired
  private ItemRepository itemRepo;

  @BeforeEach
  @AfterAll
  public void clearDatabase() {
    purchaseRepo.deleteAll();
    specificItemRepo.deleteAll();
    itemRepo.deleteAll();
  }

  @Test
  public void savePurchaseFindByIsDeliveryFindByState() {
    Purchase purchase = new Purchase();
    Purchase purchase2 = new Purchase();
    purchase2.setIsDelivery(true);
    Purchase purchase3 = new Purchase();
    purchase3.setIsDelivery(true);
    purchase3.setState(PurchaseState.Paid);
    // save
    purchaseRepo.save(purchase);
    purchaseRepo.save(purchase2);
    purchase3 = purchaseRepo.save(purchase3);
    long p3id = purchase3.getId();
    // load
    ArrayList<Purchase> deliveries = purchaseRepo.findByIsDelivery(true);
    ArrayList<Purchase> notDeliveries = purchaseRepo.findByIsDelivery(false);
    ArrayList<Purchase> inPaidState =
        purchaseRepo.findByStateOrderByTimeOfPurchaseMillis(PurchaseState.Paid);
    Purchase aPaidPurchase = purchaseRepo.findById(p3id);
    // assert
    assertEquals(PurchaseState.Paid, aPaidPurchase.getState());
    assertEquals(3, purchaseRepo.count());
    assertEquals(2, deliveries.size());
    assertEquals(1, notDeliveries.size());
    assertEquals(1, inPaidState.size());
  }

  @Test
  public void addSpecificItems() {
    Item tomato = new Item("Tomato");
    Item potato = new Item("Potato");
    tomato = itemRepo.save(tomato);
    potato = itemRepo.save(potato);
    SpecificItem tomato1 = new SpecificItem(tomato);
    SpecificItem potato1 = new SpecificItem(potato);
    Purchase myCart = new Purchase();
    myCart.addSpecificItem(tomato1);
    myCart.addSpecificItem(potato1);
    myCart.addSpecificItem(null);
    purchaseRepo.save(myCart);
    /*
     * Cascading: if Purchase is saved then its SpecificItems should be saved
     */
    Purchase retrieveCart = purchaseRepo.findById(myCart.getId());
    // id should be valid after save
    assertNotNull(retrieveCart);
    Set<SpecificItem> cartItems = retrieveCart.getSpecificItems();
    // verify cascading
    assertEquals(2, cartItems.size());
    assertTrue(cartItems.contains(potato1));
    assertTrue(cartItems.contains(tomato1));
  }

  @Test
  public void removeSpecificItems() {
    Item apple = new Item("Apple", 2.99, 100);
    apple = itemRepo.save(apple);
    Purchase myCart = new Purchase();
    SpecificItem apple1 = new SpecificItem(apple, 2);
    // add first item
    myCart.addSpecificItem(apple1);
    myCart = purchaseRepo.save(myCart);
    long cartId = myCart.getId();
    long appleId = apple1.getId();
    // retrieve cart
    Purchase retrievedCart = purchaseRepo.findById(cartId);
    SpecificItem retrievedApple = specificItemRepo.findById(appleId);
    // remove retrieved apple
    assertTrue(retrievedCart.removeSpecificItem(retrievedApple));
  }

}
