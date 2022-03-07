package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.model.Item;

@Service
public class ItemService {
	  @Autowired
	  ItemRepository itemRepository;

	  @Transactional
	  public Item createItem (String name, double price, int inventory,
	      Boolean canDeliver, boolean canPickUp) throws IllegalArgumentException {
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

	  @Transactional
	  public Item getItem (String itemname) throws IllegalArgumentException {
	    if (itemname == null || itemname.trim().length() == 0) {
	      throw new IllegalArgumentException("Item name cannot be empty!");
	    }
	    Item item = itemRepository.findByName(itemname);
	    if (item == null) {
			throw new IllegalArgumentException("Item with username \"" + itemname + "\" does not exist!");
	    }
	    return item;
	  }

	  @Transactional
		public Item deleteItem(String itemname) throws IllegalArgumentException {
	    if (itemname == null || itemname.trim().length() == 0) {
	      throw new IllegalArgumentException("Item name cannot be empty!");
	    }
	    Item item = getItem(itemname);
	    itemRepository.delete(item);
		return item;
	  }

	  @Transactional
	  public List<Item> getAllitems() {
	    ArrayList<Item> itemList = (ArrayList<Item>) itemRepository.findAll();
	    return itemList;
	  }
	  
	  @Transactional
	  public List<Item> getAllInStock() {
		ArrayList<Item> itemList = new ArrayList<Item>();
		for (Item i: itemRepository.findAll()) {
			if (i.getInventory() > 0) {
				itemList.add(i);
			}
		}
		return itemList;
	   }

		@Transactional
		public List<Item> getAllCanDeliver() {
			ArrayList<Item> itemList = new ArrayList<Item>();
			for (Item i : itemRepository.findAll()) {
				if (i.getCanDeliver() == true) {
					itemList.add(i);
				}
			}
			return itemList;
		}

		@Transactional
		public List<Item> getAllCanPickUp() {
			ArrayList<Item> itemList = new ArrayList<Item>();
			for (Item i : itemRepository.findAll()) {
				if (i.getCanPickUp() == true) {
					itemList.add(i);
				}
			}
			return itemList;
		}

		@Transactional
		public List<Item> getAllisDiscontinued() {
			ArrayList<Item> itemList = new ArrayList<Item>();
			for (Item i : itemRepository.findAll()) {
				if (i.getIsDiscontinued() == true) {
					itemList.add(i);
				}
			}
			return itemList;
		}

}
