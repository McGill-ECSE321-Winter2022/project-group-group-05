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
import mcgill.ecse321.grocerystore.model.Purchase;

@Service
public class ItemCategoryService {
  @Autowired
  ItemCategoryRepository itemCategoryRepository;
  @Autowired
  ItemRepository itemRepository;

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

  @Transactional
  public boolean addItemToItemCategory(String itemName, String categoryName)
      throws IllegalArgumentException {
    if (itemName == null || itemName.trim().length() == 0) {
      throw new IllegalArgumentException("Item name cannot be empty!");
    }
    if (categoryName == null || categoryName.trim().length() == 0) {
      throw new IllegalArgumentException("Category name cannot be empty!");
    }
    Item item = itemRepository.findByName(itemName);
    if (item == null) {
      throw new IllegalArgumentException("This item does not exist!");
    }
    ItemCategory itemCategory = getItemCategory(categoryName);
    for (ItemCategory ic : getAllItemCategories()) {
      for (Item i : ic.getItems()) {
        if (i.equals(item)) {
          throw new IllegalArgumentException("This item already belongs to a category!");
        }
      }
    }
    return itemCategory.addItem(item);
  }

  @Transactional
  public boolean removeItemFromItemCategory(String itemName, String categoryName)
      throws IllegalArgumentException {
    if (itemName == null || itemName.trim().length() == 0) {
      throw new IllegalArgumentException("Item name cannot be empty!");
    }
    if (categoryName == null || categoryName.trim().length() == 0) {
      throw new IllegalArgumentException("Category name cannot be empty!");
    }
    Item item = itemRepository.findByName(itemName);
    if (item == null) {
      throw new IllegalArgumentException("This item does not exist!");
    }
    ItemCategory itemCategory = getItemCategory(categoryName);
    return itemCategory.removeItem(item);
  }

  /**
   * return a list of items sorted in alphabetic order
   * 
   * @param name
   * @return a sorted list of items
   * @throws IllegalArgumentException
   */
  @Transactional
  public List<Item> getItemsByItemCategory(String name) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Category name cannot be empty!");
    }
    ItemCategory itemCategory = getItemCategory(name);
    List<Item> itemList = new ArrayList<Item>();
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

  @Transactional
  public List<ItemCategory> getAllItemCategories() {
    return itemCategoryRepository.findAllByOrderByName();
  }
}
