package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mcgill.ecse321.grocerystore.dao.ItemCategoryRepository;
import mcgill.ecse321.grocerystore.model.ItemCategory;

@Service
public class ItemCategoryService {
  @Autowired
  ItemCategoryRepository itemCategoryRepository;

  @Transactional
  public ItemCategory createItemCategory(String name) {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Category name cannot be empty!");
    }
    if (itemCategoryRepository.findByName(name) != null) {
      throw new IllegalArgumentException("This category already existed!");
    }
    ItemCategory itemCategory = new ItemCategory();
    itemCategory.setName(name);
    itemCategoryRepository.save(itemCategory);
    return itemCategory;
  }

  @Transactional
  public ItemCategory getItemCategory(String name) {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Category name cannot be empty!");
    }
    ItemCategory itemCategory = itemCategoryRepository.findByName(name);
    return itemCategory;
  }

  @Transactional
  public List<ItemCategory> getAllItemCategorys() {
    return toList(itemCategoryRepository.findAll());
  }

  private <T> List<T> toList(Iterable<T> iterable) {
    List<T> resultList = new ArrayList<T>();
    for (T t : iterable) {
      resultList.add(t);
    }
    return resultList;
  }
}
