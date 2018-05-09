package frontend.client;

import backend.inventory.Dish;
import backend.inventory.DishIngredient;
import backend.server.Packet;
import backend.table.Order;
import frontend.GUI.CookController;
import frontend.GUI.MenuController;
import frontend.GUI.ServerController;
import frontend.GUI.StartSceneController;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Client.java
 * Created by Bill Ang Li
 * <p>
 * This class is responsible for communicating with the backend, requesting and receiving resources required by GUI
 */
public class Client implements Runnable {
  // Resources required for client-server communication
  private static final String IP = "127.0.0.1"; // local IP//"100.64.91.138";
  private static final int PORT = 6000;
  private Socket socket;
  private volatile boolean connected;
  private volatile boolean loggedIn = false;
  private int employeeType;
  private ObjectInputStream input;
  private ObjectOutputStream output;
  private volatile boolean requestedPacketReceived = false; // The packet request has been received
  private volatile boolean otherUpdate = true; // Some other client updated something
  private volatile Object object;

  // Creating the singleton instance of this class
  private static Client instance = new Client();

  // Controllers for the GUI
  private HashMap<String, Object> controllers = new HashMap<>();

  /**
   * Singleton constructor for client, only accessible in here
   */
  private Client() {
    this.connectAgain();
  }

  /**
   * Getter for the singleton instance of Client
   *
   * @return this instance of Client
   */
  public static Client getInstance() {
    return instance;
  }

  /**
   * Connect this Client to the ComputerServer
   */
  private boolean connect() {
    try {
      this.socket = new Socket(IP, PORT);
      this.output = new ObjectOutputStream(this.socket.getOutputStream());
      this.input = new ObjectInputStream(this.socket.getInputStream());
    } catch (IOException ioe) {
      System.err.println("Error connecting to server");
      return false;
    }

    System.out.println("Connection successful");
    return true;
  }

