package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ItemCategory {

  @Id
  private String name;

}
