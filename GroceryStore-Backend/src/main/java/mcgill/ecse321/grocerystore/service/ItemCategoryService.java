package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.ItemCategoryRepository;
import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;

/**
 * RESTful Service methods for Customer
 * 
 * @author Peini Cheng
 */
@Service
public class ItemCategoryService {
  @Autowired
  ItemCategoryRepository itemCategoryRepository;
  @Autowired
  ItemRepository itemRepository;

  /**
   * Creates a new item category with the given name
   * 
   * @param name name of the category
   * @return the ItemCategory instance created with the given name
   * @throws IllegalArgumentException
   */
  @Transactional
  public ItemCategory createItemCategory(String name) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Category name cannot be empty!");
    }
    if (itemCategoryRepository.findByName(name) != null) {
      throw new IllegalArgumentException("This category already exists!");
    }
    ItemCategory itemCategory = new ItemCategory();
    itemCategory.setName(name);
    return itemCategoryRepository.save(itemCategory);
  }

  /**
   * Deletes the specific Item Category
   * 
   * @param name name of the category
   * @throws IllegalArgumentException
   */
  public void delete(String name) throws IllegalArgumentException {
    ItemCategory ic = this.getItemCategory(name);
    itemCategoryRepository.delete(ic);
  }

  /**
   * Returns the ItemCategory instance with the given name
   * 
   * @param name name of the category
   * @return the ItemCategory instance with given username if there exists one
   * @throws IllegalArgumentException
   */
  @Transactional
  public ItemCategory getItemCategory(String name) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Category name cannot be empty!");
    }
    ItemCategory itemCategory = itemCategoryRepository.findByName(name);
    if (itemCategory == null) {
      throw new IllegalArgumentException("This category does not exist!");
    }
    return itemCategory;
  }

  /**
   * Adds the specific item to the specific category
   * 
   * @param itemName name of the item
   * @param categoryName name of the category
   * @return the ItemCategory instance after the item is added
   * @throws IllegalArgumentException
   */
  @Transactional
  public ItemCategory addItemToItemCategory(String itemName, String categoryName)
      throws IllegalArgumentException {
    if (itemName == null || itemName.trim().length() == 0) {
      throw new IllegalArgumentException("Item name cannot be empty!");
    }
    Item item = itemRepository.findByName(itemName);
    if (item == null) {
      throw new IllegalArgumentException("This item does not exist!");
    }
    ItemCategory itemCategory = getItemCategory(categoryName);
    if (itemCategory.addItem(item)) {
      return itemCategoryRepository.save(itemCategory);
    }
    throw new IllegalArgumentException("This item is already in this category!");
  }

  /**
   * Removes the specific item from the specific category
   * 
   * @param itemName name of the item
   * @param categoryName name of the category
   * @return the ItemCategory instance after the item is removed
   * @throws IllegalArgumentException
   */
  @Transactional
  public ItemCategory removeItemFromItemCategory(String itemName, String categoryName)
      throws IllegalArgumentException {
    if (itemName == null || itemName.trim().length() == 0) {
      throw new IllegalArgumentException("Item name cannot be empty!");
    }
    Item item = itemRepository.findByName(itemName);
    if (item == null) {
      throw new IllegalArgumentException("This item does not exist!");
    }
    ItemCategory itemCategory = getItemCategory(categoryName);
    if (itemCategory.removeItem(item)) {
      return itemCategoryRepository.save(itemCategory);
    }
    throw new IllegalArgumentException("This item isn't in this category!");
  }

  /**
   * Returns a list of items belongs to the specific category sorted in alphabetic order
   *
   * @param name
   * @return a sorted list of items
   * @throws IllegalArgumentException
   */
  @Transactional
  public List<Item> getItemsByItemCategory(String name) throws IllegalArgumentException {
    ItemCategory itemCategory = getItemCategory(name);
    List<Item> itemList = new ArrayList<>();
    for (Item item : itemCategory.getItems()) {
      itemList.add(item);
    }
    Collections.sort(itemList, new Comparator<Item>() {
      @Override
      public int compare(Item i1, Item i2) {
        return i1.getName().compareTo(i2.getName());
      }
    });
    return itemList;
  }

  /**
   * Returns a list of categories sorted in alphabetic order
   * 
   * @return a sorted list of ItemCategory instances
   */
  @Transactional
  public List<ItemCategory> getAllItemCategories() {
    return itemCategoryRepository.findAllByOrderByName();
  }

}
