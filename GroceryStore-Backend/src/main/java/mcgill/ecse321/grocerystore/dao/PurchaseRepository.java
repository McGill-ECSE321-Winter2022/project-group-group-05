package mcgill.ecse321.grocerystore.dao;

import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Purchase;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {


}
