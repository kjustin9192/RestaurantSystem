package backend.inventory;

import java.io.Serializable;

/** Ingredient class represents food ingredients. */
public abstract class Ingredient implements Serializable {
  private String name;
  private int quantity;

  /**
   * Constructor for creating Ingredient with name and quantity of the ingredient
   *
   * @param name the name of this Ingredient
   * @param quantity the quantity of this Ingredient
   */
  public Ingredient(String name, int quantity) {
    this.name = name;
    this.quantity = quantity;
  }

  /**
   * Returns the quantity of this Ingredient.
   *
   * @return the quantity of this Ingredient.
   */
  public int getQuantity() {
    return this.quantity;
  }

  /**
   * Adds the quantity of this Ingredient.
   *
   * @param quantityUnit the quantity of the ingredient that must be added for this Ingredient
   */
  public void modifyQuantity(int quantityUnit) {
    this.quantity += quantityUnit;
  }

  /**
   * Returns the name of this Ingredient
   *
   * @return the name of this Ingredient
   */
  public String getName() {
    return this.name;
  }
}
