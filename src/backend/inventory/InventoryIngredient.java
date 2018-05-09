package backend.inventory;

/**
 * InventoryIngredient represents the Ingredient in backend.inventory
 *
 * <p>InventoryIngredient methods include checking whether the ingredient in the restaurant
 * backend.inventory is below the lower threshold
 */
public class InventoryIngredient extends Ingredient {
  private int lowerThreshold;
  // The runningQuantity is similar to the actual quantity of the InventoryIngredient, but the
  // former is updated whenever a Dish
  // is added to the order to avoid a situation where more orders are made than the ingredient
  // available in inventory -
  // while still allowing the actual quantity to be deducted only when the Dish has been prepared
  // (or if the cook needs to
  // discard the InventoryIngredient due to spoilage or wasting it by accident).
  //
  // @Contract("runningQuality <= quantity")
  //
  private int runningQuantity;
  private boolean isUnderThreshold;

  /**
   * Constructor for InventoryIngredient that takes in the name, quantity and lower threshold for
   * the ingredient
   *
   * @param name the name of this InventoryIngredient
   * @param quantity the quantity of this InventoryIngredient in stock
   * @param lowerThreshold the lowerThreshold of this InventoryIngredient; if the quantity of this
   *     Ingredient goes below this lowerThreshold, then a restock order will be made
   */
  public InventoryIngredient(String name, int quantity, int lowerThreshold) {
    super(name, quantity);
    this.lowerThreshold = lowerThreshold;
    this.runningQuantity = quantity;
    this.isUnderThreshold = quantity < lowerThreshold;
  }

  /**
   * Returns true iff the quantity of this InventoryIngredient is below the lower threshold
   *
   * @return boolean statement
   */
  public boolean isLowStock() {
    return this.lowerThreshold > this.getQuantity();
  }

  /**
   * Returns the runningQuantity of this InventoryIngredient
   * @return the runningQuantity of this InventoryIngredient
   */
  public int getRunningQuantity() {
    return this.runningQuantity;
  }

  /**
   * Modifies the quantity of this InventoryIngredient. If the quantityUnit is positive, increase
   * the quantity of this InventoryIngredient by the quantity. If the quantityUnit is negative, then
   * decrease the quantity of this InventoryIngredient by the quantity. If the quantity goes below
   * or above the the lower threshold, then the IsUnderThreshold will be turned into True or False,
   * respectively.
   *
   * @param quantityUnit the quantity of the ingredient that must be added or removed
   */
  @Override
  public void modifyQuantity(int quantityUnit) {
    boolean bool1 = this.isUnderThreshold;
    super.modifyQuantity(quantityUnit);

    if ((this.getQuantity() < lowerThreshold) && (!bool1)) {
      modifyIsUnderThreshold(true);
    } else if (((this.getQuantity() > lowerThreshold) && (bool1))) {
      modifyIsUnderThreshold(false);
    }
  }

  /**
   * Modifies the quantity of this InventoryIngredient. If the quantityUnit is positive, increase
   * the quantity of this InventoryIngredient by the quantity. If the quantityUnit is negative, then
   * decrease the quantity of this InventoryIngredient by the quantity. If the quantity goes below
   * or above the the lower threshold, then the IsUnderThreshold will be turned into True or False,
   * respectively.
   *
   * @param quantityUnit the quantity
   */
  public void modifyRunningQuantity(int quantityUnit) {
    this.runningQuantity += quantityUnit;
  }

  /**
   * Modifies the IsUnderThreshold variable of this InventoryIngredient to boolean bool
   *
   * @param bool whether the isUnderThreshold must be converted into True or False
   */
  private void modifyIsUnderThreshold(boolean bool) {
    this.isUnderThreshold = bool;
  }

  /**
   * Returns true iff the IsUnderThreshold of this InventoryIngredient is true. Otherwise returns false.
   *
   * @return true iff the IsUnderThreshold of this InventoryIngredient is true. Otherwise return false.
   */
  public boolean getIsUnderThreshold() {
    return this.isUnderThreshold;
  }

  /**
   * Sets the runningQuantity of this InventoryIngredient as int amount
   * @param amount the amount that the runningQuantity of this InventoryIngredient must be set as
   */
  public void setRunningQuantity(int amount) {
    this.runningQuantity = amount;
  }
}
