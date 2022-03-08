package mcgill.ecse321.grocerystore.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import mcgill.ecse321.grocerystore.dto.ItemDto;
import mcgill.ecse321.grocerystore.dto.PurchaseDto;
import mcgill.ecse321.grocerystore.dto.SpecificItemDto;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.SpecificItem;
import mcgill.ecse321.grocerystore.service.PurchaseService;

@CrossOrigin(origins = "*")
@RestController
public class PurchaseController {

  @Autowired
  private PurchaseService service;

  @GetMapping(value = {"/purchase/allDesc", "/purchase/allDesc/"})
  public List<PurchaseDto> getAllPurchasesDesc() {
    return service.getAllDesc().stream().map(p -> this.toDto(p)).collect(Collectors.toList());
  }

  @GetMapping(value = {"/purchase/all", "/purchase/all/"})
  public List<PurchaseDto> getAllPurchases() {
    return service.getAll().stream().map(p -> this.toDto(p)).collect(Collectors.toList());
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

  private ItemDto toDto(Item i) throws IllegalArgumentException {
    if (i == null) {
      throw new IllegalArgumentException("Invalid item.");
    }
    return new ItemDto(i.getName(), i.getPrice(), i.getInventory(), i.getCanDeliver(),
        i.getCanPickUp(), i.getIsDiscontinued());
  }

}
