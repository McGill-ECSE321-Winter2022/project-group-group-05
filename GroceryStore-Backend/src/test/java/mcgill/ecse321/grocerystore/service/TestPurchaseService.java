package mcgill.ecse321.grocerystore.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;
import mcgill.ecse321.grocerystore.dao.SpecificItemRepository;

@ExtendWith(MockitoExtension.class)
public class TestPurchaseService {

  @Mock
  private PurchaseRepository purchaseRepo;
  @Mock
  private SpecificItemRepository specificItemRepo;
  @Mock
  private ItemRepository itemRepo;

  @InjectMocks
  private PurchaseService service;

}
