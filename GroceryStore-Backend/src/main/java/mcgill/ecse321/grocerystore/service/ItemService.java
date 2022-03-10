package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mcgill.ecse321.grocerystore.dao.ItemCategoryRepository;
import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;
import mcgill.ecse321.grocerystore.dao.SpecificItemRepository;
import mcgill.ecse321.grocerystore.model.Item;

/**
 * 
 * @author Annie Kang
 * 
 * There is no delete item method in this class The item object is preserved in
 * the database so that purchase history is preserved The method
 * "setIsDiscontinued" acts as a delete method instead
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
	ItemCategoryRepository itemCatagoryRepository;

	@Transactional
	public Item createItem(String name, double price, int inventory, Boolean canDeliver, boolean canPickUp)
			throws IllegalArgumentException {
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
	public Item getItem(String itemname) throws IllegalArgumentException {
		if (itemname == null || itemname.trim().length() == 0) {
			throw new IllegalArgumentException("Item name cannot be empty!");
		}
		Item item = itemRepository.findByName(itemname);
		if (item == null) {
			throw new IllegalArgumentException("Item with name \"" + itemname + "\" does not exist!");
		}
		return item;
	}

	@Transactional
	public List<Item> getAllitems() {
		// ArrayList<Item> itemList = (ArrayList<Item>)
		// itemRepository.findAll();
		return itemRepository.findAllByOrderByName();
	}

	@Transactional
	public List<Item> getAllInStock() {
		ArrayList<Item> itemList = new ArrayList<Item>();
		for (Item i : itemRepository.findAll()) {
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
			if (i.getCanDeliver()) {
				itemList.add(i);
			}
		}
		return itemList;
	}

	@Transactional
	public List<Item> getAllCanPickUp() {
		ArrayList<Item> itemList = new ArrayList<Item>();
		for (Item i : itemRepository.findAll()) {
			if (i.getCanPickUp()) {
				itemList.add(i);
			}
		}
		return itemList;
	}

	@Transactional
	public List<Item> getAllIsDiscontinued() {
		ArrayList<Item> itemList = new ArrayList<Item>();
		for (Item i : itemRepository.findAll()) {
			if (i.getIsDiscontinued()) {
				itemList.add(i);
			}
		}
		return itemList;
	}

	@Transactional
	public Item setPrice(String itemname, double price) {
		Item item = getItem(itemname);
		if (price < 0.0) {
			throw new IllegalArgumentException("Item price cannot be negative!");
		}
		item.setPrice(price);
		return itemRepository.save(item);
	}

	@Transactional
	public Item setInventory(String itemname, int inventory) {
		Item item = getItem(itemname);
		if (inventory < 0) {
			throw new IllegalArgumentException("Item inventory cannot be negative!");
		}
		item.setInventory(inventory);
		return itemRepository.save(item);
	}

	@Transactional
	public Item setCanDeliver(String itemname, boolean canDeliver) {
		Item item = getItem(itemname);
		item.setCanDeliver(canDeliver);
		return itemRepository.save(item);
	}

	@Transactional
	public Item setCanPickUp(String itemname, boolean canPickUp) {
		Item item = getItem(itemname);
		item.setCanPickUp(canPickUp);
		return itemRepository.save(item);
	}

	@Transactional
	public Item setIsDiscontinued(String itemname, boolean isDiscontinued) {
		Item item = getItem(itemname);
		item.setIsDiscontinued(isDiscontinued);
		return itemRepository.save(item);
	}

}
