package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import mcgill.ecse321.grocerystore.dao.ItemCategoryRepository;
import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;
import mcgill.ecse321.grocerystore.dao.SpecificItemRepository;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.SpecificItem;

/**
 * RESTful service tests for Item Class.
 *
 * @author Annie Kang
 *
 */
@ExtendWith(MockitoExtension.class)
public class TestItemService {

  @Mock
  private ItemRepository itemDao;
  @Mock
  private PurchaseRepository purchaseDao;
  @Mock
  private SpecificItemRepository specificItemDao;
  @Mock
  private ItemCategoryRepository itemCategoryDao;
  @Mock
  private Item mockItem;
  @Mock
  private SpecificItem mockSpecificItem;
  @Mock
  private Purchase mockPurchase;
  @Mock
  private ItemCategory mockItemCategory;

  @InjectMocks
  private ItemService service;

  private static final String ITEM_KEY = "Tomato";
  private static final String ITEM2_KEY = "Apple";
  private static final String FAKE_ITEM_KEY = "NotAnItem";

  @BeforeEach
  public void setMockOutput() {
    lenient().when(itemDao.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
      if (invocation.getArgument(0).equals(ITEM_KEY)) {
        return mockItem;
      } else {
        return null;
      }
    });
    // imitate getting an ordered list of item objects
    lenient().when(itemDao.findAllByOrderByName()).thenAnswer((InvocationOnMock invocation) -> {
      List<Item> itemList = new ArrayList<>();
      var itemOne = new Item();
      itemOne.setName(ITEM_KEY);
      itemOne.setCanDeliver(true);
      itemOne.setCanPickUp(true);
      itemOne.setInventory(1);
      itemOne.setIsDiscontinued(false);

      var itemTwo = new Item();
      itemTwo.setName(ITEM2_KEY);
      itemTwo.setCanDeliver(true);
      itemTwo.setCanPickUp(false);
      itemTwo.setInventory(10);
      itemTwo.setIsDiscontinued(true);

      itemList.add(itemOne);
      itemList.add(itemTwo);
      return itemList;
    });
    // imitate searching for items
    lenient().when(itemDao.findByNameIgnoreCaseContainingOrderByName(anyString())).thenAnswer(i -> {
      List<Item> itemList = new ArrayList<>();
      if (ITEM2_KEY.toUpperCase().contains(((String) i.getArgument(0)).toUpperCase())) {
        var item = new Item();
        item.setName(ITEM2_KEY);
        itemList.add(item);
      }
      if (ITEM_KEY.toUpperCase().contains(((String) i.getArgument(0)).toUpperCase())) {
        var item = new Item();
        item.setName(ITEM_KEY);
        itemList.add(item);
      }
      return itemList;
    });
    lenient().when(itemDao.findByNameIgnoreCaseContainingOrderByNameDesc(anyString()))
        .thenAnswer(i -> {
          List<Item> itemList = new ArrayList<>();
          if (ITEM_KEY.toUpperCase().contains(((String) i.getArgument(0)).toUpperCase())) {
            var item = new Item();
            item.setName(ITEM_KEY);
            itemList.add(item);
          }
          if (ITEM2_KEY.toUpperCase().contains(((String) i.getArgument(0)).toUpperCase())) {
            var item = new Item();
            item.setName(ITEM2_KEY);
            itemList.add(item);
          }
          return itemList;
        });
    // whenever anything is saved, just return the parameter object
    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };
    lenient().when(itemDao.save(any(Item.class))).thenAnswer(returnParameterAsAnswer);
    lenient().when(mockItem.getName()).thenAnswer((e) -> {
      return ITEM_KEY;
    });
    this.setMockForDelete();
  }

  private void setMockForDelete() {
    // mock for testDelete purchase
    lenient().when(purchaseDao.findAllByOrderByTimeOfPurchaseMillisDesc())
        .thenAnswer((InvocationOnMock invocation) -> {
          ArrayList<Purchase> purchases = new ArrayList<>();
          purchases.add(mockPurchase);
          return purchases;
        });
    lenient().when(mockPurchase.getSpecificItems()).thenAnswer((InvocationOnMock invocation) -> {
      HashSet<SpecificItem> spItems = new HashSet<>();
      spItems.add(mockSpecificItem);
      return spItems;
    });
    lenient().when(mockSpecificItem.getItem()).thenAnswer((InvocationOnMock invocation) -> {
      return mockItem;
    });
    lenient().when(mockItem.getName()).thenAnswer((InvocationOnMock invocation) -> {
      return ITEM_KEY;
    });
    // mock for testDelete itemCategory
    lenient().when(itemCategoryDao.findAllByOrderByName())
        .thenAnswer((InvocationOnMock invocation) -> {
          ArrayList<ItemCategory> itemCategories = new ArrayList<>();
          itemCategories.add(mockItemCategory);
          return itemCategories;
        });
    lenient().when(mockItemCategory.getItems()).thenAnswer((InvocationOnMock invocation) -> {
      HashSet<Item> items = new HashSet<>();
      items.add(mockItem);
      return items;
    });
  }

  // Test for class createItem
  @Test
  public void testCreateItem() {
    String itemName = "Banana";
    String itemImage = "fakeImage.com";
    double price = 2.5;
    int inventory = 10;
    Boolean canDeliver = true;
    Boolean canPickUp = true;
    Item item = null;
    try {
      item = service.createItem(itemName, itemImage, price, inventory, canDeliver, canPickUp);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(item);
    assertEquals(itemName, item.getName());
    assertEquals(price, item.getPrice(), 0.0);
    assertEquals(inventory, item.getInventory());
    assertEquals(canDeliver, item.getCanDeliver());
    assertEquals(canPickUp, item.getCanPickUp());
  }

  @Test
  public void testCreateItemNull() {
    Item item = null;
    String error = "";
    try {
      item = service.createItem(null, "", 0.0, 0, false, false);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testCreateItemEmpty() {
    Item item = null;
    String error = "";
    try {
      item = service.createItem("  ", "", 0.0, 0, false, false);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testCreateItemExisting() {
    String itemName = ITEM_KEY;
    double price = 2.5;
    int inventory = 10;
    Boolean canDeliver = true;
    Boolean canPickUp = true;
    Item item = null;
    String error = "";
    try {
      item = service.createItem(itemName, "", price, inventory, canDeliver, canPickUp);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    assertEquals("Item name is already taken!", error);
  }

  @Test
  public void testCreateItemNullImage() {
    String itemName = "negative price";
    double price = -10;
    int inventory = 10;
    Boolean canDeliver = true;
    Boolean canPickUp = true;
    Item item = null;
    String error = "";
    try {
      item = service.createItem(itemName, null, price, inventory, canDeliver, canPickUp);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    assertEquals("Item image cannot be null!", error);
  }

  @Test
  public void testCreateItemNegativePrice() {
    String itemName = "negative price";
    double price = -10;
    int inventory = 10;
    Boolean canDeliver = true;
    Boolean canPickUp = true;
    Item item = null;
    String error = "";
    try {
      item = service.createItem(itemName, "", price, inventory, canDeliver, canPickUp);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    assertEquals("Item price cannot be negative!", error);
  }

  @Test
  public void testCreateItemNegativeInventory() {
    String itemName = "negative inventory";
    double price = 2.5;
    int inventory = -10;
    Boolean canDeliver = true;
    Boolean canPickUp = true;
    Item item = null;
    String error = "";
    try {
      item = service.createItem(itemName, "", price, inventory, canDeliver, canPickUp);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    assertEquals("Item inventory cannot be negative!", error);
  }

  @Test
  public void testDeleteItem() {
    try {
      service.delete(ITEM_KEY);
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
    InOrder deleteOrder = inOrder(specificItemDao, mockItemCategory, itemDao);
    deleteOrder.verify(specificItemDao, times(1)).delete(mockSpecificItem);
    deleteOrder.verify(mockItemCategory, times(1)).removeItem(mockItem);
    deleteOrder.verify(itemDao, times(1)).delete(mockItem);
  }

  // Test for class getItem
  @Test
  public void testGetItem() {
    Item item = null;
    try {
      item = service.getItem(ITEM_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(item);
    assertEquals(ITEM_KEY, item.getName());
  }

  @Test
  public void testGetItemNull() {
    Item item = null;
    String error = "";
    try {
      item = service.getItem(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testGetItemEmpty() {
    Item item = null;
    String error = "";
    try {
      item = service.getItem(" ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testGetItemNonExistent() {
    Item item = null;
    String error = "";
    try {
      item = service.getItem(FAKE_ITEM_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    assertEquals("Item with name " + FAKE_ITEM_KEY + " does not exist!", error);
  }

  // test for getAllItems
  @Test
  public void testGetAllItems() {
    List<Item> itemList = service.getAllItems();
    assertEquals(2, itemList.size());
    assertEquals(ITEM_KEY, itemList.get(0).getName());
    assertEquals(ITEM2_KEY, itemList.get(1).getName());
  }

  // test for getAllInStock
  @Test
  public void testGetAllInStockItems() {
    List<Item> itemList = service.getAllInStock();
    assertEquals(1, itemList.size());
    assertEquals(ITEM_KEY, itemList.get(0).getName());
  }

  // test for getAllCanDeliver
  @Test
  public void testGetAllCanDeliverItems() {
    List<Item> itemList = service.getAllCanDeliver();
    assertEquals(1, itemList.size());
    assertEquals(ITEM_KEY, itemList.get(0).getName());
  }

  // test for getAllCanPickUP
  @Test
  public void testGetAllCanPickUpItems() {
    List<Item> itemList = service.getAllCanPickUp();
    assertEquals(1, itemList.size());
    assertEquals(ITEM_KEY, itemList.get(0).getName());
  }

  // test for getAllCanDeliver
  @Test
  public void testGetAllisDiscontinuedItem() {
    List<Item> itemList = service.getAllIsDiscontinued();
    assertEquals(1, itemList.size());
    assertEquals(ITEM2_KEY, itemList.get(0).getName());
  }

  @Test
  public void testSearchItemsAscending() {
    // Both Mock Items should appear in the list, in the order: two, one (Apple, Tomato)
    List<Item> itemList = null;
    try {
      itemList = service.searchItemsAscending("a");
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(itemList);
    assertEquals(2, itemList.size());
    assertEquals(ITEM2_KEY, itemList.get(0).getName());
    assertEquals(ITEM_KEY, itemList.get(1).getName());
  }

  @Test
  public void testSearchItemsAscendingNull() {
    List<Item> itemList = null;
    String error = "";
    try {
      itemList = service.searchItemsAscending(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemList);
    assertEquals("Search Query must not be empty!", error);
  }

  @Test
  public void testSearchItemsAscendingEmpty() {
    List<Item> itemList = null;
    String error = "";
    try {
      itemList = service.searchItemsAscending("  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemList);
    assertEquals("Search Query must not be empty!", error);
  }

  @Test
  public void testSearchItemsDescending() {
    // Both Mock Items should appear in the list, in the order: one, two (Tomato, Apple)
    List<Item> itemList = null;
    try {
      itemList = service.searchItemsDescending("a");
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(itemList);
    assertEquals(2, itemList.size());
    assertEquals(ITEM_KEY, itemList.get(0).getName());
    assertEquals(ITEM2_KEY, itemList.get(1).getName());
  }

  @Test
  public void testSearchItemsDescendingNull() {
    List<Item> itemList = null;
    String error = "";
    try {
      itemList = service.searchItemsDescending(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemList);
    assertEquals("Search Query must not be empty!", error);
  }

  @Test
  public void testSearchItemsDescendingEmpty() {
    List<Item> itemList = null;
    String error = "";
    try {
      itemList = service.searchItemsDescending("  ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemList);
    assertEquals("Search Query must not be empty!", error);
  }

  @Test
  public void testSetImage() {
    Item item = null;
    String newImage = "lessfakeImage.com";
    try {
      item = service.setImage(ITEM_KEY, newImage);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(item);
    verify(mockItem, times(1)).setImage(anyString());

  }

  @Test
  public void testSetImageNull() {
    Item item = null;
    String newImage = null;
    String error = "";
    try {
      item = service.setImage(ITEM_KEY, newImage);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    verify(mockItem, times(0)).setImage(anyString());
    assertEquals("Item image cannot be null!", error);
  }

  @Test
  public void testSetPrice() {
    Item item = null;
    double newPrice = 9.9;
    try {
      item = service.setPrice(ITEM_KEY, newPrice);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(item);
    verify(mockItem, times(1)).setPrice(anyDouble());
  }

  @Test
  public void testSetPriceNegative() {
    Item item = null;
    String error = "";
    double newPrice = -9.9;
    try {
      item = service.setPrice(ITEM_KEY, newPrice);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    verify(mockItem, times(0)).setPrice(anyDouble());
    assertEquals("Item price cannot be negative!", error);
  }

  @Test
  public void testSetInventory() {
    Item item = null;
    int newInventory = 20;
    try {
      item = service.setInventory(ITEM_KEY, newInventory);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(item);
    verify(mockItem, times(1)).setInventory(anyInt());
  }

  @Test
  public void testSetInventoryNegative() {
    Item item = null;
    String error = "";
    int newInventory = -20;
    try {
      item = service.setInventory(ITEM_KEY, newInventory);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(item);
    verify(mockItem, times(0)).setInventory(anyInt());
    assertEquals("Item inventory cannot be negative!", error);
  }

  @Test
  public void testSetCanDeliver() {
    Item item = null;
    boolean newBoo = true;
    try {
      item = service.setCanDeliver(ITEM_KEY, newBoo);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(item);
    verify(mockItem, times(1)).setCanDeliver(anyBoolean());
  }

  @Test
  public void testSetCanPickUp() {
    Item item = null;
    boolean newBoo = true;
    try {
      item = service.setCanPickUp(ITEM_KEY, newBoo);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(item);
    verify(mockItem, times(1)).setCanPickUp(anyBoolean());
  }

  @Test
  public void testSetIsDiscontinued() {
    Item item = null;
    boolean newBoo = true;
    try {
      item = service.setIsDiscontinued(ITEM_KEY, newBoo);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(item);
    verify(mockItem, times(1)).setIsDiscontinued(anyBoolean());
  }
}
