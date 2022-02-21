package mcgill.ecse321.grocerystore.dao;

import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.Purchase.PurchaseState;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {

  Purchase findById(long id);

  ArrayList<Purchase> findByIsDelivery(boolean isDelivery);

  ArrayList<Purchase> findByState(PurchaseState state);

  ArrayList<Purchase> findByStateOrderByTimeOfPurchaseMillis(PurchaseState state);

  ArrayList<Purchase> findByStateOrderByTimeOfPurchaseMillisDesc(PurchaseState state);

  ArrayList<Purchase> findByStateOrderByTimeOfPurchaseMillisAsc(PurchaseState state);

  ArrayList<Purchase> findByTimeOfPurchaseMillisGreaterThanEqual(long time);

}
