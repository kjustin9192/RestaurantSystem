package frontend.GUI;

import backend.logger.BillLogger;
import backend.server.Packet;
import frontend.client.Client;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

/** A main class for GUI. GUI shows up as the main method runs. */
public class FXMain extends Application {
  Scene mainScene;
  final String TITLE = "Welcome to Four Guys restaurant system";
  public final int WIDTH = 600;
  public final int HEIGHT = 600;

  public static Client client = Client.getInstance();

  public static void main(String[] args) {
    BillLogger.init();
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    // load starter interface
    FXMLLoader startLoader =
        new FXMLLoader(this.getClass().getResource("Start.fxml"));
    GridPane startScene = startLoader.load();
    mainScene = new Scene(startScene, WIDTH, HEIGHT);
    BackgroundImage mainImage =
        new BackgroundImage(
            new Image("frontend/GUI/images/hp.jpg", 600, 600, false, true),
            BackgroundRepeat.REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    startScene.setBackground(new Background(mainImage));

    StartSceneController paneController = startLoader.getController();
    client.storeController("startController",paneController);
    paneController.start();

    primaryStage.setTitle(TITLE);
    primaryStage.setScene(mainScene);
    primaryStage.show();
    primaryStage.setOnCloseRequest(
        t -> {
          System.out.println("~~~ Shutting down in 3 seconds ~~~");
          try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          client.sendEvent(Packet.DISCONNECT);
          Platform.exit();
          System.exit(0);
        });
  }
}
