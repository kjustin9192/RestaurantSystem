package frontend.GUI;

import backend.server.Packet;
import frontend.client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;

/** the controller for the request GUI */
public class RequestController {
  @FXML Label requestListLabel;
  private Client client = Client.getInstance();

  /** initialize the GUI */
  @FXML
  private void initialize() {
    ArrayList request = (ArrayList) client.sendRequest(Packet.REQUESTREQUEST);
    requestListLabel.setText("");
    for (Object o : request) {
      String name = (String) o;
      requestListLabel.setText(requestListLabel.getText() + name + ": 20\n");
    }
  }
}
