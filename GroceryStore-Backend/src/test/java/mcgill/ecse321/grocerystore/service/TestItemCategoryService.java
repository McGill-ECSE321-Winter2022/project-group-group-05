package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
  private static final String ITEM_TWO_NAME = "ItemTwo";
  private static final Item ITEM_ONE = new Item();
  private static final Item ITEM_TWO = new Item();

  @BeforeEach
  public void setMockOutput() {
    lenient().when(itemCategoryDao.findByName(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(ITEMCATEGORY_KEY)) {
            // generate ItemCategory and all fields
            ItemCategory itemCategory = new ItemCategory();
            itemCategory.setName(ITEMCATEGORY_KEY);
            ITEM_ONE.setName(ITEM_ONE_NAME);
            ITEM_TWO.setName(ITEM_TWO_NAME);
            Set<Item> set = new HashSet<>();
            set.add(ITEM_ONE);
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
    // imitate ordered list of ItemCategory objects
    lenient().when(itemCategoryDao.findAllByOrderByName())
        .thenAnswer((InvocationOnMock invocation) -> {
          List<ItemCategory> list = new ArrayList<>();
          ItemCategory itemCategory = new ItemCategory();
          itemCategory.setName(ITEMCATEGORY_KEY);
          ITEM_ONE.setName(ITEM_ONE_NAME);
          ITEM_TWO.setName(ITEM_TWO_NAME);
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
    ItemCategory itemCategory = null;
    try {
      itemCategory = service.addItemToItemCategory(ITEM_THREE, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(itemCategory);
  }

  @Test
  public void testAddExistingItemToItemCategory() {
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.addItemToItemCategory(ITEM_ONE_NAME, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("This item is already in this category!", error);
  }

  @Test
  public void testAddNullItemToItemCategory() {
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.addItemToItemCategory(null, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testAddEmptyItemToItemCategory() {
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.addItemToItemCategory("   ", ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testAddNonExistingItemToItemCategory() {
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.addItemToItemCategory(NONEXISTING_ITEM, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("This item does not exist!", error);
  }

  @Test
  public void testRemoveItemFromItemCategory() {
    ItemCategory itemCategory = null;
    try {
      itemCategory = service.removeItemFromItemCategory(ITEM_ONE_NAME, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      fail();
    }
    assertNotNull(itemCategory);
  }

  @Test
  public void testRemoveNotPresentItemFromItemCategory() {
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.removeItemFromItemCategory(ITEM_THREE, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("This item isn't in this category!", error);
  }

  @Test
  public void testRemoveNullItemFromItemCategory() {
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.removeItemFromItemCategory(null, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testRemoveEmptyItemFromItemCategory() {
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.removeItemFromItemCategory("   ", ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("Item name cannot be empty!", error);
  }

  @Test
  public void testRemoveNonExistingItemFromItemCategory() {
    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.removeItemFromItemCategory(NONEXISTING_ITEM, ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("This item does not exist!", error);
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
    List<Item> items = new ArrayList<>();
    items.add(ITEM_ONE);
    items.add(ITEM_TWO);
    List<Item> itemSet = service.getItemsByItemCategory(ITEMCATEGORY_KEY);
    assertEquals(items, itemSet);
  }

  @Test
  public void testDelete() {
    try {
      service.delete(ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      fail(e.getMessage());
    }
  }

}
