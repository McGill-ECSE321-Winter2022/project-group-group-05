package mcgill.ecse321.grocerystore.dto;

public class OwnerDto {

  private String username;
  private String password;
  private String email;

  public OwnerDto(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @return the email
   */
  public String getEmail() {
    return email;
  }


}
