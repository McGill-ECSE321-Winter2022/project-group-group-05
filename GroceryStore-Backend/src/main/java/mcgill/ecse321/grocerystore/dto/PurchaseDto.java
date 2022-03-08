package mcgill.ecse321.grocerystore.dto;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

/**
 * 
 * @author Jimmy Sheng
 *
 */
public class PurchaseDto {

  private long id;
  private List<SpecificItemDto> specificItems;
  private String state;
  private boolean isDelivery;
  private Date dateOfPurchase;
  private Time timeOfPurchase;
  private long timeOfPurchaseMillis;

  public PurchaseDto(long id, List<SpecificItemDto> specificItems, String state, boolean isDelivery,
      long timeOfPurchaseMillis) {
    this.id = id;
    this.specificItems = specificItems;
    this.state = state;
    this.isDelivery = isDelivery;
    this.dateOfPurchase = new Date(timeOfPurchaseMillis);
    this.timeOfPurchase = new Time(timeOfPurchaseMillis);
    this.timeOfPurchaseMillis = timeOfPurchaseMillis;
  }

  /**
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * @return the specificItems
   */
  public List<SpecificItemDto> getSpecificItems() {
    return specificItems;
  }

  /**
   * @return the state
   */
  public String getState() {
    return state;
  }

  /**
   * @return the isDelivery
   */
  public boolean isDelivery() {
    return isDelivery;
  }

  /**
   * @return the dateOfPurchase
   */
  public Date getDateOfPurchase() {
    return dateOfPurchase;
  }

  /**
   * @return the timeOfPurchase
   */
  public Time getTimeOfPurchase() {
    return timeOfPurchase;
  }

  /**
   * @return the timeOfPurchaseMillis
   */
  public long getTimeOfPurchaseMillis() {
    return timeOfPurchaseMillis;
  }

  /**
   * @param specificItems the specificItems to set
   */
  public void setSpecificItems(List<SpecificItemDto> specificItems) {
    this.specificItems = specificItems;
  }

}
