package mcgill.ecse321.grocerystore.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import mcgill.ecse321.grocerystore.dto.CustomerDto;
import mcgill.ecse321.grocerystore.dto.ItemDto;
import mcgill.ecse321.grocerystore.dto.PurchaseDto;
import mcgill.ecse321.grocerystore.dto.SpecificItemDto;
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.SpecificItem;
import mcgill.ecse321.grocerystore.service.CustomerService;

/**
 * REST API for Customer service methods
 *
 */
@CrossOrigin(origins = "*")
@RestController
public class CustomerController {
  @Autowired
  private CustomerService service;

  @GetMapping(value = {"/customer", "/customer/"})
  public List<CustomerDto> getAllCustomers() {
    return service.getAllCustomers().stream().map(p -> convertToDto(p))
        .collect(Collectors.toList());
  }

  @PostMapping(value = {"/customer/{username}", "/customer/{username}/"})
  public CustomerDto createCustomer(@PathVariable("username") String username,
      @RequestParam String password, @RequestParam String email, @RequestParam String address,
      @RequestParam boolean isLocal) throws IllegalArgumentException {
    Customer customer = service.createCustomer(username, password, email, address, isLocal);
    return convertToDto(customer);
  }

  @GetMapping(value = {"/customer/{username}", "/customer/{username}/"})
  public CustomerDto getCustomer(@PathVariable("username") String username)
      throws IllegalArgumentException {
    return convertToDto(service.getCustomer(username));
  }

  @GetMapping(value = {"/customer/{username}/getPurchases", "/customer/{username}/getPurchases/"})
  public List<PurchaseDto> getPurchasesOfCustomer(@PathVariable("username") String username) {
    return service.getPurchasesByUsername(username).stream().map(c -> this.convertToDto(c))
        .collect(Collectors.toList());
  }

  @DeleteMapping(value = {"/customer/{username}", "/customer/{username}/"})
  public void deleteCustomer(@PathVariable("username") String username)
      throws IllegalArgumentException {
    service.deleteCustomer(username);
  }

  @PatchMapping(value = {"/customer/{username}", "/customer/{username}/"})
  public CustomerDto updateCustomer(@PathVariable("username") String username,
      @RequestParam Optional<String> email, @RequestParam Optional<String> password,
      @RequestParam Optional<String> address, @RequestParam Optional<Boolean> isLocal)
      throws IllegalArgumentException {
    Customer customer = service.getCustomer(username);
    if (email.isPresent()) {
      customer = service.setCustomerEmail(username, email.get());
    }
    if (password.isPresent()) {
      customer = service.setCustomerPassword(username, password.get());
    }
    if (address.isPresent()) {
      customer = service.setCustomerAddress(username, address.get());
    }
    if (isLocal.isPresent()) {
      customer = service.setCustomerIsLocal(username, isLocal.get());
    }
    return convertToDto(customer);
  }

  private List<PurchaseDto> createPurchaseDtosForCustomer(Customer c) {
    List<Purchase> purchaseList = service.getPurchasesByUsername(c.getUsername());
    List<PurchaseDto> purchases = new ArrayList<>();
    for (Purchase purchase : purchaseList) {
      purchases.add(convertToDto(purchase));
    }
    return purchases;
  }

  private CustomerDto convertToDto(Customer c) throws IllegalArgumentException {
    if (c == null) {
      throw new IllegalArgumentException("There is no such Customer!");
    }
    CustomerDto customerDto = new CustomerDto(c.getUsername(), c.getPassword(), c.getEmail(),
        c.getAddress(), c.getIsLocal(), createPurchaseDtosForCustomer(c));
    return customerDto;
  }

  private PurchaseDto convertToDto(Purchase p) throws IllegalArgumentException {
    if (p == null) {
      throw new IllegalArgumentException("Invalid purcahse.");
    }
    ArrayList<SpecificItemDto> spItemsDto = new ArrayList<>();
    for (SpecificItem spItem : p.getSpecificItems()) {
      spItemsDto.add(this.convertToDto(spItem));
    }
    return new PurchaseDto(p.getId(), spItemsDto, p.getStateFullName(), p.getIsDelivery(),
        p.getTimeOfPurchaseMillis());
  }

  private SpecificItemDto convertToDto(SpecificItem s) throws IllegalArgumentException {
    if (s == null) {
      throw new IllegalArgumentException("Invalid specific item.");
    }
    return new SpecificItemDto(s.getId(), this.convertToDto(s.getItem()), s.getPurchaseQuantity(),
        s.getPurchasePrice());
  }

  private ItemDto convertToDto(Item i) throws IllegalArgumentException {
    if (i == null) {
      throw new IllegalArgumentException("Invalid item.");
    }
    return new ItemDto(i.getName(), i.getImage(), i.getPrice(), i.getInventory(), i.getCanDeliver(),
        i.getCanPickUp(), i.getIsDiscontinued());
  }
}
