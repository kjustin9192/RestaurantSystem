package frontend.GUI;

import backend.inventory.Inventory;
import backend.inventory.InventoryIngredient;
import backend.server.Packet;
import frontend.client.Client;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.HashMap;

/** the controller for InventoryDisplay GUI */
public class InventoryDisplayController {
  @FXML private GridPane tableView;
  private Client client = Client.getInstance();
  volatile HashMap<String, InventoryIngredient> defaultInventory =
      (HashMap<String, InventoryIngredient>)
          client.sendRequest(
              Packet
                  .REQUESTINVENTORY); // TODO should get menu from web ComputerServer requestMenu()
  private Inventory inventory = Inventory.getInstance();

  /** initialize the GUI */
  public void initialize() {
    inventory.setStock(defaultInventory);
    HashMap<String, Integer> ingredients =
        (HashMap<String, Integer>) client.sendRequest(Packet.REQUESTQUANTITIES);
    int i = 0;
    for (String in : ingredients.keySet()) {
      tableView.add(new Text(in), 0, i);
      tableView.add(new Text("" + ingredients.get(in)), 1, i);
      i++;
    }
  }
}
