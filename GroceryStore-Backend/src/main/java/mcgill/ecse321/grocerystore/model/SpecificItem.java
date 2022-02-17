package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class SpecificItem {

  @Id
  @GeneratedValue
  private long id;

  // TODO: set (optional = false) once Item class is implemented
  @ManyToOne(optional = true, fetch = FetchType.EAGER)
  private Item item;
  
  private int purchaseQuantity;
  private double purchasePrice;
  
  /**
   * <b>IMPORTANT: Item must be set before saving to database</b>
   * <br>Providing default constructor for Spring
   */
  public SpecificItem() {
  }
  
  /**
   * <b>IMPORTANT: id is set automatically only after saving to database</b>
   * <br>Do not use this before saving to database
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
  
  public void setItem(Item anItem) {
    this.item = anItem;
  }
  
  public void setPurchaseQuantity(int qty) {
    this.purchaseQuantity = qty;
  }
  
  public void setPurchasePrice(int price) {
    this.purchasePrice = price;
  }
  
}
