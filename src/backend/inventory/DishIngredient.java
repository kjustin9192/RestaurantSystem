package backend.inventory;

/**
 * The DishIngredient class represents the ingredients used in a Dish
 *
 * <p>DishIngredient methods include checking whether a suggested quantity for the DishIngredient in
 * the Dish is acceptable as per the minimum and maximum quantity stipulated in each Dish
 */
public class DishIngredient extends Ingredient {
  private int minimumQuantity;
  private int maximumQuantity;

  /**
   * Constructor for DishIngredient that takes in the name, quantity, minimum and maximum quantity
   * of this ingredient
   *
   * @param name the name of this DishIngredient
   * @param quantity the quantity of this DishIngredient
   * @param minimumQuantity the minimum quantity for this DishIngredient
   * @param maximumQuantity the maximum quantity for this DishIngredient
   */
  DishIngredient(String name, int quantity, int minimumQuantity, int maximumQuantity) {
    super(name, quantity);
    this.minimumQuantity = minimumQuantity;
    this.maximumQuantity = maximumQuantity;
  }

  DishIngredient(DishIngredient dishIngredient) {
    super(dishIngredient.getName(), dishIngredient.getQuantity());
    this.minimumQuantity = dishIngredient.minimumQuantity;
    this.maximumQuantity = dishIngredient.maximumQuantity;
  }

  /**
   * Returns true iff the given suggested new quantity for this Ingredient is within the acceptable
   * range according to its lower and upper threshold; this method is reserved for the Ingredient
   * objects in Dish objects that are made to order (not the Dish objects in the menu, which need
   * not customization)
   *
   * @param n the suggested quantity for this Ingredient
   * @return boolean statement
   */
  public boolean allowed(int n) {
    return (n + getQuantity() >= minimumQuantity && n + getQuantity() <= maximumQuantity);
  }
}
