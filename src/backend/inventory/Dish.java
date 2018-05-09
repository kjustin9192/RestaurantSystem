package backend.inventory;

import backend.logger.RestaurantLogger;
import backend.table.Table;

import java.util.logging.Logger;

/**
 * The Dish class represents the dish that the restaurant offers in its menu, and also the dish that
 * the customer orders, which may be different from the default dish listed in the menu if
 * adjustments are made in certain ingredients. There may be multiple Dish classes with the same
 * name, but every dish is assigned a unique dishNumber.
 *
 * <p>The class includes methods for adjusting the ingredients in the Dish, updating the ingredients
 * in the Restaurant backend.inventory, assigning the Dish to a Table that ordered it, and creating
 * a string with the name of its dish and its price.
 */
public class Dish extends DishRecipe {
  private int dishNumber;
  private static int countDish = 0;
  private Table table;
  private boolean hasBeenDelivered;
  private String status = "";
  private static final Logger logger = Logger.getLogger(RestaurantLogger.class.getName());
  private Inventory inventory = Inventory.getInstance();

  /**
   * Constructor that takes the name of the dish, price and the list of names of the ingredients;
   * this constructor is used to create the dishes in the Menu
   *
   * @param dishName is the name of the Dish
   * @param dishPrice is the price of the Dish in dollars
   * @param ingredients is the list of names of the ingredients used for this dish
   */
  public Dish(String dishName, float dishPrice, String[] ingredients) {
    super(dishName, dishPrice, ingredients);
    hasBeenDelivered = false;
  }

  public Dish(DishRecipe dishRecipe) {
    super(dishRecipe.name, dishRecipe.price, dishRecipe.ingredientsRequired);
    hasBeenDelivered = false;
  }

  /**
   * Return true if and only if it is possible for this Dish to permit the amount amount for the
   * DishIngredient ingredientName
   *
   * @param ingredientName the name of the DishIngredient
   * @param amount the total amount of DishIngredient allowed for this Dish
   * @return true if and only if it is possible for this Dish to permit the amount amount for the
   *     DishIngredient ingredientName
   */
  public boolean ableToAdjustIngredient(String ingredientName, int amount) {
    if (ingredientsRequired.get(ingredientName).allowed(amount)) {
      return true;
      // ingredientsRequired.get(ingredientName).modifyQuantity(amount);
    } else {
      return false;
      //            logger.warning(
      //                    "Adjusting " + amount + " " + ingredientName + " is not valid for dish "
      // + name);
    }
  }

  /**
   * Adjust the DishIngredient of this Dish by this int amount, increasing by the int amount if
   * amount is greater than 0, decreasing by the int amount if amount is less than 0.
   *
   * @param ingredientName the name of the ingredient
   * @param amount the amount is being adjusted
   */
  public void adjustIngredient(String ingredientName, int amount) {
    ingredientsRequired.get(ingredientName).modifyQuantity(amount);
  }

  /** Subtracts all the amounts of ingredients used in backend.inventory to make this dish. */
  public void updateIngredientsStock() {
    for (String ingredientName : ingredientsRequired.keySet()) {
      inventory.modifyIngredientQuantity(
          ingredientName, -1 * ingredientsRequired.get(ingredientName).getQuantity());
    }
  }

  /**
   * Assigns this Dish to the Table t
   *
   * @param t the table that ordered this Dish
   */
  public void assignDishToTable(Table t) {
    table = t;
  }

  /**
   * Returns true if and only if there is enough quantity secured in the inventory to cook this
   * Dish. In other words, this method returns true iff there is enough amount of
   * InventoryIngredients in the inventory for every DishIngredient required to cook this Dish
   *
   * @param in The inventory of this restaurant
   * @return true iff there is enough InventoryIngredients in the inventory to cook this Dish
   */
  public boolean ableToCook(Inventory in) {
    for (String ingredientName : ingredientsRequired.keySet()) {
      int inventoryQuantity = in.getIngredient(ingredientName).getRunningQuantity();
      int dishQuantity = ingredientsRequired.get(ingredientName).getQuantity();
      if (inventoryQuantity < dishQuantity) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the Table table that ordered this Dish
   *
   * @return the Table table that ordered this Dish
   */
  public Table getTable() {
    return table;
  }

  /**
   * Returns the name of this Dish and its price
   *
   * @return the name of the Dish and its price
   */
  public String toString() {
    //    float currentCost = hasBeenDelivered ? price : 0;
    return String.format("%-20s $%.2f", name, price);
  }

  /** Adds the price of this Dish to the Table that ordered this Dish */
  public void addCostToTable() {
    table.addCost(this);
  }

  /** Modifies the price of this dish to 0; */
  public void isCancelled() {
    price = 0;
  }

  /**
   * Returns the unique number assigned to this Dish
   *
   * @return the unique number assigned to this Dish
   */
  public int getDishNumber() {
    return dishNumber;
  }

  public int getTableNumber() {
    return table.getTableNum();
  }

  /** Assigns a unique number that identifies this dish */
  public void assignDishNumber() {
    dishNumber = ++Dish.countDish;
  }

  /** Acknowledges this Dish is sent(i.e., delivered) to the Table that ordered it */
  public void delivered() {
    hasBeenDelivered = true;
  }

  /**
   * Returns true iff this Dish has been delivered
   *
   * @return true iff
   */
  public boolean hasBeenDelivered() {
    return hasBeenDelivered;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
}
