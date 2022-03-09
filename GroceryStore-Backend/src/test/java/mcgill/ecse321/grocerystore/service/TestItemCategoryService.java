package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;

@ExtendWith(MockitoExtension.class)
public class TestItemCategoryService {
  @Mock
  private ItemCategoryRepository itemCategoryDao;
  @Mock
  private ItemRepository itemDao;

  @InjectMocks
  private ItemCategoryService service;

  private static final String ITEMCATEGORY_KEY = "TestItemCategory";
  private static final String ITEMCATEGORY_TWO = "ItemCategoryTwo";
  private static final String NONEXISTING_KEY = "NotAnItemCategory";
  private static final String ITEM_THREE = "TestItem";
  private static final String NONEXISTING_ITEM = "NotAnItem";
  private static final String ITEM_ONE_NAME = "ItemOne";
  private static final Item ITEM_ONE = new Item();
  private static final Item ITEM_TWO = new Item();

  @BeforeEach
  public void setMockOutput() {
    lenient().when(itemCategoryDao.findByName(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(ITEMCATEGORY_KEY)) {
            ItemCategory itemCategory = new ItemCategory();
            itemCategory.setName(ITEMCATEGORY_KEY);
            ITEM_ONE.setName(ITEM_ONE_NAME);
            Set<Item> set = new HashSet<Item>();
            set.add(ITEM_ONE);
            try {
              Thread.sleep(50);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            set.add(ITEM_TWO);
            itemCategory.setItems(set);
            return itemCategory;
          } else if (invocation.getArgument(0).equals(ITEMCATEGORY_TWO)) {
            ItemCategory itemCategory = new ItemCategory();
            itemCategory.setName(ITEMCATEGORY_TWO);
            return itemCategory;
          } else {
            return null;
          }
        });

    lenient().when(itemCategoryDao.findAllByOrderByName())
        .thenAnswer((InvocationOnMock invocation) -> {
          List<ItemCategory> list = new ArrayList<ItemCategory>();
          ItemCategory itemCategory = new ItemCategory();
          itemCategory.setName(ITEMCATEGORY_KEY);
          ITEM_ONE.setName(ITEM_ONE_NAME);
          itemCategory.addItem(ITEM_ONE);
          itemCategory.addItem(ITEM_TWO);
          list.add(itemCategory);
          return list;
        });

    lenient().when(itemDao.findByName(anyString())).thenAnswer((InvocationOnMock invocation) -> {
      if (invocation.getArgument(0).equals(ITEM_THREE)) {
        Item item = new Item();
        item.setName(ITEM_THREE);
        return item;
      } else if (invocation.getArgument(0).equals(ITEM_ONE_NAME)) {
        ITEM_ONE.setName(ITEM_ONE_NAME);
        return ITEM_ONE;
      } else {
        return null;
      }
    });
    Answer<?> returnParameterAsAnswer = (InvocationOnMock invocation) -> {
      return invocation.getArgument(0);
    };
    lenient().when(itemCategoryDao.save(any(ItemCategory.class)))
        .thenAnswer(returnParameterAsAnswer);
  }

