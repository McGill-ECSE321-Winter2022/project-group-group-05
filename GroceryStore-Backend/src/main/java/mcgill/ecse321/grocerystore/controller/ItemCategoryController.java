package mcgill.ecse321.grocerystore.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import mcgill.ecse321.grocerystore.dto.ItemCategoryDto;
import mcgill.ecse321.grocerystore.dto.ItemDto;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.ItemCategory;
import mcgill.ecse321.grocerystore.service.ItemCategoryService;

@CrossOrigin(origins = "*")
@RestController
public class ItemCategoryController {
  @Autowired
  private ItemCategoryService service;

  @GetMapping(value = {"/itemCategory", "/itemCategory/"})
  public List<ItemCategoryDto> getAllItemCategoriess() {
    return service.getAllItemCategories().stream().map(p -> convertToDto(p))
        .collect(Collectors.toList());
  }

  @PostMapping(value = {"/itemCategory/{name}", "/itemCategory/{name}/"})
  public ItemCategoryDto createItemCategory(@PathVariable("name") String name)
      throws IllegalArgumentException {
    ItemCategory itemCategory = service.createItemCategory(name);
    return convertToDto(itemCategory);
  }

  @GetMapping(value = {"/itemCategory/{name}", "/itemCategory/{name}/"})
  public ItemCategoryDto getItemCategory(@PathVariable("name") String name)
      throws IllegalArgumentException {
    return convertToDto(service.getItemCategory(name));
  }

  @GetMapping(value = {"/purchase/itemCategory/{name}", "/purchase/itemCategory/{name}/"})
  public List<ItemDto> getItemsOfItemCategory(@PathVariable("name") ItemCategoryDto iDto) {
    ItemCategory i = convertToDomainObject(iDto);
    return createItemDtosForItemCategory(i);
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
    return new ItemDto(i.getName(), i.getPrice(), i.getInventory(), i.getCanDeliver(),
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

  private ItemCategory convertToDomainObject(ItemCategoryDto iDto) {
    List<ItemCategory> allItemCategories = service.getAllItemCategories();
    for (ItemCategory itemCategory : allItemCategories) {
      if (itemCategory.getName().equals(iDto.getName())) {
        return itemCategory;
      }
    }
    return null;
  }
}
