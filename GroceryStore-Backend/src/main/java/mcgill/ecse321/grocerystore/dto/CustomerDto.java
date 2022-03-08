package mcgill.ecse321.grocerystore.dto;

import java.util.List;

public class CustomerDto {

  private String username;
  private String password;
  private String email;
  private String address;
  private boolean local;
  private List<PurchaseDto> purchases;

  public CustomerDto(String username, String password, String email, String address, boolean local,
      List<PurchaseDto> purchases) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.address = address;
    this.local = local;
    this.purchases = purchases;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * @return the local
   */
  public boolean isLocal() {
    return local;
  }

  /**
   * @return the purchases
   */
  public List<PurchaseDto> getPurchases() {
    return purchases;
  }

  /**
   * @param purchases the purchases to set
   */
  public void setPurchases(List<PurchaseDto> purchases) {
    this.purchases = purchases;
  }



}
