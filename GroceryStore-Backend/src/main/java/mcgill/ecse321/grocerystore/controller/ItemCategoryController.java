package mcgill.ecse321.grocerystore.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import mcgill.ecse321.grocerystore.dto.ItemCategoryDto;
import mcgill.ecse321.grocerystore.dto.ItemDto;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;
import mcgill.ecse321.grocerystore.service.ItemCategoryService;

/**
 * REST API for ItemCategory service methods
 *
 */
@CrossOrigin(origins = "*")
@RestController
public class ItemCategoryController {
  @Autowired
  private ItemCategoryService service;

  @GetMapping(value = {"/itemCategory", "/itemCategory/"})
  public List<ItemCategoryDto> getAllItemCategories() {
    return service.getAllItemCategories().stream().map(p -> convertToDto(p))
        .collect(Collectors.toList());
  }

  @PostMapping(value = {"/itemCategory/{name}", "/itemCategory/{name}/"})
  public ItemCategoryDto createItemCategory(@PathVariable("name") String name)
      throws IllegalArgumentException {
    ItemCategory itemCategory = service.createItemCategory(name);
    return convertToDto(itemCategory);
  }

  @DeleteMapping(value = {"/itemCategory/{name}", "/itemCategory/{name}/"})
  public void delete(@PathVariable("name") String name) throws IllegalArgumentException {
    service.delete(name);
  }

  @GetMapping(value = {"/itemCategory/{name}", "/itemCategory/{name}/"})
  public ItemCategoryDto getItemCategory(@PathVariable("name") String name)
      throws IllegalArgumentException {
    return convertToDto(service.getItemCategory(name));
  }

  @GetMapping(value = {"/itemCategory/{name}/getItems", "/itemCategory/{name}/getItems/"})
  public List<ItemDto> getItemsOfItemCategory(@PathVariable("name") String name) {
    return service.getItemsByItemCategory(name).stream().map(i -> this.convertToDto(i))
        .collect(Collectors.toList());
  }

  @PatchMapping(value = {"/itemCategory/{name}/addItem", "/itemCategory/{name}/addItem/"})
  public ItemCategoryDto addItemToItemCategory(@PathVariable("name") String name,
      @RequestParam String itemName) throws IllegalArgumentException {
    return convertToDto(service.addItemToItemCategory(itemName, name));
  }

  @PatchMapping(value = {"/itemCategory/{name}/removeItem", "/itemCategory/{name}/removeItem/"})
  public ItemCategoryDto removeItemFromItemCategory(@PathVariable("name") String name,
      @RequestParam String itemName) throws IllegalArgumentException {
    return convertToDto(service.removeItemFromItemCategory(itemName, name));
  }

  private List<ItemDto> createItemDtosForItemCategory(ItemCategory i) {
    List<Item> itemList = service.getItemsByItemCategory(i.getName());
    List<ItemDto> items = new ArrayList<>();
    for (Item item : itemList) {
      items.add(convertToDto(item));
    }
    return items;
  }

  private ItemDto convertToDto(Item i) throws IllegalArgumentException {
    if (i == null) {
      throw new IllegalArgumentException("Invalid item.");
    }
    return new ItemDto(i.getName(), i.getImage(), i.getPrice(), i.getInventory(), i.getCanDeliver(),
        i.getCanPickUp(), i.getIsDiscontinued());
  }

  private ItemCategoryDto convertToDto(ItemCategory i) throws IllegalArgumentException {
    if (i == null) {
      throw new IllegalArgumentException("There is no such Customer!");
    }
    ItemCategoryDto itemCategoryDto =
        new ItemCategoryDto(i.getName(), createItemDtosForItemCategory(i));
    return itemCategoryDto;
  }
}
