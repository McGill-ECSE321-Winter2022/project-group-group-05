package mcgill.ecse321.grocerystore.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import mcgill.ecse321.grocerystore.dao.CustomerRepository;
import mcgill.ecse321.grocerystore.dao.ItemRepository;
import mcgill.ecse321.grocerystore.dao.PurchaseRepository;
import mcgill.ecse321.grocerystore.model.Customer;
import mcgill.ecse321.grocerystore.model.Item;
import mcgill.ecse321.grocerystore.model.Purchase;
import mcgill.ecse321.grocerystore.model.Purchase.PurchaseState;
import mcgill.ecse321.grocerystore.model.SpecificItem;

/**
 * Service methods for Purchase
 *
 * @author Jimmy Sheng
 *
 */
@Service
public class PurchaseService {

  @Autowired
  private CustomerRepository customerRepo;
  @Autowired
  private ItemRepository itemRepo;
  @Autowired
  private PurchaseRepository purchaseRepo;

  /**
   * Add the item with the given name to the purchase with the given ID, with the specified
   * quantity. Throws if purchase is not in the cart state, quantity > inventory, purchase doesn't
   * exist, or item doesn't exist. (RQ1)
   *
   * @param purchaseId
   * @param itemName
   * @param quantity
   * @return the purchase object
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase addItemToCart(long purchaseId, String itemName, int quantity)
      throws IllegalArgumentException {
    Purchase purchase = this.getPurchase(purchaseId);
    this.verifyCartState(purchase);
    Item item = this.getItem(itemName);
    // input validation
    this.verifyInventory(item, quantity);
    // search if item is already in cart
    for (SpecificItem cartItem : purchase.getSpecificItems()) {
      if (cartItem.getItem().equals(item)) {
        int newQuantity = cartItem.getPurchaseQuantity() + quantity;
        // verify enough inventory
        this.verifyInventory(item, newQuantity);
        cartItem.setPurchaseQuantity(newQuantity);
        cartItem.updatePurchasePrice();
        return purchaseRepo.save(purchase);
      }
    }
    // item not found in cart, create new
    purchase.addSpecificItem(new SpecificItem(item, quantity));
    return purchaseRepo.save(purchase);
  }

  /**
   * Cancel the purchase with the given ID. The purchase can only be cancelled if it's in the Paid
   * state. (RQ7)
   *
   * @param purchaseId
   * @return the cancelled purchase object
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase cancel(long purchaseId) throws IllegalArgumentException {
    Purchase purchase = this.getPurchase(purchaseId);
    this.verifyPaidState(purchase);
    purchase.setState(PurchaseState.Cancelled);
    this.restoreInventory(purchase);
    return purchaseRepo.save(purchase);
  }

  /**
   * Complete the purchase by switching it to the completed state. The purchase must be in the
   * prepared state prior to being completed.
   *
   * @param purchaseId
   * @return the completed purchase
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase complete(long purchaseId) throws IllegalArgumentException {
    Purchase purchase = this.getPurchase(purchaseId);
    this.verifyPreparedState(purchase);
    purchase.setState(PurchaseState.Completed);
    return purchaseRepo.save(purchase);
  }

  /**
   * Delete this purchase. Can be used by both POS, online, or orphan (i.e. not associated with any
   * customer)
   *
   * @param purchaseId
   * @throws IllegalArgumentException
   */
  @Transactional
  public void delete(long purchaseId) throws IllegalArgumentException {
    Purchase purchase = this.getPurchase(purchaseId);
    for (Customer c : customerRepo.findAll()) {
      if (c.getPurchases().contains(purchase)) {
        this.delete(c.getUsername(), purchaseId);
        return;
      }
    }
    // orphan purchase
    purchaseRepo.delete(purchase);
  }

  /**
   * Delete this purchase, belonging to the specified customer. Can be used by both POS or online.
   *
   * @param username
   * @param purchaseId
   * @throws IllegalArgumentException
   */
  @Transactional
  public void delete(String username, long purchaseId) throws IllegalArgumentException {
    Purchase purchase = this.getPurchase(purchaseId);
    Customer customer = customerRepo.findByUsername(username);
    if (customer == null) {
      throw new IllegalArgumentException("Customer not found.");
    }
    if (!customer.getPurchases().contains(purchase)) {
      throw new IllegalArgumentException("Purchase does not belong to customer.");
    }
    customer.removePurchase(purchase);
    customer = customerRepo.save(customer);
    purchaseRepo.delete(purchase);
  }

  /**
   * Delete all purchases in the system.
   *
   * @throws IllegalArgumentException
   */
  @Transactional
  public void deleteAll() throws IllegalArgumentException {
    List<Purchase> allPurchases = this.getAll();
    for (Purchase p : allPurchases) {
      this.delete(p.getId());
    }
  }

