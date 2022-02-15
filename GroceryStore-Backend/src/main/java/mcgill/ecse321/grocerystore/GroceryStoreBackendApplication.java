package mcgill.ecse321.grocerystore;

import java.sql.Date;
import java.sql.Time;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class GroceryStoreBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(GroceryStoreBackendApplication.class, args);
  }

  @RequestMapping("/")
  public String greeting() {
    long millis = System.currentTimeMillis();
    Date date = new java.sql.Date(millis);
    Time time = new java.sql.Time(millis);
    return "<h1>Welcome to the grocery store!</h1> <h2>The current system time is: "
        + date.toString() + " " + time.toString() + "</h2>";
  }

}
