package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;
import mcgill.ecse321.grocerystore.dao.SpecificItemRepository;
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.Purchase.PurchaseState;
import mcgill.ecse321.grocerystore.model.SpecificItem;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestPurchaseService {

  // existing data constants
  private static final long PURCHASE_ID_1 = 100;
  private static final long PURCHASE_ID_2 = 120;
  private static final long PURCHASE_TIME_1 = 8000;
  private static final long PURCHASE_TIME_2 = 9000;
  private static final String ITEM_NAME_1 = "apple";
  private static final String ITEM_NAME_2 = "milk tea";
  private static final String CUSTOMER_USERNAME_POS = "kiosk";
  private static final String CUSTOMER_USERNAME_1 = "josh";

  @Mock
  private PurchaseRepository purchaseRepo;
  @Mock
  private SpecificItemRepository specificItemRepo;
  @Mock
  private ItemRepository itemRepo;
  @Mock
  private CustomerRepository customerRepo;
  @InjectMocks
  private PurchaseService service;

  // simulate database using hashsets
  HashSet<Purchase> purchaseData;
  HashSet<SpecificItem> specificItemData;
  HashSet<Item> itemData;
  HashSet<Customer> customerData;

  @BeforeEach
  public void setMockOutput() {
    this.populateData();
    this.setFindMocks();
    this.setSaveMocks();
    this.setDeleteMocks();
  }

  @Test
  public void addItemToCartEmptyItem() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.addItemToCart(PURCHASE_ID_1, "     ", 1);
    });
    assertEquals("Item name cannot be empty.", thrown.getMessage());
  }

  @Test
  public void addItemToCartExisting() {
    try {
      // set up
      Purchase p = purchaseRepo.findById(PURCHASE_ID_1);
      SpecificItem s = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 45);
      SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 1);
      s = specificItemRepo.save(s);
      s2 = specificItemRepo.save(s2);
      p.addSpecificItem(s);
      p.addSpecificItem(s2);
      p = purchaseRepo.save(p);
      // test
      service.addItemToCart(PURCHASE_ID_1, ITEM_NAME_1, 20);
      p = purchaseRepo.findById(PURCHASE_ID_1);
      assertEquals(2, p.getSpecificItems().size());
      for (SpecificItem spItem : p.getSpecificItems()) {
        if (spItem.getItem().getName().equals(ITEM_NAME_1)) {
          assertEquals(itemRepo.findByName(ITEM_NAME_1), spItem.getItem());
          assertEquals(45 + 20, spItem.getPurchaseQuantity());
          assertEquals(itemRepo.findByName(ITEM_NAME_1).getPrice(), spItem.getPurchasePrice());
        }
      }
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void addItemToCartInvalidItem() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.addItemToCart(PURCHASE_ID_1, "this_item_doesnt_exist", 1);
    });
    assertEquals("Item not found.", thrown.getMessage());
  }

  @Test
  public void addItemToCartNoInventory() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.addItemToCart(PURCHASE_ID_1, ITEM_NAME_1, Integer.MAX_VALUE);
    });
    assertTrue(thrown.getMessage().contains("exceeds current inventory."));
  }

  @Test
  public void addItemToCartNotExisting() {
    try {
      service.addItemToCart(PURCHASE_ID_1, ITEM_NAME_1, 32);
      Purchase p = purchaseRepo.findById(PURCHASE_ID_1);
      assertEquals(1, p.getSpecificItems().size());
      for (SpecificItem spItem : p.getSpecificItems()) {
        assertEquals(itemRepo.findByName(ITEM_NAME_1), spItem.getItem());
        assertEquals(32, spItem.getPurchaseQuantity());
        assertEquals(itemRepo.findByName(ITEM_NAME_1).getPrice(), spItem.getPurchasePrice());
      }
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void addItemToCartNullItem() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.addItemToCart(PURCHASE_ID_1, null, 1);
    });
    assertEquals("Item name cannot be empty.", thrown.getMessage());
  }

  @Test
  public void addItemToCartWrongQuantity() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.addItemToCart(PURCHASE_ID_1, ITEM_NAME_1, -2);
    });
    assertEquals("Quantity must be larger than 0.", thrown.getMessage());
  }

  @Test
  public void addItemToCartWrongState() {
    Purchase p = purchaseRepo.findById(PURCHASE_ID_1);
    p.setState(PurchaseState.Completed);
    p = purchaseRepo.save(p);
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.addItemToCart(PURCHASE_ID_1, ITEM_NAME_1, 1);
    });
    assertEquals("Purchase is not in the 'cart' state.", thrown.getMessage());
  }

  @Test
  public void cancel() {
    try {
      Purchase p = purchaseRepo.findById(PURCHASE_ID_1);
      p.setState(PurchaseState.Paid);
      p = purchaseRepo.save(p);
      service.cancel(PURCHASE_ID_1);
      assertEquals(PurchaseState.Cancelled, purchaseRepo.findById(PURCHASE_ID_1).getState());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void cancelWrongState() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.cancel(PURCHASE_ID_1);
    });
    assertEquals("Purchase is not in the 'paid' state.", thrown.getMessage());
  }

  @Test
  public void complete() {
    try {
      Purchase p = purchaseRepo.findById(PURCHASE_ID_1);
      p.setState(PurchaseState.Prepared);
      p = purchaseRepo.save(p);
      service.complete(PURCHASE_ID_1);
      assertEquals(PurchaseState.Completed, purchaseRepo.findById(PURCHASE_ID_1).getState());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void completeWrongState() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.complete(PURCHASE_ID_1);
    });
    assertEquals("Purchase is not in the 'prepared' state.", thrown.getMessage());
  }

  @Test
  public void getAll() {
    // these are not actually tested since all they do is call the CRUD repository
    assertEquals(0, service.getAll().size());
    assertEquals(0, service.getAllCompleted().size());
    assertEquals(0, service.getAllDesc().size());
    assertEquals(0, service.getAllPaid().size());
    assertEquals(0, service.getAllPrepared().size());
  }

  @Test
  public void getCartByPOS() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.getCart("kiosk");
    });
    assertEquals("This is a POS account.", thrown.getMessage());
  }

  @Test
  public void getCartEmptyUsername() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.getCart("     ");
    });
    assertEquals("Username cannot be empty.", thrown.getMessage());
  }

  @Test
  public void getCartExisting() {
    try {
      // set up
      SpecificItem cartItem1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 12);
      SpecificItem cartItem2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 23);
      cartItem1 = specificItemRepo.save(cartItem1);
      cartItem2 = specificItemRepo.save(cartItem2);
      Customer c = customerRepo.findByUsername(CUSTOMER_USERNAME_1);
      Purchase p = new Purchase();
      p.setId(12345);
      p.addSpecificItem(cartItem1);
      p.addSpecificItem(cartItem2);
      p = purchaseRepo.save(p);
      c.addPurchase(p);
      c = customerRepo.save(c);
      // change price of item 1
      Item item1 = itemRepo.findByName(ITEM_NAME_1);
      double newPrice = 37385.99;
      item1.setPrice(newPrice);
      // test
      Purchase cart = service.getCart(CUSTOMER_USERNAME_1);
      assertEquals(12345, cart.getId());
      assertEquals(PurchaseState.Cart, cart.getState());
      assertEquals(2, cart.getSpecificItems().size());
      boolean priceIsUpdated = false;
      for (SpecificItem spItem : cart.getSpecificItems()) {
        if (spItem.getPurchasePrice() == newPrice) {
          priceIsUpdated = true;
        }
      }
      assertTrue(priceIsUpdated);
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void getCartInvalidUsername() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.getCart("this_customer_doesnt_exist");
    });
    assertEquals("Customer not found.", thrown.getMessage());
  }

  @Test
  public void getCartNew() {
    try {
      Purchase cart = service.getCart(CUSTOMER_USERNAME_1);
      assertEquals(0, cart.getSpecificItems().size());
      assertEquals(PurchaseState.Cart, cart.getState());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void getCartNewExistingPurchases() {
    try {
      // set up
      Customer c = customerRepo.findByUsername(CUSTOMER_USERNAME_1);
      Purchase p = new Purchase();
      p.setId(12345);
      p.setState(PurchaseState.Completed);
      p = purchaseRepo.save(p);
      c.addPurchase(p);
      c = customerRepo.save(c);
      // test
      Purchase cart = service.getCart(CUSTOMER_USERNAME_1);
      assertNotEquals(12345, cart.getId());
      assertEquals(0, cart.getSpecificItems().size());
      assertEquals(PurchaseState.Cart, cart.getState());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void getCartNullUsername() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.getCart(null);
    });
    assertEquals("Username cannot be empty.", thrown.getMessage());
  }

  @Test
  public void getPurchase() {
    try {
      service.getPurchase(PURCHASE_ID_1);
      service.getPurchase(PURCHASE_ID_2);
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void getPurchaseNotFound() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.getPurchase(-1);
    });
    assertEquals("Purchase not found.", thrown.getMessage());
  }

  @Test
  public void pay() {
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 5);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 8);
    s1 = specificItemRepo.save(s1);
    s2 = specificItemRepo.save(s2);
    cart.addSpecificItem(s1);
    cart.addSpecificItem(s2);
    cart = purchaseRepo.save(cart);
    try {
      service.pay(PURCHASE_ID_1);
      cart = purchaseRepo.findById(PURCHASE_ID_1);
      assertEquals(PurchaseState.Paid, cart.getState());
      assertEquals(2, cart.getSpecificItems().size());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void payByPOS() {
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    Customer pos = customerRepo.findByUsername(CUSTOMER_USERNAME_POS);
    pos.addPurchase(cart);
    pos = customerRepo.save(pos);
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.pay(PURCHASE_ID_1);
    });
    assertEquals("This is a POS purchase.", thrown.getMessage());
  }

  @Test
  public void payDelivery() {
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 5);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 8);
    s1 = specificItemRepo.save(s1);
    s2 = specificItemRepo.save(s2);
    cart.addSpecificItem(s1);
    cart.addSpecificItem(s2);
    cart.setIsDelivery(true);
    cart = purchaseRepo.save(cart);
    try {
      service.pay(PURCHASE_ID_1);
      cart = purchaseRepo.findById(PURCHASE_ID_1);
      assertEquals(PurchaseState.Paid, cart.getState());
      assertEquals(2, cart.getSpecificItems().size());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void payNoInventory() {
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), Integer.MAX_VALUE);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), Integer.MAX_VALUE);
    s1 = specificItemRepo.save(s1);
    s2 = specificItemRepo.save(s2);
    cart.addSpecificItem(s1);
    cart.addSpecificItem(s2);
    cart = purchaseRepo.save(cart);
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.pay(PURCHASE_ID_1);
    });
    assertTrue(thrown.getMessage().contains("exceeds current inventory."));
  }

  @Test
  public void payNotDeliverableItem() {
    Item item1 = itemRepo.findByName(ITEM_NAME_1);
    item1.setCanDeliver(false);
    itemRepo.save(item1);
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 5);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 8);
    s1 = specificItemRepo.save(s1);
    s2 = specificItemRepo.save(s2);
    cart.addSpecificItem(s1);
    cart.addSpecificItem(s2);
    cart.setIsDelivery(true);
    cart = purchaseRepo.save(cart);
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.pay(PURCHASE_ID_1);
    });
    assertTrue(thrown.getMessage().contains("cannot be delivered."));
  }

  @Test
  public void payNotPickUpableItem() {
    Item item1 = itemRepo.findByName(ITEM_NAME_1);
    item1.setCanPickUp(false);
    itemRepo.save(item1);
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 5);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 8);
    s1 = specificItemRepo.save(s1);
    s2 = specificItemRepo.save(s2);
    cart.addSpecificItem(s1);
    cart.addSpecificItem(s2);
    cart = purchaseRepo.save(cart);
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.pay(PURCHASE_ID_1);
    });
    assertTrue(thrown.getMessage().contains("cannot be picked-up."));
  }

  @Test
  public void posDeleteCart() {
    Customer pos = customerRepo.findByUsername(CUSTOMER_USERNAME_POS);
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    pos.addPurchase(cart);
    pos = customerRepo.save(pos);
    try {
      service.posDeleteCart(PURCHASE_ID_1);
      assertNull(purchaseRepo.findById(PURCHASE_ID_1));
      assertEquals(0, customerRepo.findByUsername(CUSTOMER_USERNAME_POS).getPurchases().size());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void posDeleteCartNotPOSCart() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.posDeleteCart(PURCHASE_ID_1);
    });
    assertEquals("Not a POS purchase.", thrown.getMessage());
  }

  @Test
  public void posGetNewCart() {
    try {
      service.posGetNewCart();
      assertEquals(1, customerRepo.findByUsername(CUSTOMER_USERNAME_POS).getPurchases().size());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void posNotFound() {
    customerRepo.delete(customerRepo.findByUsername(CUSTOMER_USERNAME_POS));
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.posGetNewCart();
    });
    assertEquals("POS account 'kiosk' not found.", thrown.getMessage());
  }

  @Test
  public void posPay() {
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 5);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 8);
    s1 = specificItemRepo.save(s1);
    s2 = specificItemRepo.save(s2);
    cart.addSpecificItem(s1);
    cart.addSpecificItem(s2);
    cart = purchaseRepo.save(cart);
    Customer pos = customerRepo.findByUsername(CUSTOMER_USERNAME_POS);
    pos.addPurchase(cart);
    pos = customerRepo.save(pos);
    try {
      service.posPay(PURCHASE_ID_1);
      cart = purchaseRepo.findById(PURCHASE_ID_1);
      assertEquals(PurchaseState.Completed, cart.getState());
      assertEquals(2, cart.getSpecificItems().size());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void posPayNotPOS() {
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.posPay(PURCHASE_ID_1);
    });
    assertEquals("Not a POS purchase.", thrown.getMessage());
  }

  @Test
  public void posPurgeCarts() {
    Customer pos = customerRepo.findByUsername(CUSTOMER_USERNAME_POS);
    Purchase p1 = purchaseRepo.findById(PURCHASE_ID_1);
    Purchase p2 = purchaseRepo.findById(PURCHASE_ID_2);
    p1.setState(PurchaseState.Completed);
    p1 = purchaseRepo.save(p1);
    p2 = purchaseRepo.save(p2);
    pos.addPurchase(p1);
    pos.addPurchase(p2);
    pos = customerRepo.save(pos);
    try {
      service.posPurgeCarts();
      assertEquals(1, customerRepo.findByUsername(CUSTOMER_USERNAME_POS).getPurchases().size());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void prepare() {
    Purchase p = purchaseRepo.findById(PURCHASE_ID_1);
    p.setState(PurchaseState.Paid);
    p = purchaseRepo.save(p);
    try {
      service.prepare(PURCHASE_ID_1);
      assertEquals(PurchaseState.Prepared, purchaseRepo.findById(PURCHASE_ID_1).getState());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void setIsDeliveryByPOS() {
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    Customer pos = customerRepo.findByUsername(CUSTOMER_USERNAME_POS);
    pos.addPurchase(cart);
    pos = customerRepo.save(pos);
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.setIsDelivery(PURCHASE_ID_1, true);
    });
    assertEquals("This is a POS purchase.", thrown.getMessage());
  }

  @Test
  public void setIsDeliveryNotDeliverableItem() {
    Item item1 = itemRepo.findByName(ITEM_NAME_1);
    item1.setCanDeliver(false);
    itemRepo.save(item1);
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 5);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 8);
    s1 = specificItemRepo.save(s1);
    s2 = specificItemRepo.save(s2);
    cart.addSpecificItem(s1);
    cart.addSpecificItem(s2);
    cart = purchaseRepo.save(cart);
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.setIsDelivery(PURCHASE_ID_1, true);
    });
    assertTrue(thrown.getMessage().contains("cannot be delivered."));
  }

  @Test
  public void setIsDeliveryNotPickUpableItem() {
    Item item1 = itemRepo.findByName(ITEM_NAME_1);
    item1.setCanPickUp(false);
    itemRepo.save(item1);
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 5);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 8);
    s1 = specificItemRepo.save(s1);
    s2 = specificItemRepo.save(s2);
    cart.addSpecificItem(s1);
    cart.addSpecificItem(s2);
    cart.setIsDelivery(true);
    cart = purchaseRepo.save(cart);
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      service.setIsDelivery(PURCHASE_ID_1, false);
    });
    assertTrue(thrown.getMessage().contains("cannot be picked-up."));
  }

  @Test
  public void setIsDeliveryTrue() {
    Purchase cart = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s1 = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 5);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 8);
    s1 = specificItemRepo.save(s1);
    s2 = specificItemRepo.save(s2);
    cart.addSpecificItem(s1);
    cart.addSpecificItem(s2);
    cart = purchaseRepo.save(cart);
    try {
      service.setIsDelivery(PURCHASE_ID_1, true);
      assertTrue(purchaseRepo.findById(PURCHASE_ID_1).getIsDelivery());
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void setItemQuantityExisting() {
    Purchase p = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 45);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 1);
    s = specificItemRepo.save(s);
    s2 = specificItemRepo.save(s2);
    p.addSpecificItem(s);
    p.addSpecificItem(s2);
    p = purchaseRepo.save(p);
    try {
      service.setItemQuantity(PURCHASE_ID_1, ITEM_NAME_1, 19);
      p = purchaseRepo.findById(PURCHASE_ID_1);
      assertEquals(2, p.getSpecificItems().size());
      for (SpecificItem spItem : p.getSpecificItems()) {
        if (spItem.getItem().getName().equals(ITEM_NAME_1)) {
          assertEquals(itemRepo.findByName(ITEM_NAME_1), spItem.getItem());
          assertEquals(19, spItem.getPurchaseQuantity());
          assertEquals(itemRepo.findByName(ITEM_NAME_1).getPrice(), spItem.getPurchasePrice());
        }
      }
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void setItemQuantityNotExisting() {
    try {
      service.setItemQuantity(PURCHASE_ID_1, ITEM_NAME_1, 32);
      Purchase p = purchaseRepo.findById(PURCHASE_ID_1);
      assertEquals(1, p.getSpecificItems().size());
      for (SpecificItem spItem : p.getSpecificItems()) {
        assertEquals(itemRepo.findByName(ITEM_NAME_1), spItem.getItem());
        assertEquals(32, spItem.getPurchaseQuantity());
        assertEquals(itemRepo.findByName(ITEM_NAME_1).getPrice(), spItem.getPurchasePrice());
      }
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void setItemQuantityRemove() {
    Purchase p = purchaseRepo.findById(PURCHASE_ID_1);
    SpecificItem s = new SpecificItem(itemRepo.findByName(ITEM_NAME_1), 45);
    SpecificItem s2 = new SpecificItem(itemRepo.findByName(ITEM_NAME_2), 1);
    s = specificItemRepo.save(s);
    s2 = specificItemRepo.save(s2);
    p.addSpecificItem(s);
    p.addSpecificItem(s2);
    p = purchaseRepo.save(p);
    try {
      service.setItemQuantity(PURCHASE_ID_1, ITEM_NAME_1, -1);
      p = purchaseRepo.findById(PURCHASE_ID_1);
      assertEquals(1, p.getSpecificItems().size());
      for (SpecificItem spItem : p.getSpecificItems()) {
        assertNotEquals(ITEM_NAME_1, spItem.getItem().getName());
      }
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

  private void populateData() {
    purchaseData = new HashSet<>();
    specificItemData = new HashSet<>();
    itemData = new HashSet<>();
    customerData = new HashSet<>();
    Purchase pur = new Purchase();
    pur.setId(PURCHASE_ID_1);
    pur.setTimeOfPurchaseMillis(PURCHASE_TIME_1);
    purchaseData.add(pur);
    pur = new Purchase();
    pur.setId(PURCHASE_ID_2);
    pur.setTimeOfPurchaseMillis(PURCHASE_TIME_2);
    purchaseData.add(pur);
    Item item = new Item(ITEM_NAME_1, 50, 1000, true, true);
    itemData.add(item);
    item = new Item(ITEM_NAME_2, 60, 2000, true, true);
    itemData.add(item);
    Customer cus = new Customer();
    cus.setUsername(CUSTOMER_USERNAME_POS);
    customerData.add(cus);
    cus = new Customer();
    cus.setUsername(CUSTOMER_USERNAME_1);
    customerData.add(cus);
  }

  private void setDeleteMocks() {
    // mimick default delete behavior, but on data hashsets instead
    // need to add @MockitoSettings(strictness = Strictness.LENIENT) at class declaration
    // purchaseRepo
    doThrow(new IllegalArgumentException()).when(purchaseRepo).delete(null);
    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        purchaseData.remove(invocation.getArgument(0));
        return null;
      }
    }).when(purchaseRepo).delete(any(Purchase.class));
    // customerRepo
    doThrow(new IllegalArgumentException()).when(customerRepo).delete(null);
    doAnswer(new Answer<Void>() {
      public Void answer(InvocationOnMock invocation) {
        customerData.remove(invocation.getArgument(0));
        return null;
      }
    }).when(customerRepo).delete(any(Customer.class));
  }

  private void setFindMocks() {
    // find operations look into data hashsets
    lenient().when(purchaseRepo.findById(anyLong())).thenAnswer((InvocationOnMock invocation) -> {
      for (Purchase p : purchaseData) {
        if (invocation.getArgument(0).equals(p.getId())) {
          return p;
        }
      }
      return null;
    });
    lenient().when(specificItemRepo.findById(anyLong()))
        .thenAnswer((InvocationOnMock invocation) -> {
          for (SpecificItem s : specificItemData) {
            if (invocation.getArgument(0).equals(s.getId())) {
              return s;
            }
          }
          return null;
        });
    lenient().when(itemRepo.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
      for (Item i : itemData) {
        if (invocation.getArgument(0).equals(i.getName())) {
          return i;
        }
      }
      return null;
    });
    lenient().when(customerRepo.findByUsername(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          for (Customer c : customerData) {
            if (invocation.getArgument(0).equals(c.getUsername())) {
              return c;
            }
          }
          return null;
        });
  }

  private void setSaveMocks() {
    // save operations write the data hashsets and return parameter object
    // autogenerated ID must be set manually prior to this step
    lenient().when(purchaseRepo.save(any(Purchase.class)))
        .thenAnswer((InvocationOnMock invocation) -> {
          // update the object if already present
          purchaseData.remove(invocation.getArgument(0));
          purchaseData.add(invocation.getArgument(0));
          return invocation.getArgument(0);
        });
    lenient().when(specificItemRepo.save(any(SpecificItem.class)))
        .thenAnswer((InvocationOnMock invocation) -> {
          specificItemData.remove(invocation.getArgument(0));
          specificItemData.add(invocation.getArgument(0));
          return invocation.getArgument(0);
        });
    lenient().when(itemRepo.save(any(Item.class))).thenAnswer((InvocationOnMock invocation) -> {
      itemData.remove(invocation.getArgument(0));
      itemData.add(invocation.getArgument(0));
      return invocation.getArgument(0);
    });
    lenient().when(customerRepo.save(any(Customer.class)))
        .thenAnswer((InvocationOnMock invocation) -> {
          customerData.remove(invocation.getArgument(0));
          customerData.add(invocation.getArgument(0));
          return invocation.getArgument(0);
        });
  }

}
