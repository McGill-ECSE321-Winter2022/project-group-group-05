package mcgill.ecse321.grocerystore.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Customer {

  private String username;
  private String password;
  private String email;
  private String address;
  private boolean isLocal;
  private Set<Purchase> purchases;

  public void setUsername(String value) {
    this.username = value;
  }

  @Id
  public String getUsername() {
    return this.username;
  }

  public void setPassword(String value) {
    this.password = value;
  }

  public String getPassword() {
    return this.password;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public String getEmail() {
    return this.email;
  }

  public void setAddress(String value) {
    this.address = value;
  }

  public String getAddress() {
    return this.address;
  }

  public void setIsLocal(boolean value) {
    this.isLocal = value;
  }

  public boolean getIsLocal() {
    return this.isLocal;
  }

  public void setPurchases(Set<Purchase> set) {
    this.purchases = set;
  }

  public void addPurchases(Purchase value) {
    this.purchases.add(value);
  }

  public void removePurchases(Purchase value) {
    this.purchases.remove(value);
  }

  @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
  public Set<Purchase> getPurchases() {
    return this.purchases;
  }

}
