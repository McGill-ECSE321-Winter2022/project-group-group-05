package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Holiday {

    private String name
    private int month
    private int day

  @Id
  public String getName() {
      return this.name;
  }

  public int getMonth() {
      return this.month;
  }

  public int getDay() {
      return this.day;
  }

  public void setName(String name) {
      this.name = name;
  }

  public void setMonth(int month) {
      this.month = month;
  }

  public void setDay(int day) {
      this.day = day;
  }

}
