package mcgill.ecse321.grocerystore.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Customer {
  @Id
  private String username;
  private String password;
  private String email;
  private String address;
  private boolean isLocal;
  @OneToMany(fetch = FetchType.EAGER)
  private Set<Purchase> purchases;

  public Customer() {
    this.purchases = new HashSet<>();
  }

  public void setUsername(String value) {
    this.username = value;
  }

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

  public boolean addPurchase(Purchase value) {
    return this.purchases.add(value);
  }

  public boolean removePurchase(Purchase value) {
    return this.purchases.remove(value);
  }

  public Set<Purchase> getPurchases() {
    return this.purchases;
  }

}
