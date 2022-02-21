package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Item {

  @Id
  private String name;
  // Item Attributes
  private double price;
  private int inventory;
  private boolean canDeliver;
  private boolean canPickUp;
  private boolean isDiscontinued;

  // default constructor
  /**
   * <b>IMPORTANT: Name must be set before saving to database</b> <br>
   * Providing default constructor for Spring
   */
  public Item() {}

  // getter methods
  // --------------
  public String getName() {
    return this.name;
  }

  public double getPrice() {
    return this.price;
  }

  public int getInventory() {
    return this.inventory;
  }

  public boolean getCanDeliver() {
    return this.canDeliver;
  }

  public boolean getCanPickUp() {
    return this.canPickUp;
  }

  public boolean getIsDiscontinued() {
    return this.isDiscontinued;
  }

  // setter methods
  // --------------
  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public void setInventory(int inventory) {
    this.inventory = inventory;
  }

  public void setCanDeliver(boolean canDeliver) {
    this.canDeliver = canDeliver;
  }

  public void setCanPickUp(boolean canPickUp) {
    this.canPickUp = canPickUp;
  }

  public void setIsDiscontinued(boolean isDiscontinued) {
    this.isDiscontinued = isDiscontinued;
  }

  // utility methods
  /**
   * Increment the inventory of this item by the specified value
   * 
   * @param addValue
   * @return the new total inventory of this item
   */
  public int addInventory(int addValue) {
    this.inventory += addValue;
    return this.inventory;
  }

  /**
   * Decrement the inventory of this item by the specified value if there is enough inventory
   * 
   * @param subtractValue
   * @return The new total inventory of this item<br>
   *         <b>Returns -1 if there's not enough inventory</b>
   */
  public int subInventory(int subtractValue) {
    if (this.inventory >= subtractValue) {
      this.inventory -= subtractValue;
      return this.inventory;
    }
    return -1;
  }

  /**
   * At creation, Item will have default Object.hashCode(). <br>
   * Once saved to the database, their hashCode() will be name.hashCode().
   */
  @Override
  public int hashCode() {
    if (this.getName() == null)
      return super.hashCode();
    return this.getName().hashCode();
  }

  /**
   * Two Items are not equal if they both have null name because they haven't been saved to the
   * database yet, unless they're equal by == as defined in Object. <br>
   * Otherwise, they're equal if they have the same name.
   */
  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (o == this)
      return true;
    if (o.getClass() == this.getClass()) {
      Item obj = (Item) o;
      if (obj.getName() == null)
        return false;
      return obj.getName().equals(this.getName());
    }
    return false;
  }

}
