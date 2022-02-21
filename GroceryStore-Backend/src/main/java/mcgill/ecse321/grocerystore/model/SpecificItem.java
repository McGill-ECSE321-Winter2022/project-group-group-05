package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * 
 * @author Jimmy Sheng
 *
 */
@Entity
public class SpecificItem {

  @Id
  @GeneratedValue
  private long id;

  @ManyToOne(optional = false)
  private Item item;

  private int purchaseQuantity;
  private double purchasePrice;

  /**
   * <b>IMPORTANT: Item must be set before saving to database</b> <br>
   * Providing default constructor for Spring
   */
  public SpecificItem() {}

  /**
   * Create a specificItem with the given item and 0 quantity
   * 
   * @param item
   */
  public SpecificItem(Item item) {
    this(item, 0);
  }

  /**
   * Create a specificItem with the given item and quantity
   * 
   * @param item
   * @param qty
   */
  public SpecificItem(Item item, int qty) {
    this.setItem(item);
    this.setPurchaseQuantity(qty);
    this.setPurchasePrice(item.getPrice());
  }

  /**
   * <b>IMPORTANT: id is set automatically only after saving to database</b> <br>
   * Do not use this before saving to database
   * 
   * @return auto generated id
   */
  public long getId() {
    return this.id;
  }

  public Item getItem() {
    return this.item;
  }

  public int getPurchaseQuantity() {
    return this.purchaseQuantity;
  }

  public double getPurchasePrice() {
    return this.purchasePrice;
  }

  /**
   * This method should only be used once at the creation of the specificItem<br>
   * Once set, the linked item should not be modified
   * 
   * @param anItem
   */
  public void setItem(Item anItem) {
    this.item = anItem;
  }

  public void setPurchaseQuantity(int qty) {
    this.purchaseQuantity = qty;
  }

  /**
   * This method generally <b>should not be used</b><br>
   * Use updatePurchasePrice to automatically get the price of the linked item
   * 
   * @param price
   */
  public void setPurchasePrice(double price) {
    this.purchasePrice = price;
  }

  /**
   * Update the recorded purchasePrice to the current price of the item
   * 
   * @return The updated price if item is not null<br>
   *         Otherwise returns -1
   */
  public double updatePurchasePrice() {
    if (this.item != null) {
      this.purchasePrice = this.item.getPrice();
      return this.purchasePrice;
    }
    return -1;
  }

  /**
   * At creation, SpecificItems will have default Object.hashCode(). <br>
   * Once saved to the database, their hashCode() will be their id.
   */
  @Override
  public int hashCode() {
    if (this.getId() == 0)
      return super.hashCode();
    return (int) this.getId();
  }

  /**
   * Two SpecificItems are not equal if they both have id of 0 because they haven't been saved to
   * the database yet, unless they're equal by == as defined in Object. <br>
   * Otherwise, they're equal if they have the same id.
   */
  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (o == this)
      return true;
    if (o.getClass() == this.getClass()) {
      SpecificItem obj = (SpecificItem) o;
      if (obj.getId() == 0)
        return false;
      return obj.getId() == this.getId();
    }
    return false;
  }

}
