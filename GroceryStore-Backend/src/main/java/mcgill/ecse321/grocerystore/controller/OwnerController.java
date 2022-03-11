package mcgill.ecse321.grocerystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import mcgill.ecse321.grocerystore.dto.OwnerDto;
import mcgill.ecse321.grocerystore.model.Owner;
import mcgill.ecse321.grocerystore.service.OwnerService;

@CrossOrigin(origins = "*")
@RestController
public class OwnerController {

  @Autowired
  private OwnerService service;

  /*
   * Create Owner
   */
  @PostMapping(value = {"/owner/{username}", "/owner/{username}/"})
  public OwnerDto createOwner(@PathVariable("username") String username,
      @RequestParam String password, @RequestParam String email) throws IllegalArgumentException {
    return convertToDto(service.createOwner(username, password, email));
  }

  /*
   * Get Owner
   */
  @GetMapping(value = {"/owner/{username}", "/owner/{username}/"})
  public OwnerDto getOwner(@PathVariable("username") String username)
      throws IllegalArgumentException {
    return convertToDto(service.getOwner(username));
  }

  /*
   * Delete Owner
   */
  @DeleteMapping(value = {"/owner/{username}", "/owner/{username}/"})
  public void deleteOwner(@PathVariable("username") String username)
      throws IllegalArgumentException {
    service.deleteOwner(username);
  }

  /*
   * Update Owner
   */
  @PatchMapping(value = {"/owner/{username}", "/owner/{username}/"})
  public OwnerDto updateOwner(@PathVariable("username") String username,
      @RequestParam(required = false) String email, @RequestParam(required = false) String password)
      throws IllegalArgumentException {
    OwnerDto owner = getOwner(username);
    if (email == null) {
      email = owner.getEmail();
    }
    if (password == null) {
      password = owner.getPassword();
    }
    return convertToDto(service.updateOwner(username, password, email));
  }

  private OwnerDto convertToDto(Owner owner) throws IllegalArgumentException {
    if (owner == null) {
      throw new IllegalArgumentException("There is no such Owner!");
    }
    OwnerDto ownerDto = new OwnerDto(owner.getUsername(), owner.getPassword(), owner.getEmail());
    return ownerDto;
  }

}
