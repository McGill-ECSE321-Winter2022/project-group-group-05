package mcgill.ecse321.grocerystore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;

@Service
public class PurchaseService {

  @Autowired
  private PurchaseRepository purchaseRepo;

}
