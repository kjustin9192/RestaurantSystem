package frontend.GUI;

import backend.inventory.*;
import backend.server.Packet;
import backend.table.Order;
import frontend.client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/** the controller for menu GUI */
public class MenuController {
  @FXML GridPane tableView = new GridPane();
  private Stage window;
  private final Client client = Client.getInstance();
  private int numoforder = 0;
  volatile HashMap<String, InventoryIngredient> defaultInventory =
      (HashMap<String, InventoryIngredient>)
          client.sendRequest(
              Packet
                  .REQUESTINVENTORY); // TODO should get menu from web ComputerServer requestMenu()
  private final Inventory inventory = Inventory.getInstance();
  private ArrayList<Dish> recipe = new ArrayList<>();
  private int tableNumber;

  volatile HashMap<String, DishRecipe> menuDishes =
      (HashMap<String, DishRecipe>) client.sendRequest(Packet.REQUESTMENU);
  private final Menu menu = Menu.getMenu();

  private int myId;

  @FXML private TableView<Dish> menuColumn = new TableView<>();

  @FXML private TableView<Dish> orderColumn = new TableView<>();

  public void setMyId(int id) {
    this.myId = id;
  }

  private Order dishOrder;
  private ArrayList<Dish> order;

  /**
   * set up the table number for this order
   *
   * @param tableNumber the table that ordering
   */
  public void setTableNumber(int tableNumber) {
    this.tableNumber = tableNumber;
  }

  /**
   * set up the stage that is display GUI
   *
   * @param stage the stage that is display GUI
   */
  public void setStage(Stage stage) {
    this.window = stage;
  }

  /**
   * update any difference in local inventory and back end inventory
   *
   * @param newDisplayQuantity the quantity that changed
   */
  public void updateRunningQuantity(HashMap newDisplayQuantity) {
    for (Object i : newDisplayQuantity.keySet()) {
      String ingredientName = (String) i;
      int runningQuantity = (int) newDisplayQuantity.get(i);
      InventoryIngredient inventoryIngredient = inventory.getIngredient(ingredientName);
      inventoryIngredient.setRunningQuantity(runningQuantity);
    }
    updateMenu();
  }

  /** Update the menu, grey out dish that are unavailable */
  public void updateMenu() {
    ObservableList<Dish> dishes = FXCollections.observableArrayList();
    for (Dish dish : recipe) {
      boolean cookable = dish.ableToCook(inventory);

      if (cookable) {
        dishes.add(dish);
      }
    }
    menuColumn.setItems(dishes);
  }

  @FXML
  private void getOrderButtonClicked() {
    Dish selectedDish = menuColumn.getSelectionModel().getSelectedItem();
    if (selectedDish != null) {
      // set up the ingredient adjustment interface
      Stage st = new Stage();
      Dish dish = new Dish(selectedDish);

      // pass ingredient to server
      HashMap<String, DishIngredient> ingredients = dish.getIngredientsRequired();
      ArrayList<DishIngredient> dishIngredients = new ArrayList<>();
      for (String in : ingredients.keySet()) {
        dishIngredients.add(ingredients.get(in));
      }
      client.sendAdjustIngredientRequest(dishIngredients, true);

      FXMLLoader ingredientLoader =
          new FXMLLoader(this.getClass().getResource("src/frontend/GUI/Ingredient.fxml"));
      try {
        GridPane ingredient = ingredientLoader.load();
        IngredientController controller = ingredientLoader.getController();
        controller.setDish(dish);
        controller.setController(this);
        Scene ingredientScene = new Scene(ingredient, 400, 400);
        st.setScene(ingredientScene);
        st.show();
      } catch (IOException e1) {
        System.out.println(ingredientLoader);
      }
    }
  }

  /**
   * Add a dish to the order.
   *
   * @param dish The dish that is being added to the order.
   */
  public void addDish(Dish dish) {
    order.add(dish);
    updateOrder();
  }

  @FXML
  private void getDeleteButtonClicked() {
    Dish selectedDish = orderColumn.getSelectionModel().getSelectedItem();

    if (selectedDish != null) {
      order.remove(selectedDish);
      HashMap<String, DishIngredient> ingredients1 = selectedDish.getIngredientsRequired();
      ArrayList<DishIngredient> dishIngredients1 = new ArrayList<>();
      for (String in : ingredients1.keySet()) {
        dishIngredients1.add(ingredients1.get(in));
      }
      client.sendAdjustIngredientRequest(dishIngredients1, false);
      updateOrder();
    }
  }

  @FXML
  private void getSubmitButtonClicked() {
    for (Dish dish : order) {
      dishOrder.addDish(dish);
    }
    ArrayList<Object> info = new ArrayList<>();
    info.add(tableNumber);
    info.add(dishOrder);
    client.sendEvent(Packet.ENTERMENU, info);

    numoforder = 0;
    dishOrder = new Order();
    order = new ArrayList<>();
    updateOrder();

    ((Stage) menuColumn.getScene().getWindow()).close();
  }

  private void updateOrder() {
    ObservableList<Dish> dishes = FXCollections.observableArrayList();
    for (Dish dish : order) {
      dishes.add(dish);
    }
    orderColumn.setItems(dishes);
  }

  /** initialize the GUI */
  public void initialize() {
    menu.setDishes(menuDishes);
    inventory.setStock(defaultInventory);
    HashMap<String, DishRecipe> dishes = menu.getDishes();

    dishOrder = new Order();
    order = new ArrayList<>();
    for (String di : dishes.keySet()) {
      recipe.add(new Dish(dishes.get(di)));
    }
    updateMenu();
  }
}