  /**
   * Get a list of all purchases, ordered from oldest to newest by time of purchase. This list is
   * backed by an arraylist. (RQ9)
   *
   * @return a list
   */
  @Transactional
  public List<Purchase> getAll() {
    return purchaseRepo.findAllByOrderByTimeOfPurchaseMillisAsc();
  }

  /**
   * Get a list of all purchases in the completed state, ordered from oldest to newest by time of
   * purchase. This list is backed by an arraylist.
   *
   * @return a list
   */
  @Transactional
  public List<Purchase> getAllCompleted() {
    return this.getByState(PurchaseState.Completed);
  }

  /**
   * Get a list of all purchases, ordered from newest to oldest by time of purchase. This list is
   * backed by an arraylist. (RQ9)
   *
   * @return a list
   */
  @Transactional
  public List<Purchase> getAllDesc() {
    return purchaseRepo.findAllByOrderByTimeOfPurchaseMillisDesc();
  }

  /**
   * Get a list of all purchases in the paid state, ordered from oldest to newest by time of
   * purchase. This list is backed by an arraylist. (RQ8)
   *
   * @return a list
   */
  @Transactional
  public List<Purchase> getAllPaid() {
    return this.getByState(PurchaseState.Paid);
  }

  /**
   * Get a list of all purchases in the prepared state, ordered from oldest to newest by time of
   * purchase. This list is backed by an arraylist.
   *
   * @return a list
   */
  @Transactional
  public List<Purchase> getAllPrepared() {
    return this.getByState(PurchaseState.Prepared);
  }

  /**
   * Get a list of all purchases in the given state, ordered from oldest to newest by time of
   * purchase. This list is backed by an arraylist. (RQ8)
   *
   * @param state
   * @return a list
   */
  @Transactional
  public List<Purchase> getByState(PurchaseState state) {
    return purchaseRepo.findByStateOrderByTimeOfPurchaseMillisAsc(state);
  }

  /**
   * This method should <b>not</b> be accessed by the in-store POS.<br>
   * Get a cart for the given customer. A customer may only have one purchase in the cart state at a
   * time.
   *
   * @param username
   * @return the customer's existing cart, or a new cart if it doesn't exist
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase getCart(String username) throws IllegalArgumentException {
    Customer customer = this.getCustomer(username);
    for (Purchase p : customer.getPurchases()) {
      // return if found a cart
      if (p.getState() == PurchaseState.Cart) {
        // update the prices of items in cart
        for (SpecificItem spItem : p.getSpecificItems()) {
          spItem.updatePurchasePrice();
        }
        return purchaseRepo.save(p);
      }
    }
    // customer does not have a cart yet, create new
    Purchase cart = new Purchase();
    cart = purchaseRepo.save(cart);
    customer.addPurchase(cart);
    customerRepo.save(customer);
    return cart;
  }

  /**
   * Get the purchase object with the given ID.
   *
   * @param purchaseId
   * @return the purchase object
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase getPurchase(long purchaseId) throws IllegalArgumentException {
    Purchase purchase = purchaseRepo.findById(purchaseId);
    if (purchase == null) {
      throw new IllegalArgumentException("Purchase not found.");
    }
    return purchase;
  }

  /**
   * This method should <b>not</b> be accessed by the in-store POS.<br>
   * Change the given purchase to the paid state. (RQ6)
   *
   * @param purchaseId
   * @return the paid purchase
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase pay(long purchaseId) throws IllegalArgumentException {
    // input validation
    Customer pos = this.getPOS();
    Purchase purchase = this.getPurchase(purchaseId);
    if (pos.getPurchases().contains(purchase)) {
      throw new IllegalArgumentException("This is a POS purchase.");
    }
    this.verifyCartState(purchase);
    this.verifyCartNotEmpty(purchase);
    this.verifyCheckoutInventory(purchase);
    this.verifyDeliveryPickup(purchase);
    // proceed with checkout
    for (SpecificItem spItem : purchase.getSpecificItems()) {
      // priceOfPurchase is not updated here, it's updated at getCart
      // the store keep same price at the moment customers view their cart
      spItem.getItem().subInventory(spItem.getPurchaseQuantity());
      itemRepo.save(spItem.getItem());
    }
    purchase.setTimeOfPurchaseMillis();
    purchase.setState(PurchaseState.Paid);
    return purchaseRepo.save(purchase);
  }

  /**
   * <b>This method shall only be accessed by the in-store POS.</b><br>
   * Only purchases in the cart state can be deleted.
   *
   * @param purchaseId
   * @throws IllegalArgumentException
   */
  @Transactional
  public void posDeleteCart(long purchaseId) throws IllegalArgumentException {
    Customer pos = this.getPOS();
    Purchase purchase = this.getPurchase(purchaseId);
    this.verifyCartState(purchase);
    if (pos.removePurchase(purchase)) {
      customerRepo.save(pos);
      purchaseRepo.delete(purchase);
      return;
    }
    throw new IllegalArgumentException("Not a POS purchase.");
  }