  @Test
  public void testCreateItemCategory() {
    String name = "test";
    ItemCategory itemCategory = null;
    try {
      itemCategory = service.createItemCategory(name);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(itemCategory);
    assertEquals(name, itemCategory.getName());
  }

  @Test
  public void testCreateItemCategoryNull() {
    String name = null;
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.createItemCategory(name);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testCreateItemCategoryEmpty() {
    String name = "";
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.createItemCategory(name);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testCreateItemCategorySpaces() {
    String name = "   ";
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.createItemCategory(name);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testCreateExistingItemCategory() {
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.createItemCategory(ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("This category already exists!", error);
  }

  @Test
  public void testAddItemToItemCategory() {
    boolean success = false;
    try {
      success = service.addItemToItemCategory(ITEM_THREE, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertTrue(success);
  }

  @Test
  public void testAddOtherTypeItemToItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory(ITEM_ONE_NAME, ITEMCATEGORY_TWO);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("This item already belongs to a category!", error);
  }

  @Test
  public void testAddNullItemToItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory(null, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testAddEmptyItemToItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory("", ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testAddSpacesItemToItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory("   ", ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testAddNonExistingItemToItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory(NONEXISTING_ITEM, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("This item does not exist!", error);
  }

  @Test
  public void testAddItemToNullItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory(ITEM_THREE, null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testAddItemToEmptyItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory(ITEM_THREE, "");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testAddItemToSpacesItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory(ITEM_THREE, "   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testAddItemToNonExistingItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory(ITEM_THREE, NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("This category does not exist!", error);
  }

  @Test
  public void testRemoveItemFromItemCategory() {
    boolean success = false;
    try {
      success = service.removeItemFromItemCategory(ITEM_ONE_NAME, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertTrue(success);
  }

  @Test
  public void testRemoveOtherTypeItemFromItemCategory() {
    boolean success = false;
    try {
      success = service.removeItemFromItemCategory(ITEM_ONE_NAME, ITEMCATEGORY_TWO);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertFalse(success);
  }

  @Test
  public void testRemoveNullItemFromItemCategory() {
    String error = null;
    try {
      service.removeItemFromItemCategory(null, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testRemoveEmptyItemFromItemCategory() {
    String error = null;
    try {
      service.removeItemFromItemCategory("", ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testRemoveSpacesItemFromItemCategory() {
    String error = null;
    try {
      service.removeItemFromItemCategory("   ", ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testRemoveNonExistingItemFromItemCategory() {
    String error = null;
    try {
      service.removeItemFromItemCategory(NONEXISTING_ITEM, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("This item does not exist!", error);
  }

  @Test
  public void testRemoveItemFromNullItemCategory() {
    String error = null;
    try {
      service.removeItemFromItemCategory(ITEM_ONE_NAME, null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testRemoveItemFromEmptyItemCategory() {
    String error = null;
    try {
      service.removeItemFromItemCategory(ITEM_ONE_NAME, "");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testRemoveItemFromSpacesItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory(ITEM_ONE_NAME, "   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testRemoveItemFromNonExistingItemCategory() {
    String error = null;
    try {
      service.addItemToItemCategory(ITEM_ONE_NAME, NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("This category does not exist!", error);
  }

  @Test
  public void testGetExistingItemCategory() {
    assertEquals(ITEMCATEGORY_KEY, service.getItemCategory(ITEMCATEGORY_KEY).getName());
  }

  @Test
  public void testGetNullItemCategory() {
    String error = null;
    try {
      service.getItemCategory(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testGetEmptyItemCategory() {
    String error = null;
    try {
      service.getItemCategory("");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testGetSpacesItemCategory() {
    String error = null;
    try {
      service.getItemCategory("   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testGetNonExistingItemCategory() {
    String error = null;
    try {
      service.getItemCategory(NONEXISTING_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("This category does not exist!", error);
  }

  @Test
  public void testGetItemsByItemCategory() {
    List<Item> items = new ArrayList<Item>();
    items.add(ITEM_ONE);
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    items.add(ITEM_TWO);
    List<Item> itemSet = service.getItemsByItemCategory(ITEMCATEGORY_KEY);
    assertEquals(items, itemSet);
  }

  @Test
  public void testGetItemsByNullItemCategory() {
    String error = null;
    try {
      service.getItemsByItemCategory(null);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testGetItemsByEmptyItemCategory() {
    String error = null;
    try {
      service.getItemsByItemCategory("");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }

  @Test
  public void testGetItemsBySpacesItemCategory() {
    String error = null;
    try {
      service.getItemsByItemCategory("   ");
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertEquals("Category name cannot be empty!", error);
  }
}
