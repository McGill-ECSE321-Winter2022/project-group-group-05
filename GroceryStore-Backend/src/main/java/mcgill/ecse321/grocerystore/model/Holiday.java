package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Holiday {

    private String name
    private int month
    private int day

  @Id
  public String getname() {
      return this.name;
  }

  public int getmonth() {
      return this.month;
  }

  public int getday() {
      return this.day;
  }

  public void setname(String name) {
      this.name = name;
  }

  public void setmonth(int month) {
      this.month = month;
  }

  public void setday(int day) {
      this.day = day;
  }

}