  /**
   * <b>This method shall only be accessed by the in-store POS.</b><br>
   *
   * @return a new purchase object
   */
  @Transactional
  public Purchase posGetNewCart() throws IllegalArgumentException {
    Customer pos = this.getPOS();
    Purchase cart = new Purchase();
    cart = purchaseRepo.save(cart);
    pos.addPurchase(cart);
    pos = customerRepo.save(pos);
    return cart;
  }

  /**
   * <b>This method shall only be accessed by the in-store POS.</b><br>
   * Change the given purchase to the completed state. (RQ6)
   *
   * @param purchaseId
   * @return the completed purchase
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase posPay(long purchaseId) throws IllegalArgumentException {
    // input validation
    Customer pos = this.getPOS();
    Purchase purchase = this.getPurchase(purchaseId);
    if (!pos.getPurchases().contains(purchase)) {
      throw new IllegalArgumentException("Not a POS purchase.");
    }
    this.verifyCartState(purchase);
    this.verifyCartNotEmpty(purchase);
    this.verifyCheckoutInventory(purchase);
    // proceed with checkout
    for (SpecificItem spItem : purchase.getSpecificItems()) {
      // priceOfPurchase is not updated here since it's an in-store POS
      spItem.getItem().subInventory(spItem.getPurchaseQuantity());
      itemRepo.save(spItem.getItem());
    }
    purchase.setTimeOfPurchaseMillis();
    purchase.setState(PurchaseState.Completed);
    return purchaseRepo.save(purchase);
  }

  /**
   * Delete all purchases in the cart state associated with the POS customer.
   *
   * @throws IllegalArgumentException
   */
  @Transactional
  public void posPurgeCarts() throws IllegalArgumentException {
    Customer pos = this.getPOS();
    ArrayList<Purchase> toBeDeleted = new ArrayList<>();
    Iterator<Purchase> iter = pos.getPurchases().iterator();
    while (iter.hasNext()) {
      Purchase p = iter.next();
      if (p.getState() == PurchaseState.Cart) {
        toBeDeleted.add(p);
        iter.remove();
      }
    }
    customerRepo.save(pos);
    purchaseRepo.deleteAll(toBeDeleted);
  }

  /**
   * Prepare the purchase with the given ID. The purchase can only be prepared if it's in the Paid
   * state. (RQ8)
   *
   * @param purchaseId
   * @return the prepared purchase object
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase prepare(long purchaseId) throws IllegalArgumentException {
    Purchase purchase = this.getPurchase(purchaseId);
    this.verifyPaidState(purchase);
    purchase.setState(PurchaseState.Prepared);
    return purchaseRepo.save(purchase);
  }

  /**
   * This method should <b>not</b> be accessed by the in-store POS.<br>
   * Set if the purchase is an delivery or an pickup. Throw exception if incompatible items are
   * present in the cart.
   *
   * @param purchaseId
   * @param isDelivery
   * @return the updated purchase object
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase setIsDelivery(long purchaseId, boolean isDelivery)
      throws IllegalArgumentException {
    // input validation
    Customer pos = this.getPOS();
    Purchase purchase = this.getPurchase(purchaseId);
    if (pos.getPurchases().contains(purchase)) {
      throw new IllegalArgumentException("This is a POS purchase.");
    }
    this.verifyCartState(purchase);
    if (isDelivery) {
      for (SpecificItem spItem : purchase.getSpecificItems()) {
        if (!spItem.getItem().getCanDeliver()) {
          throw new IllegalArgumentException(
              "Item '" + spItem.getItem().getName() + "' cannot be delivered.");
        }
      }
    } else {
      for (SpecificItem spItem : purchase.getSpecificItems()) {
        if (!spItem.getItem().getCanPickUp()) {
          throw new IllegalArgumentException(
              "Item '" + spItem.getItem().getName() + "' cannot be picked-up.");
        }
      }
    }
    purchase.setIsDelivery(isDelivery);
    return purchaseRepo.save(purchase);
  }

  /**
   * Set the quantity of the item in this purchase that must be in the cart state. If quantity <= 0,
   * the item will be removed from the purchase. If the item is not already in the purchase, it will
   * be added. (RQ1)
   *
   * @param purchaseId
   * @param itemName
   * @param quantity
   * @return the purchase object
   * @throws IllegalArgumentException
   */
  @Transactional
  public Purchase setItemQuantity(long purchaseId, String itemName, int quantity)
      throws IllegalArgumentException {
    Purchase purchase = this.getPurchase(purchaseId);
    this.verifyCartState(purchase);
    Item item = this.getItem(itemName);
    // search if item is already in cart, do removal if necessary
    Iterator<SpecificItem> iter = purchase.getSpecificItems().iterator();
    while (iter.hasNext()) {
      SpecificItem spItem = iter.next();
      if (spItem.getItem().equals(item)) {
        if (quantity <= 0) {
          iter.remove();
          return purchaseRepo.save(purchase);
        }
        // if not removal, verify inventory and update quantity
        this.verifyInventory(item, quantity);
        spItem.setPurchaseQuantity(quantity);
        spItem.updatePurchasePrice();
        return purchaseRepo.save(purchase);
      }
    }
    // item not found in cart, verify inventory and create
    this.verifyInventory(item, quantity);
    purchase.addSpecificItem(new SpecificItem(item, quantity));
    return purchaseRepo.save(purchase);
  }

