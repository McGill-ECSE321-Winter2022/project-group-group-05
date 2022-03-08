package mcgill.ecse321.grocerystore.dto;

public class SpecificItemDto {

  private long id;
  private ItemDto item;
  private int purchaseQuantity;
  private double purchasePrice;

  public SpecificItemDto(long id, ItemDto item, int purchaseQuantity, double purchasePrice) {
    this.id = id;
    this.item = item;
    this.purchaseQuantity = purchaseQuantity;
    this.purchasePrice = purchasePrice;
  }

  /**
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * @return the item
   */
  public ItemDto getItem() {
    return item;
  }

  /**
   * @return the purchaseQuantity
   */
  public int getPurchaseQuantity() {
    return purchaseQuantity;
  }

  /**
   * @return the purchasePrice
   */
  public double getPurchasePrice() {
    return purchasePrice;
  }

}
