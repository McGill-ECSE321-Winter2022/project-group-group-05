package mcgill.ecse321.grocerystore.model;

import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Holiday {
  @Id
  private String name;
  private Date date;

  public String getName() {
    return this.name;
  }

  public Date getDate() {
    return this.date;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDate(Date date) {
    this.date = date;
  }

}
