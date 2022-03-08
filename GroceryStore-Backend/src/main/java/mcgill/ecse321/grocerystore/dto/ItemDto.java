package mcgill.ecse321.grocerystore.dto;

public class ItemDto {

  private String name;
  private double price;
  private int inventory;
  private boolean canDeliver;
  private boolean canPickUp;
  private boolean discontinued;

  public ItemDto(String name, double price, int inventory, boolean canDeliver, boolean canPickUp,
      boolean discontinued) {
    this.name = name;
    this.price = price;
    this.inventory = inventory;
    this.canDeliver = canDeliver;
    this.canPickUp = canPickUp;
    this.discontinued = discontinued;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the price
   */
  public double getPrice() {
    return price;
  }

  /**
   * @return the inventory
   */
  public int getInventory() {
    return inventory;
  }

  /**
   * @return the canDeliver
   */
  public boolean isCanDeliver() {
    return canDeliver;
  }

  /**
   * @return the canPickUp
   */
  public boolean isCanPickUp() {
    return canPickUp;
  }

  /**
   * @return the discontinued
   */
  public boolean isDiscontinued() {
    return discontinued;
  }

}