  @Transactional
  private Customer getCustomer(String username) throws IllegalArgumentException {
    if (username == null || username.trim().equals("")) {
      throw new IllegalArgumentException("Username cannot be empty.");
    }
    if (username.equals("kiosk")) {
      throw new IllegalArgumentException("This is a POS account.");
    }
    Customer customer = customerRepo.findByUsername(username);
    if (customer == null) {
      throw new IllegalArgumentException("Customer not found.");
    }
    return customer;
  }

  @Transactional
  private Item getItem(String itemName) throws IllegalArgumentException {
    if (itemName == null || itemName.trim().equals("")) {
      throw new IllegalArgumentException("Item name cannot be empty.");
    }
    Item item = itemRepo.findByName(itemName);
    if (item == null) {
      throw new IllegalArgumentException("Item not found.");
    }
    return item;
  }

  @Transactional
  private Customer getPOS() throws IllegalArgumentException {
    Customer pos = customerRepo.findByUsername("kiosk");
    if (pos == null) {
      throw new IllegalArgumentException("POS account 'kiosk' not found.");
    }
    return pos;
  }

  @Transactional
  private void restoreInventory(Purchase purchase) {
    for (SpecificItem spItem : purchase.getSpecificItems()) {
      spItem.getItem().addInventory(spItem.getPurchaseQuantity());
      itemRepo.save(spItem.getItem());
    }
  }

  private void verifyCartNotEmpty(Purchase purchase) throws IllegalArgumentException {
    if (purchase.getSpecificItems().isEmpty()) {
      throw new IllegalArgumentException("Purchase is empty.");
    }
  }

  private void verifyCartState(Purchase purchase) throws IllegalArgumentException {
    if (purchase.getState() != PurchaseState.Cart) {
      throw new IllegalArgumentException("Purchase is not in the 'cart' state.");
    }
  }

  private void verifyCheckoutInventory(Purchase purchase) throws IllegalArgumentException {
    String errorMsg = "";
    for (SpecificItem spItem : purchase.getSpecificItems()) {
      try {
        this.verifyInventory(spItem.getItem(), spItem.getPurchaseQuantity());
      } catch (IllegalArgumentException e) {
        errorMsg += e.getMessage();
      }
    }
    if (!errorMsg.equals("")) {
      throw new IllegalArgumentException(errorMsg);
    }
  }

  private void verifyDeliveryPickup(Purchase purchase) throws IllegalArgumentException {
    if (purchase.getIsDelivery()) {
      for (SpecificItem spItem : purchase.getSpecificItems()) {
        if (!spItem.getItem().getCanDeliver()) {
          throw new IllegalArgumentException(
              "Item '" + spItem.getItem().getName() + "' cannot be delivered.");
        }
      }
    } else {
      for (SpecificItem spItem : purchase.getSpecificItems()) {
        if (!spItem.getItem().getCanPickUp()) {
          throw new IllegalArgumentException(
              "Item '" + spItem.getItem().getName() + "' cannot be picked-up.");
        }
      }
    }
  }

  private void verifyInventory(Item item, int quantity) throws IllegalArgumentException {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity must be larger than 0.");
    }
    if (item.getInventory() < quantity) {
      throw new IllegalArgumentException(
          "Quantity of '" + item.getName() + "' exceeds current inventory. ");
    }
  }

  private void verifyPaidState(Purchase purchase) throws IllegalArgumentException {
    if (purchase.getState() != PurchaseState.Paid) {
      throw new IllegalArgumentException("Purchase is not in the 'paid' state.");
    }
  }

  private void verifyPreparedState(Purchase purchase) throws IllegalArgumentException {
    if (purchase.getState() != PurchaseState.Prepared) {
      throw new IllegalArgumentException("Purchase is not in the 'prepared' state.");
    }
  }


}
