package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class SpecificItem {

  @Id
  @GeneratedValue
  private long id;

}
