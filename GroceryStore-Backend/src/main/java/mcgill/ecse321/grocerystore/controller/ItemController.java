package mcgill.ecse321.grocerystore.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mcgill.ecse321.grocerystore.dto.ItemDto;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.service.ItemService;

/**
 * RESTful API for Employee service methods
 * 
 * @author Annie Kang
 */
@CrossOrigin(origins = "*") @RestController
public class ItemController {
	@Autowired
	private ItemService service;

	@PostMapping(value = { "/item/{name}", "/item/{name}/" })
	public ItemDto createItem(@PathVariable("name") String name, @RequestParam double price,
			@RequestParam int inventory, @RequestParam boolean canDeliver, @RequestParam boolean canPickUp)
			throws IllegalArgumentException {
		return convertToDto(service.createItem(name, price, inventory, canDeliver, canPickUp));
	}

	@GetMapping(value = { "/item/{name}", "/item/{name}/" })
	public ItemDto getItem(@PathVariable("name") String name) throws IllegalArgumentException {
		return convertToDto(service.getItem(name));
	}

	@GetMapping(value = { "/item/getAll", "/item/getAll/" })
	public List<ItemDto> getAllItems() {
		return service.getAllItems().stream().map(p -> convertToDto(p)).collect(Collectors.toList());
	}

	@GetMapping(value = { "/item/allInStock", "/item/getAll/allInStock/" })
	public List<ItemDto> getAllInStock() {
		return service.getAllInStock().stream().map(p -> convertToDto(p)).collect(Collectors.toList());
	}

	@GetMapping(value = { "/item/allCanDeliver", "/item/getAll/allCanDeliver/" })
	public List<ItemDto> getAllCanDeliver() {
		return service.getAllCanDeliver().stream().map(p -> convertToDto(p)).collect(Collectors.toList());
	}

	@GetMapping(value = { "/item/allCanPickUp", "/item/getAll/allCanPickUp/" })
	public List<ItemDto> getAllCanPickUp() {
		return service.getAllCanPickUp().stream().map(p -> convertToDto(p)).collect(Collectors.toList());
	}

	@GetMapping(value = { "/item/allIsDiscontinued", "/item/getAll/allIsDiscontinued/" })
	public List<ItemDto> getAllIsDiscontinued() {
		return service.getAllIsDiscontinued().stream().map(p -> convertToDto(p)).collect(Collectors.toList());
	}

	@PatchMapping(value = { "/item/{name}/setPrice", "/item/{name}/setPrice/" })
	public void setPrice(@PathVariable("name") String name, @RequestParam double price)
			throws IllegalArgumentException {
		service.setPrice(name, price);
	}

	@PatchMapping(value = { "/item/{name}/setInventory", "/item/{name}/setInventory/" })
	public void setInventory(@PathVariable("name") String name, @RequestParam int inventory)
			throws IllegalArgumentException {
		service.setInventory(name, inventory);
	}

	@PatchMapping(value = { "/item/{name}/setCanDeliver", "/item/{name}/setCanDeliver/" })
	public void setCanDeliver(@PathVariable("name") String name, @RequestParam boolean canDeliver)
			throws IllegalArgumentException {
		service.setCanDeliver(name, canDeliver);
	}

	@PatchMapping(value = { "/item/{name}/setCanPickUp", "/item/{name}/setCanPickUp/" })
	public void setCanPickUp(@PathVariable("name") String name, @RequestParam boolean canPickUp)
			throws IllegalArgumentException {
		service.setCanPickUp(name, canPickUp);
	}

	@PatchMapping(value = { "/item/{name}/setIsDiscontinued", "/item/{name}/setIsDiscontinued/" })
	public void setIsDiscontinued(@PathVariable("name") String name, @RequestParam boolean isDiscontinued)
			throws IllegalArgumentException {
		service.setIsDiscontinued(name, isDiscontinued);
	}

	private ItemDto convertToDto(Item i) throws IllegalArgumentException {
		if (i == null) {
			throw new IllegalArgumentException("Invalid item.");
		}
		return new ItemDto(i.getName(), i.getPrice(), i.getInventory(), i.getCanDeliver(), i.getCanPickUp(),
				i.getIsDiscontinued());
	}

}
