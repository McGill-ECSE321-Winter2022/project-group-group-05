package mcgill.ecse321.grocerystore.model;

import java.sql.Date;
import java.sql.Time;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Purchase {

  public enum PurchaseState {
    Cart, Paid, Prepared, Completed
  };

  @Id
  @GeneratedValue
  private long id;

  @OneToMany(cascade = {CascadeType.ALL})
  private Set<SpecificItem> specificItems;

  @Enumerated(EnumType.STRING)
  private PurchaseState state;

  private boolean isDelivery;

  private long timeOfPurchaseMillis;

  public Purchase() {
    this.specificItems = new HashSet<SpecificItem>();
    this.state = PurchaseState.Cart;
    this.isDelivery = false;
    this.timeOfPurchaseMillis = System.currentTimeMillis();
  }

  public long getId() {
    return this.id;
  }

  public Set<SpecificItem> getSpecificItems() {
    return this.specificItems;
  }

  public String getState() {
    return this.state.toString();
  }

  public boolean getIsDelivery() {
    return this.isDelivery;
  }

  public Date getDateOfPurchase() {
    return new Date(this.timeOfPurchaseMillis);
  }

  public Time getTimeOfPurchase() {
    return new Time(this.timeOfPurchaseMillis);
  }

  public long getTimeOfPurchaseMillis() {
    return this.timeOfPurchaseMillis;
  }

  public void addSpecificItem(SpecificItem anItem) {
    this.specificItems.add(anItem);
  }

  public boolean removeSpecificItem(SpecificItem anItem) {
    if (this.specificItems.contains(anItem)) {
      this.specificItems.remove(anItem);
      return true;
    }
    return false;
  }

  public boolean setState(String state) {
    if (state.equals("Paid")) {
      this.state = PurchaseState.Paid;
      return true;
    } else if (state.equals("Prepared")) {
      this.state = PurchaseState.Prepared;
      return true;
    } else if (state.equals("Completed")) {
      this.state = PurchaseState.Completed;
      return true;
    }
    return false;
  }

  public void setIsDelivery(boolean b) {
    this.isDelivery = b;
  }

  public void setTimeOfPurchaseMillis() {
    this.timeOfPurchaseMillis = System.currentTimeMillis();
  }

}
