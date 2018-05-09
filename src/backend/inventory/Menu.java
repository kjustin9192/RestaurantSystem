package backend.inventory;

import java.io.*;
import java.util.HashMap;

/**
 * The class represents the menu of this restaurant. It stores all the DishRecipes - the dishes offered by the
 * restaurant.
 */
public class Menu implements Serializable {
  private static Menu menu = new Menu();

  private HashMap<String, DishRecipe> menuDishes = new HashMap<>();
  //    private static Inventory backend.inventory;
  //
  //    public Menu(Inventory backend.inventory)throws IOException{
  //        this.backend.inventory = backend.inventory;
  //        create();
  //    }

  private Menu() {}

  public void readFromFile() {
    try {
      menu.create();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Menu getMenu() {
    return menu;
  }

  public Object readResolve() {
    return getMenu();
  }

  /** Creates a menu using the provided phase1/menu.txt file */
  private void create() throws IOException {
    try (BufferedReader fileReader = new BufferedReader(new FileReader("menu.txt"))) {

      // Print the lines from f prefaced with the line number,
      // starting at 1.
      String line = fileReader.readLine();
      while (line != null) {
        String[] items = line.split(";");
        String name = items[0];
        float price = Integer.parseInt(items[1]);
        String[] ingredients = items[2].split(",");
        this.menuDishes.put(name, new DishRecipe(name, price, ingredients));
        line = fileReader.readLine();
      }
    }
  }

  /**
   * TODO: Describe what this method does
   *
   * @param menuDishes TODO: Describe what
   */
  public void setDishes(HashMap<String, DishRecipe> menuDishes) {
    menu.menuDishes = menuDishes;
  }

  /**
   * Returns a copy of the Dish dish
   *
   * @param name the name of the Dish dish
   * @return the copy of the Dish dish
   */
  public Dish createNewDishd(String name) {
    DishRecipe dish = menu.menuDishes.get(name);
    return new Dish(dish);
  }

  /**
   * Returns the HashMap with names of the Dishes as keys and DishRecipes as values in this menu.
   * @return the HashMap with names of the Dishes as keys and DishRecipes as values in this menu.
   */
  public HashMap<String, DishRecipe> getDishes() {
    return menu.menuDishes;
  }
}
