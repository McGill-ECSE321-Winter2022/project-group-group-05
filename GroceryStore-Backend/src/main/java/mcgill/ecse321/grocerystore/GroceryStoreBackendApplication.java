package mcgill.ecse321.grocerystore;

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
    return "Welcome to the grocery store.";
  }

}
