package mcgill.ecse321.grocerystore.dto;

import java.util.List;

public class ItemCategoryDto {

  private String name;
  private List<ItemDto> items;

  public ItemCategoryDto(String name, List<ItemDto> items) {
    this.name = name;
    this.items = items;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the items
   */
  public List<ItemDto> getItems() {
    return items;
  }

  /**
   * @param items the items to set
   */
  public void setItems(List<ItemDto> items) {
    this.items = items;
  }


}
