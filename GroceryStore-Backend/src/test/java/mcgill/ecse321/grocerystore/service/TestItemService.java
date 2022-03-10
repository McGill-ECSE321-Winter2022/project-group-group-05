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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

		lenient().when(itemDao.findAllByOrderByName()).thenAnswer((InvocationOnMock invocation) -> {
			List<Item> itemList = new ArrayList<Item>();
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

		// whenever anything is saved, just return the parameter object
		Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
			return invocation.getArgument(0);
		};

		lenient().when(itemDao.save(any(Item.class))).thenAnswer(returnParameterAsAnswer);
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
			item = service.createItem(itemname, price, inventory, canDeliver, canPickUp);
		} catch (IllegalArgumentException e) {
			fail();
		}
		assertNotNull(item);
		assertEquals(itemname, item.getName());
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
			item = service.createItem(null, 0.0, 0, false, false);
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
			item = service.createItem("  ", 0.0, 0, false, false);
		} catch (IllegalArgumentException e) {
			error = e.getMessage();
		}
		assertNull(item);
		assertEquals("Item name cannot be empty!", error);
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
		String itemname = "negative price";
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
		String itemname = "negative inventory";
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
		assertEquals("Item with name \"" + FAKE_ITEM_KEY + "\" does not exist!", error);
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
