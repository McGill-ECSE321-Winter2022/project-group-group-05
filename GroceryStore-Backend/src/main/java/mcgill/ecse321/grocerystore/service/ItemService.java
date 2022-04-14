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
 * RESTful Service methods for Item
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

  /**
   * Creates a new item with the given parameters
   * 
   * @param name name of the new item
   * @param image image of the new item
   * @param price price of the new item
   * @param inventory current stock of the new item
   * @param canDeliver whether the new item can be delivered
   * @param canPickUp whether the new item can be picked up
   * @return the new Item instance created
   * @throws IllegalArgumentException
   */
  @Transactional
  public Item createItem(String name, String image, double price, int inventory, boolean canDeliver,
      boolean canPickUp) throws IllegalArgumentException {
    if (name == null || name.trim().length() == 0) {
      throw new IllegalArgumentException("Item name cannot be empty!");
    }
    if (itemRepository.findByName(name) != null) {
      throw new IllegalArgumentException("Item name is already taken!");
    }
    if (image == null) {
      throw new IllegalArgumentException("Item image cannot be null!");
    }
    if (price < 0.0) {
      throw new IllegalArgumentException("Item price cannot be negative!");
    }
    if (inventory < 0) {
      throw new IllegalArgumentException("Item inventory cannot be negative!");
    }

    Item item = new Item();
    item.setName(name);
    item.setImage(image);
    item.setPrice(price);
    item.setInventory(inventory);
    item.setCanDeliver(canDeliver);
    item.setCanPickUp(canPickUp);
    item.setIsDiscontinued(false);
    return itemRepository.save(item);
  }

  /**
   * Deletes the item with the given name, while also removing any SpecificItems associated with
   * this Item.<br>
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

  /**
   * Returns the item with the given name
   * 
   * @param itemName name of the item
   * @return the Item instance with the given name
   * @throws IllegalArgumentException
   */
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

  /**
   * Returns a list of all items sorted in alphabetic order
   * 
   * @return a sorted list of Item instances
   */
  @Transactional
  public List<Item> getAllItems() {
    return itemRepository.findAllByOrderByName();
  }

  /**
   * Returns a list of items that are currently in stock sorted in alphabetic order
   * 
   * @return a sorted list of available Item instances
   */
  @Transactional
  public List<Item> getAllInStock() {
    ArrayList<Item> itemList = new ArrayList<>();
    for (Item i : itemRepository.findAllByOrderByName()) {
      if (i.getInventory() > 0 && !i.getIsDiscontinued()) {
        itemList.add(i);
      }
    }
    return itemList;
  }

  /**
   * 
   * Returns a list of items that can be delivered sorted in alphabetic order
   * 
   * @return a sorted list of Item instances available for delivery
   */
  @Transactional
  public List<Item> getAllCanDeliver() {
    ArrayList<Item> itemList = new ArrayList<>();
    for (Item i : itemRepository.findAllByOrderByName()) {
      if (i.getCanDeliver() && !i.getIsDiscontinued()) {
        itemList.add(i);
      }
    }
    return itemList;
  }

  /**
   * Returns a list of items that can be picked up sorted in alphabetic order
   * 
   * @return sorted list of Item instances available for pick-up
   */
  @Transactional
  public List<Item> getAllCanPickUp() {
    ArrayList<Item> itemList = new ArrayList<>();
    for (Item i : itemRepository.findAllByOrderByName()) {
      if (i.getCanPickUp() && !i.getIsDiscontinued()) {
        itemList.add(i);
      }
    }
    return itemList;
  }

  /**
   * Returns a list of discontinued items sorted in alphabetic order
   * 
   * @return sorted list of Item instances that are discontinued
   */
  @Transactional
  public List<Item> getAllIsDiscontinued() {
    ArrayList<Item> itemList = new ArrayList<>();
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

  /**
   * Changes the image of the specific item
   * 
   * @param itemName name of the item
   * @param image new image of the item
   * @return the Item instance after the image is changed
   */
  @Transactional
  public Item setImage(String itemName, String image) {
    Item item = getItem(itemName);
    if (image == null) {
      throw new IllegalArgumentException("Item image cannot be null!");
    }
    item.setImage(image);
    return itemRepository.save(item);
  }

  /**
   * Changes the price of the specific item
   * 
   * @param itemName name of the item
   * @param price new price of the item
   * @return the Item instance after the price is changed
   */
  @Transactional
  public Item setPrice(String itemName, double price) {
    Item item = getItem(itemName);
    if (price < 0.0) {
      throw new IllegalArgumentException("Item price cannot be negative!");
    }
    item.setPrice(price);
    return itemRepository.save(item);
  }

  /**
   * Changes the current stock of the specific item
   * 
   * @param itemName name of the item
   * @param inventory inventory of the item
   * @return the Item instance after the current inventory is changed
   */
  @Transactional
  public Item setInventory(String itemName, int inventory) {
    Item item = getItem(itemName);
    if (inventory < 0) {
      throw new IllegalArgumentException("Item inventory cannot be negative!");
    }
    item.setInventory(inventory);
    return itemRepository.save(item);
  }

  /**
   * Sets if the specific item is available for delivery
   * 
   * @param itemName name of the item
   * @param whether the item can be delivered
   * @return the Item instance after the change
   */
  @Transactional
  public Item setCanDeliver(String itemName, boolean canDeliver) {
    Item item = getItem(itemName);
    item.setCanDeliver(canDeliver);
    return itemRepository.save(item);
  }

  /**
   * Sets if the specific item is available for pick-up
   * 
   * @param itemName name of the item
   * @param whether the item can be picked up
   * @return the Item instance after the change
   */
  @Transactional
  public Item setCanPickUp(String itemName, boolean canPickUp) {
    Item item = getItem(itemName);
    item.setCanPickUp(canPickUp);
    return itemRepository.save(item);
  }

  /**
   * Sets if the specific item is discontinued
   * 
   * @param itemName name of the item
   * @param isDiscontinued whether the item is discontinued
   * @return the Item instance after the change
   */
  @Transactional
  public Item setIsDiscontinued(String itemName, boolean isDiscontinued) {
    Item item = getItem(itemName);
    item.setIsDiscontinued(isDiscontinued);
    return itemRepository.save(item);
  }

}
