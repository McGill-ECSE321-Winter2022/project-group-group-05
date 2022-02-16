package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
  private CrudRepository<SpecificItem, Long> specificItemRepo;

  @BeforeEach
  @AfterAll
  public void clearDatabase() {
    purchaseRepo.deleteAll();
    specificItemRepo.deleteAll();
  }

  @Test
  public void saveFindByIsDeliveryFindByState() {
    /*
     * Trying to figure out how auto generated id works
     */
    Purchase purchase = new Purchase();
    Purchase purchase2 = new Purchase();
    purchase2.setIsDelivery(true);
    Purchase purchase3 = new Purchase();
    purchase3.setIsDelivery(true);
    purchase3.setState(PurchaseState.Paid);
    System.out.println("Purchase 3's id is now: " + purchase3.getId());
    // save
    purchaseRepo.save(purchase);
    purchaseRepo.save(purchase2);
    purchaseRepo.save(purchase3);
    System.out.println("After saving, Purchase 3's id is: " + purchase3.getId());
    // load
    ArrayList<Purchase> deliveries = purchaseRepo.findByIsDelivery(true);
    ArrayList<Purchase> notDeliveries = purchaseRepo.findByIsDelivery(false);
    ArrayList<Purchase> inPaidState =
        purchaseRepo.findByStateOrderByTimeOfPurchaseMillis(PurchaseState.Paid);
    for (Purchase p : deliveries) {
      if (p.getState().equals("Paid")) {
        System.out.println("This order is paid!");
        System.out.println("This order's id is: " + p.getId());
        System.out.println("After loading, Purchase 3's id is: " + purchase3.getId());
        if (p.getId() == inPaidState.get(0).getId()) {
          System.out.println("It is in the inPaidState list!");
        }
      }
    }
    // assert
    assertEquals(3, purchaseRepo.count());
    assertEquals(2, deliveries.size());
    assertEquals(1, notDeliveries.size());
    assertEquals(1, inPaidState.size());
  }

  @Test
  public void saveSpecificItems() {
    SpecificItem tomato = new SpecificItem();
    SpecificItem potato = new SpecificItem();
    Purchase myCart = new Purchase();
    myCart.addSpecificItem(tomato);
    myCart.addSpecificItem(potato);
    purchaseRepo.save(myCart);
    /*
     * Cascading: if Purchase is saved then its SpecificItems should be saved
     */
    Purchase retrieveCart = purchaseRepo.findPurchaseById(myCart.getId());
    // id should be valid after save
    assertNotNull(retrieveCart);
    Set<SpecificItem> cartItems = retrieveCart.getSpecificItems();
    // verify cascading
    assertEquals(2, cartItems.size());
  }

  @Test
  public void addRemoveSpecificItems() {
    Purchase myCart = new Purchase();
    myCart.addSpecificItem(new SpecificItem());
    myCart = purchaseRepo.save(myCart);
    long myCartId = myCart.getId();
    // 1st retrieval
    Purchase retrieveCart = purchaseRepo.findPurchaseById(myCartId);
    assertEquals(1, retrieveCart.getSpecificItems().size());
    retrieveCart.addSpecificItem(new SpecificItem());
    purchaseRepo.save(retrieveCart);
    // 2nd retrieval
    retrieveCart = purchaseRepo.findPurchaseById(myCartId);
    assertEquals(2, retrieveCart.getSpecificItems().size());
    retrieveCart.addSpecificItem(new SpecificItem());
    purchaseRepo.save(retrieveCart);
    // 3rd retrieval
    retrieveCart = purchaseRepo.findPurchaseById(myCartId);
    assertEquals(3, retrieveCart.getSpecificItems().size());
    retrieveCart.getSpecificItems().clear();
    purchaseRepo.save(retrieveCart);
    // finish
    retrieveCart = purchaseRepo.findPurchaseById(myCartId);
    assertEquals(0, retrieveCart.getSpecificItems().size());
  }

}
