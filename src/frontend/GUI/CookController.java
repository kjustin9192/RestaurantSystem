package frontend.GUI;

import backend.inventory.Dish;
import backend.server.Packet;
import backend.table.Order;
import frontend.client.Client;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/** The controller for Cook GUI */
public class CookController {
  @FXML HBox hBox;
  @FXML Button getOrderButton;
  @FXML Label numOfOrderLabel;
  private int numOfOrdersInQueue;
  private int myId;
  private Client client = Client.getInstance();

  /* table view of dishes in progress */
  @FXML private TableView<Dish> tableViewDishesInProgress = new TableView<>();

  /* table view of dishes to be cooked */
  @FXML private TableView<Dish> tableViewDishesInQueue = new TableView<>();

  /**
   * Set id of this employee.
   *
   * @param id Id of this employee.
   */
  public void setMyId(int id) {
    myId = id;
  }

  /** Update table view of dishes that are in progress. */
  public void updateDishesInProgressOnTableView() {
    tableViewDishesInProgress.setItems(getDishesInProgress());
  }

  /**
   * Update table view of dishes that are in progress.
   *
   * @param dishesInProgress updated list of dishes that will be shown on table view.
   */
  public void updateDishesInProgressOnTableView(LinkedList<Dish> dishesInProgress) {
    ObservableList<Dish> observableList = FXCollections.observableArrayList();
    observableList.addAll(dishesInProgress);
    tableViewDishesInProgress.setItems(observableList);
  }

  /** Update table view of dishes that are in queue and waiting to be confirmed by cook. */
  public void updateOrdersInQueueOnTableView() {
    tableViewDishesInQueue.setItems(getDishesInFirstOrderQueue());
  }

  /**
   * Update table view of dishes that are in queue and waiting to be confirmed by cook.
   *
   * @param ordersInQueue updated list of orders that needs to be confirmed by cook.
   */
  public void updateOrdersInQueueOnTableView(LinkedList<Order> ordersInQueue) {
    ObservableList<Dish> observableList = FXCollections.observableArrayList();

    ArrayList<Dish> dishes = new ArrayList<>();
    if (!ordersInQueue.isEmpty()) {
      dishes = ordersInQueue.get(0).getDishes();
    }
    numOfOrdersInQueue = ordersInQueue.size();
    Platform.runLater(
        () ->
            numOfOrderLabel.setText(
                "Number of orders in queue: " + Integer.toString(numOfOrdersInQueue)));

    observableList.addAll(dishes);
    tableViewDishesInQueue.setItems(observableList);
  }

  @FXML
  private void initialize() {
    updateDishesInProgressOnTableView();
    updateOrdersInQueueOnTableView();
  }

  /**
   * Get dishes in progress from backend server.
   *
   * @return dishes in progress from backend server.
   */
  private ObservableList<Dish> getDishesInProgress() {
    ObservableList<Dish> dishes = FXCollections.observableArrayList();

    LinkedList<Dish> dishesInProgress =
        (LinkedList<Dish>) client.sendRequest(Packet.REQUESTDISHESINPROGRESS);

    dishes.addAll(dishesInProgress);

    return dishes;
  }

  /**
   * Get dishes of first order in orders of queue, and set a number on label which is the number of
   * orders in queue.
   *
   * @return dishes of first order in orders of queue, and set a number on label which is the number
   *     of orders in queue.
   */
  private ObservableList<Dish> getDishesInFirstOrderQueue() {
    ObservableList<Dish> dishes = FXCollections.observableArrayList();

    LinkedList ordersInQueue = (LinkedList) client.sendRequest(Packet.REQUESTORDERSINQUEUE);

    numOfOrdersInQueue = ordersInQueue.size();
    numOfOrderLabel.setText("Number of orders in queue: " + Integer.toString(numOfOrdersInQueue));

    if (!ordersInQueue.isEmpty()) {
      dishes.addAll(((Order) ordersInQueue.get(0)).getDishes());
    }

    return dishes;
  }

  /** */
  @FXML
  private void finishedButtonClicked() {
    Dish selectedDish = tableViewDishesInProgress.getSelectionModel().getSelectedItem();

    if (selectedDish != null) {
      int dishNumber = selectedDish.getDishNumber();
      client.sendEvent(Packet.DISHREADY, dishNumber);
    }
  }

  @FXML
  /**
   * This method is called when 'confirm next order' button is clicked. Confirms the first order in
   * queue.
   */
  private void confirmNextOrderButtonClicked() {
    client.sendEvent(Packet.ORDERRECEIVED);
  }

  @FXML
  /**
   * This is called when 'Receive item' button is clicked. Allows to enter amount of received
   * ingredients to the server.
   */
  private void receiveItem() {
    Stage window = new Stage();
    window.initModality(Modality.APPLICATION_MODAL);

    try {
      FXMLLoader loader =
          new FXMLLoader(this.getClass().getResource("src/frontend/GUI/ReceiveItem.fxml"));
      Parent root = loader.load();
      window.setTitle("Receive Item");
      window.setScene(new Scene(root, 400, 200));
      ReceiveItemController controller = loader.getController();
      controller.setMyId(myId);
      controller.start();
      window.showAndWait();
    } catch (IOException e) {
      System.out.println("println item error");
    }
  }

  @FXML
  /** Called when 'Sign out' button is clicked. Logs out and goes back to log-in window. */
  private void logOff() throws IOException {
    client.sendEvent(Packet.LOGOFF);

    FXMLLoader startLoader =
        new FXMLLoader(this.getClass().getResource("src/frontend/GUI/Start.fxml"));
    GridPane root = startLoader.load();
    Scene mainScene = new Scene(root, 600, 600);
    BackgroundImage mainImage =
        new BackgroundImage(
            new Image("hp.jpg", 600, 600, false, true),
            BackgroundRepeat.REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    root.setBackground(new Background(mainImage));

    StartSceneController paneController = startLoader.getController();
    paneController.start();

    Stage window = (Stage) (hBox.getScene().getWindow());

    window.setTitle("Welcome to Four Guys Restaurant System");
    window.setScene(mainScene);
    window.show();
  }
}
