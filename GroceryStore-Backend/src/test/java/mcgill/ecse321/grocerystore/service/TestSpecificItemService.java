package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.dao.SpecificItemRepository;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.SpecificItem;

/**
 * RESTful service tests for Specific Item Class.
 * 
 * @author Annie Kang
 */
@ExtendWith(MockitoExtension.class)
public class TestSpecificItemService {

	@Mock
	private ItemRepository itemDao;
	@Mock
	private SpecificItemRepository specificItemDao;
	@Mock
	private Item mockItem;
	@Mock
	private Item existingMockItem;
	@Mock
	private SpecificItem mockSpecificItem;

	@InjectMocks
	private SpecificItemService service;

	private static final Long SPECIFICITEM_KEY = (long) 001;
	private static final Long SPECIFICITEM2_KEY = (long) 002;
	private static final Long FAKE_SPECIFICITEM_KEY = (long) 007;

	private static final String ITEM_KEY = "Tomato";
	private static final String EXISTING_ITEM_KEY = "Apple";
	private static final String FAKE_ITEM_KEY = "fake";

	private static final int EXISTING_INVENTORY = 5;

	@BeforeEach
	public void setMockOutput() {
		lenient().when(specificItemDao.findById(anyLong())).thenAnswer((InvocationOnMock invocation) -> {
			if (invocation.getArgument(0).equals(SPECIFICITEM_KEY)) {
				return mockSpecificItem;
			} else {
				return null;
			}
		});

		lenient().when(itemDao.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
			if (invocation.getArgument(0).equals(ITEM_KEY)) {
				return mockItem;
			} else if (invocation.getArgument(0).equals(EXISTING_ITEM_KEY)) {
				return existingMockItem;
			} else {
				return null;
			}
		});

		lenient().when(mockItem.getInventory()).thenAnswer((InvocationOnMock invocation) -> {
			return EXISTING_INVENTORY;
		});
		// whenever anything is saved, just return the parameter object
		Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
			return invocation.getArgument(0);
		};

		lenient().when(itemDao.save(any(Item.class))).thenAnswer(returnParameterAsAnswer);
		lenient().when(specificItemDao.save(any(SpecificItem.class))).thenAnswer(returnParameterAsAnswer);
		lenient().when(mockSpecificItem.getId()).thenAnswer((e) -> {
			return SPECIFICITEM_KEY;
		});
		// mock output for mock object keys
		lenient().when(mockItem.getName()).thenAnswer((e) -> {
			return ITEM_KEY;
		});
	  }
		// Test for class createItem
		  @Test
		  public void testCreateSpecificItem() {
				int qty = 2;
			String itemName = ITEM_KEY;
		    SpecificItem specificitem = null;
		    try {
				specificitem = service.createSpecificItem(itemName, qty);
		    } catch (IllegalArgumentException e) {
				fail();
		    }
		    assertNotNull(specificitem);
		    assertEquals(qty, specificitem.getPurchaseQuantity());
			assertEquals(itemName, specificitem.getItem().getName());
		  
		  }

		  @Test
		  public void testCreateSpecificItemNoneExistingItem() {
				String error = "";
				int qty = 2;
			String itemName = FAKE_ITEM_KEY;
		    SpecificItem specificitem = null;
		    try {
				specificitem = service.createSpecificItem(itemName, qty);
		    } catch (IllegalArgumentException e) {
				error = e.getMessage();
		    }
			assertNull(specificitem);
			assertEquals("This type of item does not exist!", error);
		}

		@Test
		public void testCreateSpecificItemInvalidQuantity() {
			String error = "";
			int qty = -10;
			String itemName = ITEM_KEY;
			SpecificItem specificitem = null;
			try {
				specificitem = service.createSpecificItem(itemName, qty);
			} catch (IllegalArgumentException e) {
				error = e.getMessage();
			}
			assertNull(specificitem);
			assertEquals("Item quantity should be positive!", error);
		  
		  }

			@Test
			public void testCreateSpecificItemExceedQuantity() {
				String error = "";
				int qty = 10;
				String itemName = ITEM_KEY;
				SpecificItem specificitem = null;
				try {
					specificitem = service.createSpecificItem(itemName, qty);
				} catch (IllegalArgumentException e) {
					error = e.getMessage();
				}
				assertNull(specificitem);
				assertEquals("Item quantity exceeds avaliable quantitiy!", error);

			}

			// Test for class getItem
			@Test
			public void testGetSpecificItem() {
				SpecificItem specificItem = null;
				try {
					specificItem = service.getSpecificItem(SPECIFICITEM_KEY);
				} catch (IllegalArgumentException e) {
					fail();
				}
				assertNotNull(specificItem);
				assertEquals(SPECIFICITEM_KEY.longValue(), specificItem.getId());
			}

			@Test
			public void testGetISpecificItemNotExist() {
				SpecificItem specificItem = null;
				String error = "";
				try {
					specificItem = service.getSpecificItem(FAKE_SPECIFICITEM_KEY);
				} catch (IllegalArgumentException e) {
					error = e.getMessage();
				}
				assertNull(specificItem);
				assertEquals("Item with provided ID does not exist!", error);
			}

			// test for class deleteSpecific Item
			@Test
			public void testDeleteSpecificItem() {
				SpecificItem specificItem = null;
				try {
					specificItem = service.deleteSpecificItem(SPECIFICITEM_KEY);
				} catch (IllegalArgumentException e) {
					fail();
				}
				verify(itemDao, times(0)).deleteById(anyString());
				assertNotNull(specificItem);
				assertEquals(SPECIFICITEM_KEY.longValue(), specificItem.getId());
			}

			@Test
			public void testDeleteItemNonExistent() {
				SpecificItem specificItem = null;
				String error = "";
				try {
					specificItem = service.deleteSpecificItem(FAKE_SPECIFICITEM_KEY);
				} catch (IllegalArgumentException e) {
					error = e.getMessage();
				}
				verify(itemDao, times(0)).deleteById(anyString());
				assertNull(specificItem);
				assertEquals("The specific item with ID \"" + FAKE_SPECIFICITEM_KEY + "\" does not exist!", error);
			}
		  

}

