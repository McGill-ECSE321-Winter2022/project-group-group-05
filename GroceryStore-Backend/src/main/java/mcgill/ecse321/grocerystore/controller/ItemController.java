package mcgill.ecse321.grocerystore.controller;

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
import mcgill.ecse321.grocerystore.dto.ItemDto;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.service.ItemService;

/**
 * RESTful API for Employee service methods
 * 
 * @author Annie Kang
 */
@CrossOrigin(origins = "*")
@RestController
public class ItemController {
  @Autowired
  private ItemService service;

  @PostMapping(value = {"/item/{name}", "/item/{name}/"})
  public ItemDto createItem(@PathVariable("name") String name, @RequestParam double price,
      @RequestParam int inventory, @RequestParam boolean canDeliver,
      @RequestParam boolean canPickUp) throws IllegalArgumentException {
    return convertToDto(service.createItem(name, price, inventory, canDeliver, canPickUp));
  }

  @DeleteMapping(value = {"/item/{name}", "/item/{name}/"})
  public void delete(@PathVariable("name") String name) {
    service.delete(name);
  }

  @GetMapping(value = {"/item/{name}", "/item/{name}/"})
  public ItemDto getItem(@PathVariable("name") String name) throws IllegalArgumentException {
    return convertToDto(service.getItem(name));
  }

  @GetMapping(value = {"/item/getAll", "/item/getAll/"})
  public List<ItemDto> getAllItems() {
    return service.getAllItems().stream().map(p -> convertToDto(p)).collect(Collectors.toList());
  }

  @GetMapping(value = {"/item/allInStock", "/item/getAll/allInStock/"})
  public List<ItemDto> getAllInStock() {
    return service.getAllInStock().stream().map(p -> convertToDto(p)).collect(Collectors.toList());
  }

  @GetMapping(value = {"/item/allCanDeliver", "/item/getAll/allCanDeliver/"})
  public List<ItemDto> getAllCanDeliver() {
    return service.getAllCanDeliver().stream().map(p -> convertToDto(p))
        .collect(Collectors.toList());
  }

  @GetMapping(value = {"/item/allCanPickUp", "/item/getAll/allCanPickUp/"})
  public List<ItemDto> getAllCanPickUp() {
    return service.getAllCanPickUp().stream().map(p -> convertToDto(p))
        .collect(Collectors.toList());
  }

  @GetMapping(value = {"/item/allIsDiscontinued", "/item/getAll/allIsDiscontinued/"})
  public List<ItemDto> getAllIsDiscontinued() {
    return service.getAllIsDiscontinued().stream().map(p -> convertToDto(p))
        .collect(Collectors.toList());
  }

  @GetMapping(value = {"/item/searchAscending", "/item/searchAscending/"})
  public List<ItemDto> searchItemsAscending(@RequestParam String searchQuery) {
    return service.searchItemsAscending(searchQuery).stream().map(p -> convertToDto(p))
        .collect(Collectors.toList());

  }

  @GetMapping(value = {"/item/searchDescending", "/item/searchDescending/"})
  public List<ItemDto> searchItemsDescending(@RequestParam String searchQuery) {
    return service.searchItemsDescending(searchQuery).stream().map(p -> convertToDto(p))
        .collect(Collectors.toList());
  }

  @PatchMapping(value = {"/item/{name}/setPrice", "/item/{name}/setPrice/"})
  public ItemDto setPrice(@PathVariable("name") String name, @RequestParam double price)
      throws IllegalArgumentException {
    return convertToDto(service.setPrice(name, price));
  }

  @PatchMapping(value = {"/item/{name}/setInventory", "/item/{name}/setInventory/"})
  public ItemDto setInventory(@PathVariable("name") String name, @RequestParam int inventory)
      throws IllegalArgumentException {
    return convertToDto(service.setInventory(name, inventory));
  }

  @PatchMapping(value = {"/item/{name}/setCanDeliver", "/item/{name}/setCanDeliver/"})
  public ItemDto setCanDeliver(@PathVariable("name") String name, @RequestParam boolean canDeliver)
      throws IllegalArgumentException {
    return convertToDto(service.setCanDeliver(name, canDeliver));
  }

  @PatchMapping(value = {"/item/{name}/setCanPickUp", "/item/{name}/setCanPickUp/"})
  public ItemDto setCanPickUp(@PathVariable("name") String name, @RequestParam boolean canPickUp)
      throws IllegalArgumentException {
    return convertToDto(service.setCanPickUp(name, canPickUp));
  }

  @PatchMapping(value = {"/item/{name}/setIsDiscontinued", "/item/{name}/setIsDiscontinued/"})
  public ItemDto setIsDiscontinued(@PathVariable("name") String name,
      @RequestParam boolean isDiscontinued) throws IllegalArgumentException {
    return convertToDto(service.setIsDiscontinued(name, isDiscontinued));
  }

  private ItemDto convertToDto(Item i) throws IllegalArgumentException {
    return new ItemDto(i.getName(), i.getPrice(), i.getInventory(), i.getCanDeliver(),
        i.getCanPickUp(), i.getIsDiscontinued());
  }

}
