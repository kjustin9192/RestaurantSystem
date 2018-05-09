package backend.inventory;

import java.io.Serializable;
import java.util.HashMap;

/**
 * The DishRecipe class represents the recipe that specifies which DishIngredients will be used and
 * the quantity of each DishIngredient required to complete this Dish. The DishRecipe also describes
 * the price of a Dish created following the recipe, without adjusting any of the Ingredients.
 *
 * <p>The class includes methods for adjusting the DishIngredients in the Dish, updating the
 * ingredients in the Restaurant backend.inventory, assigning the Dish to a Table that ordered it,
 * and creating a string with the name of its dish and its price.
 */
public class DishRecipe implements Serializable {
  protected String name;
  protected HashMap<String, DishIngredient> ingredientsRequired = new HashMap<>();
  // private int dishNumber;
  // private static int countDish = 0;
  protected float price;
  // private Table table;
  // private boolean hasBeenDelivered;
  // private static final Logger logger = Logger.getLogger(RestaurantLogger.class.getName());

  /**
   * Constructor that takes the name of the dishRecipe, price of the DishRecipe the list of names of
   * the DishIngredients; this constructor is used to create the dishes in the Menu
   *
   * @param dishRecipeName the name of the DishRecipe
   * @param price the price of the Dish created following DishRecipe would price in dollars without
   *     adjusting DishIngredient
   * @param ingredients the list of names of the Ingredients used in this DishRecipe
   */
  public DishRecipe(String dishRecipeName, float price, String[] ingredients) {
    this.name = dishRecipeName;
    this.price = price;
    // hasBeenDelivered = false;
    for (String dishIngredient : ingredients) {
      String[] item = dishIngredient.split(":");
      int[] limit = {Integer.parseInt(item[2]), Integer.parseInt(item[3])};
      DishIngredient in =
          new DishIngredient(item[0], Integer.parseInt(item[1]), limit[0], limit[1]);
      ingredientsRequired.put(item[0], in);
    }
  }

  /**
   * Constructor that takes the name of the Dish, price of the Dish, and the HashMap with Strings as
   * keys and DishIngredient as values
   *
   * @param dishName the name of the Dish
   * @param dishPrice the price of the Dish created following DishRecipe would price in dollars
   *     without adjusting DishIngredient
   * @param ingredientsRequired the HashMap with Strings as keys and DishIngredient as values
   */
  public DishRecipe(
      String dishName, float dishPrice, HashMap<String, DishIngredient> ingredientsRequired) {
    this.name = dishName;
    this.price = dishPrice;
    // hasBeenDelivered = false;
    for (String name : ingredientsRequired.keySet()) {
      this.ingredientsRequired.put(name, new DishIngredient(ingredientsRequired.get(name)));
    }
  }

  /**
   * Returns the name of this DishRecipe
   *
   * @return the name of this DishRecipe
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the price of the DishRecipe
   *
   * @return the price of the Dish created following DishRecipe would price in dollars without
   *     adjusting DishIngredient
   */
  public float getPrice() {
    return price;
  }

  /**
   * Returns the name of the DishRecipe and the price of the Dish created following DishRecipe would
   * price in dollars without adjusting DishIngredient
   *
   * @return the name of the dish and its price
   */
  public String toString() {
    return String.format("%-20s: $%.2f", name, price);
  }

  /**
   * Returns the HashMap of string of the DishRecipe and the DishIngredient dish ingredient for this
   * dish
   *
   * @return Return the HashMap of string of the dish name and the DishIngredient dish ingredient
   *     for this dish
   */
  public HashMap<String, DishIngredient> getIngredientsRequired() {
    return ingredientsRequired;
  }
}
