package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.ItemCategoryRepository;
import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;
import mcgill.ecse321.grocerystore.dao.SpecificItemRepository;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.SpecificItem;

/**
 * 
 * @author Annie Kang
 *
 */
@Service
public class ItemService {
  @Autowired
  ItemRepository itemRepository;
  @Autowired
  PurchaseRepository purchaseRepository;
  @Autowired
  SpecificItemRepository specificItemRepository;
  @Autowired
  ItemCategoryRepository itemCategoryRepository;

  @Transactional
  public Item createItem(String name, double price, int inventory, boolean canDeliver,
      boolean canPickUp) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Item name cannot be empty!");
    }
    if (itemRepository.findByName(name) != null) {
      throw new IllegalArgumentException("Item name is already taken!");
    }
    if (price < 0.0) {
      throw new IllegalArgumentException("Item price cannot be negative!");
    }
    if (inventory < 0) {
      throw new IllegalArgumentException("Item inventory cannot be negative!");
    }

    Item item = new Item();
    item.setName(name);
    item.setPrice(price);
    item.setInventory(inventory);
    item.setCanDeliver(canDeliver);
    item.setCanPickUp(canPickUp);
    item.setIsDiscontinued(false);
    return itemRepository.save(item);
  }

  /**
   * Delete the item with the given name, while also removing any SpecificItems associated with this
   * Item.<br>
   * <b>This action should not be done under normal circumstances.</b><br>
   * The removal of an item from the store should be done through setIsDiscontinued. This method is
   * provided for maintenance purposes only and would alter purchase histories kept in the system.
   * 
   * @param itemName
   * @throws IllegalArgumentException
   */
  @Transactional
  public void delete(String itemName) throws IllegalArgumentException {
    Item item = this.getItem(itemName);
    // remove associations
    Iterator<Purchase> pIter =
        purchaseRepository.findAllByOrderByTimeOfPurchaseMillisDesc().iterator();
    // delete all SpecificItems referencing this item
    while (pIter.hasNext()) {
      Purchase p = pIter.next();
      Iterator<SpecificItem> sIter = p.getSpecificItems().iterator();
      while (sIter.hasNext()) {
        SpecificItem s = sIter.next();
        if (s.getItem().equals(item)) {
          sIter.remove();
          specificItemRepository.delete(s);
          break;
        }
      }
      purchaseRepository.save(p);
    }
    Iterator<ItemCategory> icIter = itemCategoryRepository.findAllByOrderByName().iterator();
    // remove this item from all ItemCategories
    while (icIter.hasNext()) {
      ItemCategory ic = icIter.next();
      Iterator<Item> iIter = ic.getItems().iterator();
      while (iIter.hasNext()) {
        Item i = iIter.next();
        if (i.equals(item)) {
          ic.removeItem(item);
        }
      }
      itemCategoryRepository.save(ic);
    }
    itemRepository.delete(item);
  }

  @Transactional
  public Item getItem(String itemName) throws IllegalArgumentException {
    if (itemName == null || itemName.trim().length() == 0) {
      throw new IllegalArgumentException("Item name cannot be empty!");
    }
    Item item = itemRepository.findByName(itemName);
    if (item == null) {
      throw new IllegalArgumentException("Item with name " + itemName + " does not exist!");
    }
    return item;
  }

  @Transactional
  public List<Item> getAllItems() {
    return itemRepository.findAllByOrderByName();
  }

  @Transactional
  public List<Item> getAllInStock() {
    ArrayList<Item> itemList = new ArrayList<Item>();
    for (Item i : itemRepository.findAllByOrderByName()) {
      if (i.getInventory() > 0 && !i.getIsDiscontinued()) {
        itemList.add(i);
      }
    }
    return itemList;
  }

  @Transactional
  public List<Item> getAllCanDeliver() {
    ArrayList<Item> itemList = new ArrayList<Item>();
    for (Item i : itemRepository.findAllByOrderByName()) {
      if (i.getCanDeliver() && !i.getIsDiscontinued()) {
        itemList.add(i);
      }
    }
    return itemList;
  }

  @Transactional
  public List<Item> getAllCanPickUp() {
    ArrayList<Item> itemList = new ArrayList<Item>();
    for (Item i : itemRepository.findAllByOrderByName()) {
      if (i.getCanPickUp() && !i.getIsDiscontinued()) {
        itemList.add(i);
      }
    }
    return itemList;
  }

  @Transactional
  public List<Item> getAllIsDiscontinued() {
    ArrayList<Item> itemList = new ArrayList<Item>();
    for (Item i : itemRepository.findAllByOrderByName()) {
      if (i.getIsDiscontinued()) {
        itemList.add(i);
      }
    }
    return itemList;
  }

  @Transactional
  public List<Item> searchItemsAscending(String searchQuery) throws IllegalArgumentException {
    if (searchQuery == null || searchQuery.trim().length() == 0) {
      throw new IllegalArgumentException("Search Query must not be empty!");
    }
    return itemRepository.findByNameIgnoreCaseContainingOrderByName(searchQuery);
  }

  @Transactional
  public List<Item> searchItemsDescending(String searchQuery) throws IllegalArgumentException {
    if (searchQuery == null || searchQuery.trim().length() == 0) {
      throw new IllegalArgumentException("Search Query must not be empty!");
    }
    return itemRepository.findByNameIgnoreCaseContainingOrderByNameDesc(searchQuery);
  }

  @Transactional
  public Item setPrice(String itemName, double price) {
    Item item = getItem(itemName);
    if (price < 0.0) {
      throw new IllegalArgumentException("Item price cannot be negative!");
    }
    item.setPrice(price);
    return itemRepository.save(item);
  }

  @Transactional
  public Item setInventory(String itemName, int inventory) {
    Item item = getItem(itemName);
    if (inventory < 0) {
      throw new IllegalArgumentException("Item inventory cannot be negative!");
    }
    item.setInventory(inventory);
    return itemRepository.save(item);
  }

  @Transactional
  public Item setCanDeliver(String itemName, boolean canDeliver) {
    Item item = getItem(itemName);
    item.setCanDeliver(canDeliver);
    return itemRepository.save(item);
  }

  @Transactional
  public Item setCanPickUp(String itemName, boolean canPickUp) {
    Item item = getItem(itemName);
    item.setCanPickUp(canPickUp);
    return itemRepository.save(item);
  }

  @Transactional
  public Item setIsDiscontinued(String itemName, boolean isDiscontinued) {
    Item item = getItem(itemName);
    item.setIsDiscontinued(isDiscontinued);
    return itemRepository.save(item);
  }

}
