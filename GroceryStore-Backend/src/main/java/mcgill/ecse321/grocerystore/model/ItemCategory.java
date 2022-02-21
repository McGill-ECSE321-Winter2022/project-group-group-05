package mcgill.ecse321.grocerystore.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;



@Entity
public class ItemCategory {

  @Id
  private String name;
  // ItemCategory Associations
  @OneToMany(fetch = FetchType.EAGER)
  private Set<Item> items;

  public ItemCategory() {
    this.items = new HashSet<Item>();
  }

  // Getters
  public String getName() {
    return this.name;
  }

  public Set<Item> getItems() {
    return this.items;
  }

  // Setters
  public void setName(String name) {
    this.name = name;
  }

  public boolean addItem(Item item) {
    return this.items.add(item);
  }

  public boolean removeItem(Item item) {
    return this.items.remove(item);
  }

  /**
   * Remove the item with a given name in the item set
   * 
   * @param name
   * @return true if the item is removed
   */
  public boolean removeItem(String name) {
    Iterator<Item> i = this.items.iterator();
    while (i.hasNext()) {
      Item spItem = i.next();
      if (spItem.getName().equals(name)) {
        i.remove();
        return true;
      }
    }
    return false;
  }

}


