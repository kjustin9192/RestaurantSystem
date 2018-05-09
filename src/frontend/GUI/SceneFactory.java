package frontend.GUI;

import backend.server.Packet;
import frontend.client.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

/** factory that generate scene for employees */
public class SceneFactory {
  private final int WIDTH = 600;
  private final int HEIGHT = 600;
  private final Client client = Client.getInstance();

  public SceneFactory() {}

  /**
   * create the corresponding scene for employee
   *
   * @param type the type of this employee
   * @param id the id of the employee
   * @return the corresponding scene for employee
   * @throws IOException IOException
   */
  public Scene createScene(int type, int id) throws IOException {
    Scene scene = null;
    if (type == Packet.COOKTYPE) {
      // load cook interface
      FXMLLoader cookLoader =
          new javafx.fxml.FXMLLoader(this.getClass().getResource("Cook.fxml"));
      Parent cook = cookLoader.load();
      scene = new Scene(cook, WIDTH, HEIGHT);

      CookController paneController = cookLoader.getController();
      client.storeController("cookController", paneController);
      paneController.setMyId(id);
    } else if (type == Packet.SERVERTYPE) {
      // load server interface
      FXMLLoader serverLoader =
          new javafx.fxml.FXMLLoader(this.getClass().getResource("ServerStage.fxml"));
      Parent server = serverLoader.load();
      scene = new Scene(server, WIDTH, HEIGHT);

      // load menu interface
      FXMLLoader menuLoader =
          new FXMLLoader(this.getClass().getResource("Menu.fxml"));
      Parent menu = menuLoader.load();
      Scene menuScene = new Scene(menu, WIDTH, HEIGHT);

      // injecting menu scene into the controller of the server scene
      ServerController paneController = serverLoader.getController();
      ///            paneController.setmyId(id); TODO: Figure out what this does
      client.storeController("serverController", paneController);
      paneController.setMenuScene(menuScene);
      paneController.setMyId(id);

      // injecting server scene into the controller of the menu scene
      MenuController menuPaneController = menuLoader.getController();

      client.storeController("menuController", menuPaneController);
    } else if (type == Packet.MANAGERTYPE) {
      // load manager interface
      FXMLLoader managerLoader =
          new javafx.fxml.FXMLLoader(this.getClass().getResource("Manager.fxml"));
      Parent manager = managerLoader.load();
      scene = new Scene(manager, WIDTH, HEIGHT);

      ManagerController paneController = managerLoader.getController();
      paneController.setMyId(id);
    }
    return scene;
  }
}
