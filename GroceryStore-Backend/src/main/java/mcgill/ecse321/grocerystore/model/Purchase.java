package mcgill.ecse321.grocerystore.model;

import java.sql.Date;
import java.sql.Time;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Jimmy Sheng
 *
 */
@Entity
public class Purchase {

  public enum PurchaseState {
    Cart, Paid, Prepared, Completed, Cancelled
  }

  @Id
  @GeneratedValue
  private long id;

  // For simplicity use FetchType.EAGER to load associations
  @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
  private Set<SpecificItem> specificItems;

  @Enumerated(EnumType.STRING)
  private PurchaseState state;

  private boolean isDelivery;

  private long timeOfPurchaseMillis;

  public Purchase() {
    this.specificItems = new HashSet<>();
    this.state = PurchaseState.Cart;
    this.isDelivery = false;
    this.timeOfPurchaseMillis = System.currentTimeMillis();
  }

  /**
   * The id should NOT be used before added to the database since it will have the value 0
   *
   * @return id which has been auto generated by Spring when added to database
   */
  public long getId() {
    return this.id;
  }

  public Set<SpecificItem> getSpecificItems() {
    return this.specificItems;
  }

  public PurchaseState getState() {
    return this.state;
  }

  public String getStateFullName() {
    return this.state.toString();
  }

  /**
   * If a Purchase belongs to a normal customer (i.e. not a point-of-sale) and is not for delivery,
   * then it is for pickup
   *
   * @return if this Purchase is for delivery
   */
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

  /**
   * This should only be performed when this is in the <b>Cart</b> state
   *
   * @param anItem
   * @return true if the specificItem is added to the set
   */
  public boolean addSpecificItem(SpecificItem anItem) {
    return this.specificItems.add(anItem);
  }

  /**
   * Remove the specificItem from the purchase. Runs in O(1) time.<br>
   * This should only be performed when this is in the <b>Cart</b> state
   *
   * @param anItem
   * @return true if the purchase contains the specificItem
   */
  public boolean removeSpecificItem(SpecificItem anItem) {
    return this.specificItems.remove(anItem);
  }

  /**
   * Alternative way to remove specificItem by id. Runs in O(n) time.<br>
   * This should only be performed when this is in the <b>Cart</b> state
   *
   * @param id
   * @return true if the purchase contains the specificItem with given id
   */
  public boolean removeSpecificItem(long id) {
    Iterator<SpecificItem> i = this.specificItems.iterator();
    while (i.hasNext()) {
      SpecificItem spItem = i.next();
      if (spItem.getId() == id) {
        i.remove();
        return true;
      }
    }
    return false;
  }

  /**
   * For mockito testing only. The id should never be set manually otherwise.
   *
   * @param id
   */
  public void setId(long id) {
    this.id = id;
  }

  public void setState(PurchaseState state) {
    this.state = state;
  }

  /**
   * Should only be performed when this is in the <b>Cart</b> state
   *
   * @param b
   */
  public void setIsDelivery(boolean b) {
    this.isDelivery = b;
  }

  /**
   * Should only be performed once when this transitions from <b>Cart</b> to <b>Paid</b> state
   *
   * @return system time in milliseconds
   */
  public long setTimeOfPurchaseMillis() {
    this.timeOfPurchaseMillis = System.currentTimeMillis();
    return this.timeOfPurchaseMillis;
  }


  /**
   * For mockito testing only. The time should never be set manually otherwise.
   *
   * @param timeOfPurchaseMillis
   */
  public void setTimeOfPurchaseMillis(long timeOfPurchaseMillis) {
    this.timeOfPurchaseMillis = timeOfPurchaseMillis;
  }

  /**
   * At creation, Purchase will have default Object.hashCode(). <br>
   * Once saved to the database, their hashCode() will be their id.
   */
  @Override
  public int hashCode() {
    if (this.getId() == 0)
      return super.hashCode();
    return (int) this.getId();
  }

  /**
   * Two Purchases are not equal if they both have id of 0 because they haven't been saved to the
   * database yet, unless they're equal by == as defined in Object. <br>
   * Otherwise, they're equal if they have the same id.
   */
  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (o == this)
      return true;
    if (o.getClass() == this.getClass()) {
      Purchase obj = (Purchase) o;
      if (obj.getId() == 0)
        return false;
      return obj.getId() == this.getId();
    }
    return false;
  }

}
