package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Holiday {
  @Id
  private String name;
  private int month;
  private int day;

  public String getName() {
    return this.name;
  }

  public int getMonth() {
    return this.month;
  }

  public int getDay() {
    return this.day;
  }

  public void setName(String value) {
    this.name = value;
  }

  public void setMonth(int value) {
    this.month = value;
  }

  public void setDay(int value) {
    this.day = value;
  }
}
