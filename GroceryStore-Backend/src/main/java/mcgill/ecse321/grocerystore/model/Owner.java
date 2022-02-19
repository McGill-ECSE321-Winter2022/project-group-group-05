package mcgill.ecse321.grocerystore.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Owner {
  @Id
  private String username;
  private String password;
  private String email;

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String value) {
    this.username = value;
  }

  public void setPassword(String value) {
    this.password = value;
  }

  public String getPassword() {
    return this.password;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public String getEmail() {
    return this.email;
  }
}
