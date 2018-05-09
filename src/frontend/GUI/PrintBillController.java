package frontend.GUI;

import backend.inventory.Dish;
import backend.logger.BillLogger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.util.logging.Logger;

/** the controller for print bill GUI */
public class PrintBillController {
  private static final Logger logger = Logger.getLogger(BillLogger.class.getName());
  private int tableNumber;
  private float totalCost;
  private int numOfCustomers;
  @FXML Label tableNumberLabel;
  @FXML Label billLabel;
  @FXML TableView tableView;

  private String billInitial = "Choose dishes";
  private String billHeader = "=== PRINT BILL ===\n";
  private StringBuilder billBody = new StringBuilder();

  /**
   * Set table number of this table that asked for bill.
   *
   * @param tableNumber number of the table that asked for bill.
   */
  public void setTableNumber(int tableNumber) {
    this.tableNumber = tableNumber;
  }

  @FXML
  private void addBillButtonClicked() {
    Dish selectedDish = (Dish) tableView.getSelectionModel().getSelectedItem();
    ObservableList<Dish> observableList = tableView.getItems();
    if (selectedDish != null) {
      billBody.append(selectedDish.toString() + "\n"); // add dish to bill

      // remove item from table view
      observableList.remove(selectedDish);
      tableView.setItems(observableList);

      totalCost += selectedDish.getPrice();

      String result =
          billHeader
              + billBody
              + "SubTotal: $"
              + String.format("%.2f", totalCost)
              + "\n"
              + "Tax: $"
              + String.format("%.2f", totalCost * 0.13)
              + "\n";

      if (numOfCustomers >= 8) {
        result += "Gratuity: $" + String.format("%.2f", totalCost * 0.15);
        result += "\n\n" + "Total: $" + String.format("%.2f", totalCost * 1.28);
      } else {
        result += "\n" + "Total: $" + String.format("%.2f", totalCost * 1.13);
      }
      billLabel.setText(result);
    }
  }

  @FXML
  private void payBillButtonClicked() {
    if (!billLabel.getText().equals(billInitial)) {
      logger.info(billLabel.getText());
    }
    billLabel.setText(billInitial);
    totalCost = 0;
  }

  /**
   * Set the table view with items in <code>observableList</code>.
   *
   * @param observableList the list that contains items that are going to be displayed on table
   *     view.
   */
  public void setTableView(ObservableList<Dish> observableList) {
    tableView.setItems(observableList);
    if (!observableList.isEmpty()) {
      numOfCustomers = observableList.get(0).getTable().getNumOfCustomer();
    }
  }
}