  /**
   * This is run when the new thread of Client is started
   * It is responsible for constantly listening for new updates from the ComputerServer
   */
  @Override
  public void run() {
    // Only listen for updates when this Client is connected to the ComputerServer
    while (this.connected) {
      try {
        this.object = this.input.readObject();
        if (object != null) {
          // Received new object in ObjectInputStream from ComputerServer
          Packet packet = (Packet) object;
//          System.out.println("Packet type: " + packet.getType());

          if (packet.getType() == Packet.LOGINCONFIRMATION) {
            // Confirm that this client has logged in successfully or not
            confirmLogIn((int) packet.getItem());
          } else if (packet.isUpdateType()) {
            // This Packet is an update from the ComputerServer (these can be sent to Client even if not requested)
//            System.out.println("Received " + packet.getItem());

            if (otherUpdate) {
              // This packet was not requested by the Client, so it is an update on the resources from changes made by
              // other clients or the ComputerServer
              switch (packet.getType()) {
                case Packet.SERVERSHUTDOWN:
                  // The ComputerServer is shutting down, so shut down this program too
                  this.send(Packet.DISCONNECT);
                  this.shutDown();
                  break;
                case Packet.RECEIVEDISHESINPROGRESS: {
                  // Update to dishesInProgress
                  LinkedList<Dish> dishesInProgress = (LinkedList<Dish>) packet.getItem();
                  CookController cookController = (CookController) controllers.get("cookController");
                  cookController.updateDishesInProgressOnTableView(dishesInProgress);
                  break;
                }
                case Packet.RECEIVEORDERSINQUEUE: {
                  // Update to ordersInQueue
                  LinkedList<Order> ordersInQueue = (LinkedList<Order>) packet.getItem();
                  CookController cookController = (CookController) controllers.get("cookController");
                  cookController.updateOrdersInQueueOnTableView(ordersInQueue);
                  break;
                }
                case Packet.RECEIVEDISHESCOMPLETED:
                  // Update to dishesCompleted
                  LinkedList<Dish> dishesCompleted = (LinkedList<Dish>) packet.getItem();
                  if (this.employeeType == Packet.SERVERTYPE) {
                    ServerController serverController = (ServerController) controllers.get("serverController");
                    serverController.updateTableView(dishesCompleted);
                  }
                  break;
                case Packet.RECEIVERUNNINGQUANTITYADJUSTMENT:
                  // Update to runningQuantity
                  HashMap newDisplayQuantity = (HashMap) packet.getItem();
                  if (this.employeeType == Packet.SERVERTYPE) {
                    MenuController menuController = (MenuController) controllers.get("menuController");
                    menuController.updateRunningQuantity(newDisplayQuantity);
                  }
                  break;
                case Packet.RECEIVETABLEOCCUPANCY:
                  // Update to tableOccupancy
                  ServerController serverController = (ServerController) controllers.get("serverController");
                  serverController.updateTableColor((ArrayList) packet.getItem());
                  break;
              }
            } else {
              // This is not an update, but actually was a resource requested by this Client
              System.out.println("The object is ready");
              this.requestedPacketReceived = true;
            }
          } else if (packet.isReceiveType()) {
            // This Packet was requested by the Client
            System.out.println("The object is ready");
            this.requestedPacketReceived = true;
          } else {
//            System.out.println("*** Packet type invalid ***");
          }
        }
      } catch (IOException e) {
        System.out.println("*** IO Exception ***");
        this.connected = false;
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

    // Closing streams and socket
    try {
      this.input.close();
      this.output.close();
      this.socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Receives a protocol and decides whether the client has logged on successfully (helper method for above method)
   *
   * @param confirmation is the protocol
   */
  private void confirmLogIn(int confirmation) {
    switch (confirmation) {
      case Packet.LOGINFAILED:
        this.requestedPacketReceived = true;
        this.loggedIn = false;
        break;
      default:
        this.requestedPacketReceived = true;
        this.loggedIn = true;
        break;
    }
  }

  /**
   * Send a Packet with the packet type and an item to be sent
   *
   * @param type is the Packet type
   * @param item is the item to be send to the ComputerServer
   */
  private void send(int type, Object item) {
    // Only send when this client is connected to the ComputerServer
    if (this.connected) {
      System.out.println("Sending " + type + " " + item);

      // Disconnect from the ComputerServer if this Packet is to send a disconnect message
      if (type == Packet.DISCONNECT) {
        this.connected = false;
      }

      // Sending the Packet
      try {
        Packet packet = new Packet(type, item);
        this.output.reset();
        this.output.writeObject(packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Send a Packet only containing the Packet type, which is the protocol
   *
   * @param type is the protocol of this message to ComputerServer
   */
  private void send(int type) {
    if (this.connected) {
      // Only send when Client is connected to ComputerServer
//      System.out.println("Sending " + type);

      // Disconnect if sending Client is disconnecting
      if (type == Packet.DISCONNECT) {
        this.connected = false;
      }

      try {
        Packet packet = new Packet(type);
        this.output.writeObject(packet);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Send a request for a resource from the ComputerServer
   *
   * @param requestType is the protocol for the request as defined in Packet
   * @return the object received by this request
   */
  public Object sendRequest(int requestType) {
    this.otherUpdate = false;
    this.send(requestType);

    // Waiting for Server to respond
    System.out.println("Waiting for ComputerServer to respond to request...");
    while (!this.requestedPacketReceived) ;

    // Return the employee type to the GUI
    System.out.println("Object is ready");
    this.requestedPacketReceived = false;
    System.out.println("Received " + ((Packet) this.object).getItem());
    this.otherUpdate = true;
    return ((Packet) this.object).getItem();
  }

  /**
   * Send a request for a resource from the ComputerServer
   *
   * @param requestType is the protocol for the request as defined in Packet
   * @param item is the item sent along in the Packet
   * @return the object that this request receives from the ComputerServer
   */
  public Object sendRequest(int requestType, Object item) {

    this.otherUpdate = false;
    this.send(requestType, item);

    // Waiting for Server to respond
    System.out.println("Waiting for ComputerServer to respond to request" + requestType + "...");
    while (!this.requestedPacketReceived) ;

    // Return the employee type to the GUI
    System.out.println("Object is ready");
    this.requestedPacketReceived = false;
    System.out.println("Received " + ((Packet) this.object).getItem());
    this.otherUpdate = true;
    return ((Packet) this.object).getItem();
  }

  /**
   * Send a log in request to the ComputerServer
   * Receives the employee type protocol if logged on successfully, Packet.LOGINFAILED otherwise
   *
   * @param id is the employeeID to be logged in
   * @return   the int response from ComputerServer
   */
  public int sendLogInRequest(String id) {
    this.send(Packet.LOGINREQUEST, Integer.parseInt(id));

    // Waiting for Server to respond
    System.out.println("Waiting for ComputerServer to respond to log in request...");
    while (!this.requestedPacketReceived) ;

    // Return the employee type to the GUI
    System.out.println("Employee Type is ready");
    this.requestedPacketReceived = false;
    int employeeType = (int) ((Packet) this.object).getItem();
    if (employeeType != Packet.LOGINFAILED) {
      this.employeeType = employeeType;
    }
    return employeeType;
  }

  /* NOTE: sendAdjustIngredientRequest only changes runningQuantity since it is used for ingredient is adjusted
   *       when ordering a dish, so all instances of the Server will get real-time updates
   *       The quantity of an ingredient is only changed when events are called (e.g. receive ingredient or when
   *       Cook finishes making a dish)
   */

  /**
   * Sends the ComputerServer an ingredient to have the running quantity adjusted
   * This overloaded method is used for multiple ingredients are being adjusted (i.e. ordering something)
   *
   * It is called sendAdjustIngredientRequest because it is used only when individual ingredients are adjusted in the
   * dish is being ordered
   *
   * @param dishIngredients is the ArrayList of ingredients that need to be changed (changes in quantity are all pos.)
   * @param shouldSubtractQuantity is true if the quantity after this method is called should be lower
   */
  public void sendAdjustIngredientRequest(ArrayList<DishIngredient> dishIngredients, boolean shouldSubtractQuantity) {
    this.otherUpdate = false;
    this.send(Packet.ADJUSTINGREDIENT, new Object[]{dishIngredients, shouldSubtractQuantity});

    // Waiting for the Server to respond
    System.out.println("Waiting for ComputerServer to respond to ingredient adjustment...");
    while (!this.requestedPacketReceived) ;

    this.requestedPacketReceived = false;
    Packet packet = (Packet) this.object;

    System.out.println("Received " + packet.getItem());

    HashMap newDisplayQuantity = (HashMap) packet.getItem();
    MenuController menuController = (MenuController) controllers.get("menuController");
    menuController.updateRunningQuantity(newDisplayQuantity);
    this.otherUpdate = true;
  }

  /**
   * Sends the ComputerServer an ingredient to have the running quantity adjusted
   * This overloaded method is used for only one ingredient is being adjusted
   * <p>
   *
   * @param ingredient is the ingredient being adjusted
   * @param quantity is the quantity being adjusted from the running quantity (changes in quantity can be negative)
   */
  public void sendAdjustIngredientRequest(DishIngredient ingredient, int quantity) {
    this.otherUpdate = false;
    this.send(Packet.ADJUSTINDIVIDUALINGREDIENT, new Object[]{ingredient, quantity});

    // Waiting for the Server to respond
    System.out.println("Waiting for ComputerServer to respond to ingredient adjustment...");
    while (!this.requestedPacketReceived) ;

    this.requestedPacketReceived = false;
    Packet packet = (Packet) this.object;

    System.out.println("Received " + packet.getItem());

    HashMap newDisplayQuantity = (HashMap) packet.getItem();
    MenuController menuController = (MenuController) controllers.get("menuController");
    menuController.updateRunningQuantity(newDisplayQuantity);
    this.otherUpdate = true;
  }

  /* NOTE: When sendEvent() is called, it sends an Packet with the given protocol (defined in Packet)
   *       The ComputerServer knows who is sending the event because the employeeID is stored in ClientThread in backend
   */

  /**
   * Send an event with the methodName to be added to the EventQueue in the backend
   * This overloaded method is for methods that have multiple parameters
   *
   * @param methodName is the protocol of the method in the event
   * @param parameters is an ArrayList of the parameters used in the method
   */
  public void sendEvent(int methodName, ArrayList parameters) {
    Packet packet = new Packet(methodName, parameters);
    this.send(methodName, parameters);
  }

  /**
   * Send an event with the methodName to be added to the EventQueue in the backend
   * This overloaded method is for methods that only have one parameter
   *
   * @param methodName is the protocol of the method in the event
   * @param parameter  is a single parameter used in the method
   */
  public void sendEvent(int methodName, Object parameter) {
    ArrayList<Object> parameters = new ArrayList<>();
    parameters.add(parameter);
    Packet packet = new Packet(methodName, parameters);
    this.send(methodName, parameters); //TODO packet not used
  }

  /**
   * Send an event with the methodName to be added to the EventQueue in the backend
   * This overloaded method is for events that have no parameters
   *
   * @param methodName is the protocol of the method in the event
   */
  public void sendEvent(int methodName) {
    Packet packet = new Packet(methodName);
    this.send(methodName);
  }

  /**
   * Store an additional controller with the given name
   *
   * @param name       is the name of the controller
   * @param controller is the controller being stored
   */
  public void storeController(String name, Object controller) {
    controllers.put(name, controller);
  }

  /**
   * Getter for the GUI controller with the given name
   *
   * @param name is the name of the controller
   * @return the controller
   */
  public Object getController(String name) {
    return this.controllers.get(name);
  }

  /**
   * Getter to find out whether or not this Client is connected to the ComputerServer
   *
   * @return true if it is connected to ComputerServer, false if not
   */
  public boolean isConnected() {
    return connected;
  }

  /**
   * Shutting the GUI and all the running threads in Client
   * <p>
   * This is called when Client is closing the program or when the Server is shutting down
   */
  private void shutDown() {
//    // Give a shut down warning by freezing everything
    StartSceneController controller = (StartSceneController)controllers.get("startController");
    controller.shut();
    System.out.println("~~~ Shutting down in 3 seconds ~~~");
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // Setting logged in and shut down to be false
    this.connected = false;
    this.loggedIn = false;

    // Closing everything when server shuts down
    try {
      this.input.close();
      this.output.close();
      this.socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Shutting down GUI
    Platform.exit();
  }

  /**
   * This tries to connect the Client to ComputerServer again if the first try failed
   * <p>
   * Connection failures are caused by wrong port or ComputerServer is not even running
   */
  public void connectAgain() {
    if (!this.connected) {
      this.connected = this.connect();
      if (this.connected) {
        Thread t = new Thread(this);
        t.start();
      } else {
        System.err.println("Could not connect to server");
      }
    }
  }
}
