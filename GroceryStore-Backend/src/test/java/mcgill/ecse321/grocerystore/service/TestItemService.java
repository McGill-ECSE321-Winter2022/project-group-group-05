package mcgill.ecse321.grocerystore.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.model.Item;

/**
 * RESTful service tests for Item Class.
 * 
 * @author Annie Kang
 */
@ExtendWith(MockitoExtension.class)
public class TestItemService {

	@Mock
	private ItemRepository itemDao;
	@Mock
	private Item mockItem;

	@InjectMocks
	private ItemService service;

	private static final String ITEM_KEY = "Tomato";
	private static final String ITEM2_KEY = "Apple";
	private static final String FAKE_ITEM_KEY = "NotAnItem";

	@BeforeEach
    public void setMockOutput() {
    lenient().when(itemDao.findByName(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(ITEM_KEY)) {
            return mockItem;
          } else {
            return null;
          }
        });
    lenient().when(itemDao.findAll()).thenAnswer((InvocationOnMock invocation) -> {
		List<Item> itemList = new ArrayList<Item>();
		var itemOne = new Item();
		itemOne.setName(ITEM_KEY);
		itemOne.setCanDeliver(false);
		itemOne.setCanPickUp(true);
		itemOne.setInventory(0);
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
    // whenever anything is saved, just return the parameter object
    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };
    
    lenient().when(itemDao.save(any(Item.class))).thenAnswer(returnParameterAsAnswer);
	lenient().when(mockItem.getName()).thenAnswer((e) -> {
      return ITEM_KEY;
    });
	// mock output for mock object keys
	lenient().when(mockItem.getName()).thenAnswer((e) -> {
		return ITEM_KEY;
	});
  }

	// Test for class createItem
	  @Test
	  public void testCreateItem() {
	    String itemname = "Banana";
	    double price = 2.5;
	    int inventory = 10;
	    Boolean canDeliver = true;
	    Boolean canPickUp = true;
	    Item item = null;
	    try {
	      item = service.createItem(itemname,price,inventory,canDeliver,canPickUp);
	    } catch (IllegalArgumentException e) {
	      fail();
	    }
	    assertNotNull(item);
	    assertEquals(itemname, item.getName());
	    assertEquals(price, item.getPrice(),0.0);
	    assertEquals(inventory, item.getInventory());
	    assertEquals(canDeliver, item.getCanDeliver());
	    assertEquals(canPickUp, item.getCanPickUp());
	  }
	  
	  @Test
	  public void testCreateItemNull() {
	    Item item = null;
	    String error = "";
	    try {
	      item = service.createItem(null, 0.0, 0, false, false);
	    } catch (IllegalArgumentException e) {
	      error = e.getMessage();
	    }
	    assertNull(item);
	    assertEquals(
				"Item username cannot be empty!", error);
	  }
	  
	  @Test
	  public void testCreateItemEmpty() {
	    Item item = null;
	    String error = "";
	    try {
	      item = service.createItem("  ", 0.0, 0, false, false);
	    } catch (IllegalArgumentException e) {
	      error = e.getMessage();
	    }
	    assertNull(item);
	    assertEquals(
				"Item username cannot be empty!", error);
	  }
	  
	  @Test
	  public void testCreateItemExisting() {
	    String itemname = ITEM_KEY;
	    double price = 2.5;
	    int inventory = 10;
	    Boolean canDeliver = true;
	    Boolean canPickUp = true;
	    Item item = null;
	    String error = "";
	    try {
			item = service.createItem(itemname, price, inventory, canDeliver, canPickUp);
	    } catch (IllegalArgumentException e) {
	      error = e.getMessage();
	    }
		assertNull(item);
		assertEquals("Item name is already taken!", error);
	  }

		@Test
		public void testCreateItemNegativePrice() {
			String itemname = ITEM_KEY;
			double price = -10;
			int inventory = 10;
			Boolean canDeliver = true;
			Boolean canPickUp = true;
			Item item = null;
			String error = "";
			try {
				item = service.createItem(itemname, price, inventory, canDeliver, canPickUp);
			} catch (IllegalArgumentException e) {
				error = e.getMessage();
			}
			assertNull(item);
			assertEquals("Item price cannot be negative!", error);
		}

		@Test
		public void testCreateItemNegativeInventory() {
			String itemname = ITEM_KEY;
			double price = 2.5;
			int inventory = -10;
			Boolean canDeliver = true;
			Boolean canPickUp = true;
			Item item = null;
			String error = "";
			try {
				item = service.createItem(itemname, price, inventory, canDeliver, canPickUp);
			} catch (IllegalArgumentException e) {
				error = e.getMessage();
			}
			assertNull(item);
			assertEquals("Item inventory cannot be negative!", error);
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
			assertEquals("Item with username \"" + FAKE_ITEM_KEY + "\" does not exist!", error);
		}

		// test for class deleteItem
		@Test
		public void testDeleteItem() {
			Item item = null;
			try {
				item = service.deleteItem(ITEM_KEY);
			} catch (IllegalArgumentException e) {
				fail();
			}
			verify(itemDao, times(1)).deleteById(anyString());
			assertNotNull(item);
			assertEquals(ITEM_KEY, item.getName());
		}

		@Test
		public void testDeleteItemNonExistent() {
			Item item = null;
			String error = "";
			try {
				item = service.deleteItem(FAKE_ITEM_KEY);
			} catch (IllegalArgumentException e) {
				error = e.getMessage();
			}
			verify(itemDao, times(0)).deleteById(anyString());
			assertNull(item);
			assertEquals("Item with username \"" + FAKE_ITEM_KEY + "\" does not exist!", error);
		}

		@Test
		public void testDeleteItemNull() {
			Item item = null;
			String error = "";
			try {
				item = service.deleteItem(null);
			} catch (IllegalArgumentException e) {
				error = e.getMessage();
			}
			assertNull(item);
			assertEquals("Item name cannot be empty!", error);
		}

		@Test
		public void testDeleteItemEmpty() {
			Item item = null;
			String error = "";
			try {
				item = service.deleteItem(" ");
			} catch (IllegalArgumentException e) {
				error = e.getMessage();
			}
			assertNull(item);
			assertEquals("Item name cannot be empty!", error);
		}

		// test for getAllItems
		@Test
		public void testGetAllItems() {
			List<Item> itemList = service.getAllitems();
			assertEquals(2, itemList.size());
			assertEquals(ITEM_KEY, itemList.get(0).getName());
			assertEquals(ITEM2_KEY, itemList.get(1).getName());
		}

		// test for getAllInStock
		@Test
		public void testGetAllInStockItems() {
			List<Item> itemList = service.getAllInStock();
			assertEquals(1, itemList.size());
			assertEquals(ITEM2_KEY, itemList.get(0).getName());
		}

		// test for getAllCanDeliver
		@Test
		public void testGetAllCanDeliverItems() {
			List<Item> itemList = service.getAllCanDeliver();
			assertEquals(1, itemList.size());
			assertEquals(ITEM2_KEY, itemList.get(0).getName());
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
			List<Item> itemList = service.getAllisDiscontinued();
			assertEquals(1, itemList.size());
			assertEquals(ITEM2_KEY, itemList.get(0).getName());
		}

}
