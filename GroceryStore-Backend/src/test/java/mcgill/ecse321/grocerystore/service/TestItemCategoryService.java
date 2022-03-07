package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import java.util.HashSet;
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
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;

@ExtendWith(MockitoExtension.class)
public class TestItemCategoryService {
  @Mock
  private ItemCategoryRepository itemCategoryDao;

  @InjectMocks
  private ItemCategoryService service;

  private static final String ITEMCATEGORY_KEY = "TestItemCategory";
  private static final String NONEXISTING_KEY = "NotAnItemCategory";
  private static final Item ITEM_ONE = new Item();
  private static final Item ITEM_TWO = new Item();

  @BeforeEach
  public void setMockOutput() {
    lenient().when(itemCategoryDao.findByName(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(ITEMCATEGORY_KEY)) {
            ItemCategory itemCategory = new ItemCategory();
            itemCategory.setName(ITEMCATEGORY_KEY);
            itemCategory.addItem(ITEM_ONE);
            itemCategory.addItem(ITEM_TWO);
            return itemCategory;
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
    Set<Item> items = new HashSet<Item>();
    items.add(ITEM_ONE);
    items.add(ITEM_TWO);
    Set<Item> itemSet = service.getItemsByItemCategory(ITEMCATEGORY_KEY);
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