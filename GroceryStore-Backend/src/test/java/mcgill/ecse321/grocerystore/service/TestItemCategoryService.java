package mcgill.ecse321.grocerystore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import mcgill.ecse321.grocerystore.dao.ItemCategoryRepository;
import mcgill.ecse321.grocerystore.model.ItemCategory;

@ExtendWith(MockitoExtension.class)
public class TestItemCategoryService {
  @Mock
  private ItemCategoryRepository itemCategoryDao;

  @InjectMocks
  private ItemCategoryService service;

  private static final String ITEMCATEGORY_KEY = "TestItemCategory";
  private static final String NONEXISTING_KEY = "NotAnItemCategory";

  @BeforeEach
  public void setMockOutput() {
    lenient().when(itemCategoryDao.findByName(anyString()))
        .thenAnswer((InvocationOnMock invocation) -> {
          if (invocation.getArgument(0).equals(ITEMCATEGORY_KEY)) {
            ItemCategory itemCategory = new ItemCategory();
            itemCategory.setName(ITEMCATEGORY_KEY);
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
    assertEquals(0, service.getAllItemCategorys().size());

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
    assertEquals(0, service.getAllItemCategorys().size());

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
    assertEquals(0, service.getAllItemCategorys().size());

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
    assertEquals(0, service.getAllItemCategorys().size());

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
    assertEquals(0, service.getAllItemCategorys().size());

    ItemCategory itemCategory = null;
    String error = null;
    try {
      itemCategory = service.createItemCategory(ITEMCATEGORY_KEY);
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
    }
    assertNull(itemCategory);
    assertEquals("This category already existed!", error);
  }

  @Test
  public void testGetExistingPerson() {
    assertEquals(ITEMCATEGORY_KEY, service.getItemCategory(ITEMCATEGORY_KEY).getName());
  }

  @Test
  public void testGetNonExistingPerson() {
    assertNull(service.getItemCategory(NONEXISTING_KEY));
  }
}
