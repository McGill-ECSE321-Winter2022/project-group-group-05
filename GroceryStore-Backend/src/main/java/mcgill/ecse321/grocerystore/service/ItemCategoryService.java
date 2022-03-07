package mcgill.ecse321.grocerystore.service;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mcgill.ecse321.grocerystore.dao.ItemCategoryRepository;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;

@Service
public class ItemCategoryService {
  @Autowired
  ItemCategoryRepository itemCategoryRepository;

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
  public Set<Item> getItemsByItemCategory(String name) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Category name cannot be empty!");
    }
    ItemCategory itemCategory = getItemCategory(name);
    return itemCategory.getItems();
  }

  @Transactional
  public List<ItemCategory> getAllItemCategories() {
    return itemCategoryRepository.findAllByOrderByName();
  }
}
