package mcgill.ecse321.grocerystore.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import mcgill.ecse321.grocerystore.dto.ItemDto;
import mcgill.ecse321.grocerystore.dto.PurchaseDto;
import mcgill.ecse321.grocerystore.dto.SpecificItemDto;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.SpecificItem;
import mcgill.ecse321.grocerystore.service.PurchaseService;

/**
 * RESTful API for Purchase services
 * 
 * @author Jimmy Sheng
 */
@CrossOrigin(origins = "*")
@RestController
public class PurchaseController {

  @Autowired
  private PurchaseService service;

  @PostMapping(value = {"/purchase/addItem/{id}", "/purchase/addItem/{id}/"})
  public PurchaseDto addItemToCart(@PathVariable("id") long purchaseId,
      @RequestParam String itemName, @RequestParam int quantity) throws IllegalArgumentException {
    return this.toDto(service.addItemToCart(purchaseId, itemName, quantity));
  }

  @PostMapping(value = {"/purchase/cancel/{id}", "/purchase/cancel/{id}/"})
  public PurchaseDto cancel(@PathVariable("id") long purchaseId) throws IllegalArgumentException {
    return this.toDto(service.cancel(purchaseId));
  }

  @PostMapping(value = {"/purchase/complete/{id}", "/purchase/complete/{id}/"})
  public PurchaseDto complete(@PathVariable("id") long purchaseId) throws IllegalArgumentException {
    return this.toDto(service.complete(purchaseId));
  }

  @DeleteMapping(value = {"/purchase/delete/{id}", "/purchase/delete/{id}/"})
  public void delete(@PathVariable("id") long purchaseId, @RequestParam Optional<String> username)
      throws IllegalArgumentException {
    if (username.isPresent()) {
      service.delete(username.get(), purchaseId);
    } else {
      service.delete(purchaseId);
    }
  }

  @DeleteMapping(value = {"/purchase/deleteAll", "/purchase/deleteAll/"})
  public void deleteAll() throws IllegalArgumentException {
    service.deleteAll();
  }

  @GetMapping(value = {"/purchase/all", "/purchase/all/"})
  public List<PurchaseDto> getAll() {
    return service.getAll().stream().map(p -> this.toDto(p)).collect(Collectors.toList());
  }

  @GetMapping(value = {"/purchase/allCompleted", "/purchase/allCompleted/"})
  public List<PurchaseDto> getAllCompleted() {
    return service.getAllCompleted().stream().map(p -> this.toDto(p)).collect(Collectors.toList());
  }

  @GetMapping(value = {"/purchase/allDesc", "/purchase/allDesc/"})
  public List<PurchaseDto> getAllDesc() {
    return service.getAllDesc().stream().map(p -> this.toDto(p)).collect(Collectors.toList());
  }

  @GetMapping(value = {"/purchase/allPaid", "/purchase/allPaid/"})
  public List<PurchaseDto> getAllPaid() {
    return service.getAllPaid().stream().map(p -> this.toDto(p)).collect(Collectors.toList());
  }

  @GetMapping(value = {"/purchase/allPrepared", "/purchase/allPrepared/"})
  public List<PurchaseDto> getAllPrepared() {
    return service.getAllPrepared().stream().map(p -> this.toDto(p)).collect(Collectors.toList());
  }

  @PostMapping(value = {"/purchase/cart", "/purchase/cart/"})
  public PurchaseDto getCart(@RequestParam String username) throws IllegalArgumentException {
    return this.toDto(service.getCart(username));
  }

  @GetMapping(value = {"/purchase/{id}", "/purchase/{id}/"})
  public PurchaseDto getPurchase(@PathVariable("id") long purchaseId)
      throws IllegalArgumentException {
    return this.toDto(service.getPurchase(purchaseId));
  }

  @PostMapping(value = {"/purchase/pay/{id}", "/purchase/pay/{id}/"})
  public PurchaseDto pay(@PathVariable("id") long purchaseId) throws IllegalArgumentException {
    return this.toDto(service.pay(purchaseId));
  }

  @DeleteMapping(value = {"/purchase/pos/delete/{id}", "/purchase/pos/delete/{id}/"})
  public void posDeleteCart(@PathVariable("id") long purchaseId) throws IllegalArgumentException {
    service.posDeleteCart(purchaseId);
  }

  @PostMapping(value = {"/purchase/pos/cart", "/purchase/pos/cart/"})
  public PurchaseDto posGetNewCart() throws IllegalArgumentException {
    return this.toDto(service.posGetNewCart());
  }

  @PostMapping(value = {"/purchase/pos/pay/{id}", "/purchase/pos/pay/{id}/"})
  public PurchaseDto posPay(@PathVariable("id") long purchaseId) throws IllegalArgumentException {
    return this.toDto(service.posPay(purchaseId));
  }

  @DeleteMapping(value = {"/purchase/pos/purge", "/purchase/pos/purge/"})
  public void posPurgeCarts() throws IllegalArgumentException {
    service.posPurgeCarts();
  }

  @PostMapping(value = {"/purchase/prepare/{id}", "/purchase/prepare/{id}/"})
  public PurchaseDto prepare(@PathVariable("id") long purchaseId) throws IllegalArgumentException {
    return this.toDto(service.prepare(purchaseId));
  }

  @PostMapping(value = {"/purchase/setIsDelivery/{id}", "/purchase/setIsDelivery/{id}/"})
  public PurchaseDto setIsDelivery(@PathVariable("id") long purchaseId,
      @RequestParam boolean isDelivery) throws IllegalArgumentException {
    return this.toDto(service.setIsDelivery(purchaseId, isDelivery));
  }

  @PostMapping(value = {"/purchase/setItem/{id}", "/purchase/setItem/{id}/"})
  public PurchaseDto setItemQuantity(@PathVariable("id") long purchaseId,
      @RequestParam String itemName, @RequestParam int quantity) throws IllegalArgumentException {
    return this.toDto(service.setItemQuantity(purchaseId, itemName, quantity));
  }

  private ItemDto toDto(Item i) throws IllegalArgumentException {
    if (i == null) {
      throw new IllegalArgumentException("Invalid item.");
    }
    return new ItemDto(i.getName(), i.getImage(), i.getPrice(), i.getInventory(), i.getCanDeliver(),
        i.getCanPickUp(), i.getIsDiscontinued());
  }

  private PurchaseDto toDto(Purchase p) throws IllegalArgumentException {
    if (p == null) {
      throw new IllegalArgumentException("Invalid purcahse.");
    }
    ArrayList<SpecificItemDto> spItemsDto = new ArrayList<>();
    for (SpecificItem spItem : p.getSpecificItems()) {
      spItemsDto.add(this.toDto(spItem));
    }
    return new PurchaseDto(p.getId(), spItemsDto, p.getStateFullName(), p.getIsDelivery(),
        p.getTimeOfPurchaseMillis());
  }

  private SpecificItemDto toDto(SpecificItem s) throws IllegalArgumentException {
    if (s == null) {
      throw new IllegalArgumentException("Invalid specific item.");
    }
    return new SpecificItemDto(s.getId(), this.toDto(s.getItem()), s.getPurchaseQuantity(),
        s.getPurchasePrice());
  }

}
