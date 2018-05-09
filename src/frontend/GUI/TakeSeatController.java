package frontend.GUI;

import backend.server.Packet;
import frontend.client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

/** the controller for take seat GUI */
public class TakeSeatController {
  @FXML private TextField tf;
  @FXML private Button confirmButton, cancelButton;
  int tableNumber;
  private int myId;
  ArrayList<Rectangle> rectangleArrayList = new ArrayList<>();

  private final Client client = Client.getInstance();

  /**
   * Set id of this employee.
   *
   * @param id Id of this employee.
   */
  public void setMyId(int id) {
    this.myId = id;
  }

  /**
   * set the table number for the take seat button
   *
   * @param tableNumber the table that the customer is taking seat
   */
  public void setTableNumber(int tableNumber) {
    this.tableNumber = tableNumber;
  }

  @FXML
  private void confirmButtonClicked() {
    if (tf.getText().length() > 0 && Integer.parseInt(tf.getText()) > 0) {
      // Change the color of the table into COLOR_OCCUPIED

      ArrayList<Object> parameters = new ArrayList<>();
      parameters.add(tableNumber);
      parameters.add(Integer.parseInt(tf.getText())); // TODO: Should be number of customers
      client.sendEvent(Packet.TAKESEAT, parameters);
      // TODO: In backend, call takeSeat() method (need number of customers)

      // close the window
      ((Stage) confirmButton.getScene().getWindow()).close();
    }
  }

  /** cancel the current action */
  @FXML
  private void cancelButtonClicked() {
    // close the window
    ((Stage) cancelButton.getScene().getWindow()).close();
  }

  /** start up the code after init */
  public void start() {
    tf.textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue.matches("\\d*")) {
                tf.setText(newValue.replaceAll("[^\\d]", ""));
              }
            });
  }
}
