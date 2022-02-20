package mcgill.ecse321.grocerystore.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;



@Entity
public class ItemCategory {

  @Id
  private String name;
//ItemCategory Associations
 @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
 private Set<Item> item;
 public ItemCategory() {
	    this.item = new HashSet<Item>();
	  }
  //setters
  public String getName() {
	  return this.name;
  }
  
  //getters
  public void setName(String name) {
	  this.name=name;
  }
  public boolean addSItem(Item item) {
	    return this.item.add(item);
	  }
  
  public boolean addItem(String name) {
	  Iterator<Item> i = this.item.iterator();
	    while (i.hasNext()) {
	      Item spItem = i.next();
	      if (spItem.getName()==name) {
	        this.item.add(spItem);
	        return true;
	      }
	    }
	    return false;
  }
  
  public boolean removeItem(Item item) {
    return this.item.remove(item);
  }
  public boolean removeItem(String name) {
	    Iterator<Item> i = this.item.iterator();
	    while (i.hasNext()) {
	      Item spItem = i.next();
	      if (spItem.getName()==name) {
	        i.remove();
	        return true;
	      }
	    }
	    return false;

	}
	

  

}
