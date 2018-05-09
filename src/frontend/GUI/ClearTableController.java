package frontend.GUI;

import backend.server.Packet;
import frontend.client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

/** The controller for ClearTableAlertBox GUI */
public class ClearTableController {
  @FXML private Button yesButton, noButton;
  @FXML private Label tableNumberLabel;
  private int tableNumber;
  ArrayList<Rectangle> rectangleArrayList = new ArrayList<>();
  private Client client = Client.getInstance();

  /**
   * Set table number of the table being cleared
   *
   * @param tableNumber number of table that is being cleared
   */
  public void setTableNumber(int tableNumber) {
    this.tableNumber = tableNumber;
  }

  /**
   * Set text of the alert box.
   *
   * @param s the text that is printed on the alert box.
   */
  public void setText(String s) {
    tableNumberLabel.setText(s);
  }

  @FXML
  /** clear the table, and close the alert box. */
  private void yesButtonClicked() {
    client.sendEvent(Packet.CLEARTABLE, tableNumber);

    ((Stage) yesButton.getScene().getWindow()).close();
  }

  @FXML
  /** Close alert box without any change. */
  private void noButtonClicked() {
    ((Stage) noButton.getScene().getWindow()).close();
  }
}
