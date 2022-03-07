package mcgill.ecse321.grocerystore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.dao.SpecificItemRepository;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.SpecificItem;

@Service
public class SpecificItemService {

	@Autowired
	ItemRepository itemRepository;
	@Autowired
	SpecificItemRepository specificItemRepository;

	@Transactional
	public SpecificItem createSpecificItem(String itemName, int qty)
			throws IllegalArgumentException {
		if (itemRepository.findByName(itemName) != null) {
			throw new IllegalArgumentException("This type of item does not exist!");
		}
		Item item = itemRepository.findByName(itemName);
		if (qty <= 0) {
			throw new IllegalArgumentException("Item quantity should be positive!");
		}
		if (qty >= item.getInventory()) {
			throw new IllegalArgumentException("Item quantity exceeds avaliable quantitiy!");
		}

		SpecificItem specificitem = new SpecificItem();
		specificitem.setItem(item);
		specificitem.setPurchaseQuantity(qty);
		specificitem.setPurchasePrice(item.getPrice());
		return specificItemRepository.save(specificitem);
	}

	@Transactional
	public SpecificItem getSpecificItem(long specificItemId) throws IllegalArgumentException {
		SpecificItem specificItem = specificItemRepository.findById(specificItemId);
		if (specificItem == null) {
			throw new IllegalArgumentException("Item with provided ID does not exist!");
		}
		return specificItem;
	}

	@Transactional
	public SpecificItem deleteSpecificItem(long specificitemID) throws IllegalArgumentException {
		SpecificItem specificItem = specificItemRepository.findById(specificitemID);
		if (specificItem == null) {
			throw new IllegalArgumentException("The specific item with ID \"" + specificitemID + "\" does not exist!");
		}
		specificItemRepository.delete(specificItem);
		return specificItem;
	}

}
