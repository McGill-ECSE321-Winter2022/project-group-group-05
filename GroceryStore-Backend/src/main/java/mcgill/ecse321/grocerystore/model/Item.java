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


  // This class has no association



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
