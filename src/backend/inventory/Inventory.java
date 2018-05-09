package backend.inventory;

import backend.logger.RestaurantLogger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Inventory class represents the backend.inventory of ingredients in the Restaurant.
 *
 * <p>Inventory methods include, but are not limited to, modifying quantity of each ingredient in
 * the backend.inventory, adding Ingredient to the backend.inventory, and creating a String that
 * lists each ingredient in the backend.inventory and its stock.
 */
public class Inventory implements Serializable {
  private static Inventory instance = new Inventory();
  private volatile HashMap<String, InventoryIngredient> ingredientsInventory = new HashMap<>();
  private static final Logger logger = Logger.getLogger(RestaurantLogger.class.getName());
  private ArrayList<String> requested = new ArrayList<>();

  /**
   * Returns the Ingredient ingredient stored in the backend.inventory
   *
   * @param ingredientName the name of the Ingredient ingredient
   * @return the Ingredient ingredient that has the matching ingredientName
   */
  public InventoryIngredient getIngredient(String ingredientName) {
    return ingredientsInventory.get(ingredientName);
  }

  private Inventory() {}

  public static Inventory getInstance() {
    return instance;
  }

  public Object readResolve() {
    return getInstance();
  }

  public void setStock(HashMap<String, InventoryIngredient> in) {
    instance.ingredientsInventory = in;
  }

  public HashMap modifyIngredientRunningQuantity(String ingredientName, int quantityUnits) {
    InventoryIngredient stockIngredient = ingredientsInventory.get(ingredientName);
    stockIngredient.modifyRunningQuantity(quantityUnits);
    HashMap<String, Integer> newDisplayQuantity = new HashMap<>();
    newDisplayQuantity.put(stockIngredient.getName(), stockIngredient.getRunningQuantity());
    return newDisplayQuantity;
  }

  public HashMap modifyIngredientRunningQuantity(
      ArrayList<DishIngredient> dishIngredientList, boolean shouldDecreaseQuantity) {
    HashMap<String, Integer> newDisplayQuantity = new HashMap<>();

    for (DishIngredient dishIngredient : dishIngredientList) {
      InventoryIngredient stockIngredient = ingredientsInventory.get(dishIngredient.getName());

      int quantityUnits = -1 * dishIngredient.getQuantity();

      if (!shouldDecreaseQuantity) {
        quantityUnits *= -1;
      }
      stockIngredient.modifyRunningQuantity(quantityUnits);
      newDisplayQuantity.put(stockIngredient.getName(), stockIngredient.getRunningQuantity());
    }
    return newDisplayQuantity;
  }

  public boolean isInventoryIngredientEnough(String ingredientName, int quantityUnits) {
    InventoryIngredient stockIngredient = ingredientsInventory.get(ingredientName);
    if (stockIngredient.getRunningQuantity() > quantityUnits) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Modifies the quantity of the ingredient in the Inventory by quantityUnits; if quantityUnits is
   * negative, then subtract the quantity of the ingredient in the Inventory by quantityUnits
   *
   * @param ingredientName the name of the Ingredient to be added or subtracted
   * @param quantityUnits quantity of the Ingredient to be added or subtracted
   */
  public void modifyIngredientQuantity(String ingredientName, int quantityUnits) {
    InventoryIngredient stockIngredient = ingredientsInventory.get(ingredientName);
    boolean isAlreadyLow = stockIngredient.getIsUnderThreshold();
    stockIngredient.modifyQuantity(quantityUnits);
    boolean isCurrentlyLow = stockIngredient.getIsUnderThreshold();
    // if this InventoryIngredient was not already below the threshold, then
    // execute createRequest
    if (!isAlreadyLow && isCurrentlyLow) {
      createRequest(ingredientName);
    }
  }

  /**
   * Create a text sendRequest to restock the Inventory Ingredient that has the same name as
   * ingredientName
   *
   * @param ingredientName the name of the InventoryIngredient that needs to be requested for
   *     restock
   */
  private void createRequest(String ingredientName) {
    // create a sendRequest as text that is to be stored in requests.txt for the manager
    // to cut and paste into n email
    // Default amount to sendRequest is 20 units
    // The manager can manually change that amount when creating the email
    requested.add(ingredientName);
    BufferedWriter bw;
    try (BufferedReader fileReader = new BufferedReader(new FileReader("request.txt"))) {
      String myContent = ingredientName + " 20";
      // Specify the file name and path here

      String line = fileReader.readLine();
      StringBuilder outPut = new StringBuilder();
      while (line != null) {
        outPut.append(line).append("\n");
        line = fileReader.readLine();
      }

      File file = new File("request.txt");

      /* This logic will make sure that the file
       * gets created if it is not present at the
       * specified location*/
      FileWriter fw = new FileWriter(file);
      bw = new BufferedWriter(fw);
      bw.write(outPut + myContent);
      logger.info("Request updated: " + ingredientName);
      bw.close();

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  //  public ArrayList<String> getRequests() {
  // TODO: REQUEST Manager calls this to get a ArrayList of ingredientNames to be requested
  //  }

  /**
   * Adds the InventoryIngredient ingredient to the backend.inventory
   *
   * @param ingredient The InventoryIngredient ingredient to be added to the backend.inventory
   */
  public void add(InventoryIngredient ingredient) {
    ingredientsInventory.put(ingredient.getName(), ingredient);
  }

  /** Returns the String of list of ingredients and its stock */
  public void inventoryToString() {

    ArrayList<String> listOfKeys = new ArrayList<>(ingredientsInventory.keySet());

    listOfKeys.sort(String.CASE_INSENSITIVE_ORDER);

    StringBuilder logString = new StringBuilder("List of ingredients in stock: \n");
    for (String ingredientName : listOfKeys) {
      logString.append(
          String.format(
              "%-17s %d%n",
              ingredientName, ingredientsInventory.get(ingredientName).getQuantity()));
    }
    logger.info(logString.toString());
  }

  public HashMap<String, InventoryIngredient> getIngredientsInventory() {
    return ingredientsInventory;
  }

  /**
   * return the requested ingredient
   *
   * @return the requested ingredient
   */
  public ArrayList getRequests() {
    return requested;
  }

  public void removeFromRequest(String name) {
    requested.remove(name);
  }
}
