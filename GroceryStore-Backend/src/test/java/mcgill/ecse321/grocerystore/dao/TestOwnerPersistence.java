package mcgill.ecse321.grocerystore.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import mcgill.ecse321.grocerystore.model.Owner;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class TestOwnerPersistence {
  @Autowired
  private OwnerRepository ownerRepository;

  @AfterEach
  public void clearDatabase() {
    ownerRepository.deleteAll();
  }

  @Test
  public void testPersistAndLoadPerson() {
    String username = "TestOwner";
    String password = "password";
    String email = "owner@email.ca";
    Owner owner = new Owner();
    owner.setUsername(username);
    owner.setEmail(email);
    owner.setPassword(password);
    ownerRepository.save(owner);

    owner = null;

    owner = ownerRepository.findOwnerByUsername(username);
    assertNotNull(owner);
    assertEquals(username, owner.getUsername());
    assertEquals(password, owner.getPassword());
    assertEquals(email, owner.getEmail());
  }
}
