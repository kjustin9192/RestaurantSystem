package frontend.GUI;

import backend.inventory.Dish;
import backend.server.Packet;
import backend.table.Order;
import frontend.client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

import java.util.LinkedList;

/** The controller for DishesNotDelivered GUI */
public class DishesNotDeliveredController {
  @FXML private TableView tableView;
  private Client client = Client.getInstance();

  /** Update amount of ingredients on the table view. */
  private void updateDishesOnTableView() {
    ObservableList<Dish> allDishes = FXCollections.observableArrayList();
    allDishes.addAll(getDishesCompleted());
    allDishes.addAll(getDishesInProgress());
    allDishes.addAll(getDishesInOrderQueue());

    tableView.setItems(allDishes);
  }

  @FXML
  /** Called when 'Refresh' button is clicked. Refreshes all the amount of ingredients. */
  private void refreshButtonClicked() {
    updateDishesOnTableView();
  }

  @FXML
  private void initialize() {
    updateDishesOnTableView();
  }

  /**
   * Returns all the dishes that are cooked but not delivered yet.
   *
   * @return all the dishes that are cooked but not delivered yet.
   */
  private ObservableList<Dish> getDishesCompleted() {
    ObservableList<Dish> dishes = FXCollections.observableArrayList();

    LinkedList<Dish> dishesCompleted =
        (LinkedList<Dish>) client.sendRequest(Packet.REQUESTDISHESCOMPLETED);

    dishes.addAll(dishesCompleted);

    return dishes;
  }

  /**
   * Returns all dishes that are being cooked.
   *
   * @return all dishes that are being cooked.
   */
  private ObservableList<Dish> getDishesInProgress() {
    ObservableList<Dish> dishes = FXCollections.observableArrayList();

    LinkedList<Dish> dishesInProgress =
        (LinkedList<Dish>) client.sendRequest(Packet.REQUESTDISHESINPROGRESS);

    dishes.addAll(dishesInProgress);

    return dishes;
  }

  /**
   * Returns all dishes in the queue that are waiting for cook to confirm.
   *
   * @return all dishes in the queue that are waiting for cook to confirm.
   */
  private ObservableList<Dish> getDishesInOrderQueue() {
    ObservableList<Dish> dishes = FXCollections.observableArrayList();

    LinkedList<Order> ordersInQueue =
        (LinkedList<Order>) client.sendRequest(Packet.REQUESTORDERSINQUEUE);

    for (Order o : ordersInQueue) {
      dishes.addAll(o.getDishes());
    }

    return dishes;
  }
}
