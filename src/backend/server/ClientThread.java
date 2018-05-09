package backend.server;

import backend.employee.ServiceEmployee;
import backend.event.EventManager;
import backend.event.ProcessableEvent;
import backend.inventory.DishIngredient;
import backend.inventory.Inventory;
import backend.inventory.InventoryIngredient;
import backend.inventory.Menu;
import backend.logger.ComputerServerLogger;
import backend.table.TableManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import static backend.inventory.Inventory.getInstance;

/**
 * ClientThread.java Created by Bill Ang Li
 *
 * <p>This class is created for the ComputerServer to connect and communicate with individual
 * clients
 */
class ClientThread implements Runnable {
  // Variables for client-server communication
  private Socket socket;
  private boolean connected;
  private ObjectInputStream input;
  private ObjectOutputStream output;
  private boolean loggedOn = false;
  private int employeeID = -1; // Default value
  private int employeeType = -1; // Default value

  // Logger
  private static Logger logger = Logger.getLogger(ComputerServerLogger.class.getName());

  // Getting the instances from singleton classes
  private Inventory inventory = Inventory.getInstance();
  private ComputerServer computerServer = ComputerServer.getInstance();

  /**
   * Constructor for Client Thread
   *
   * @param socket is the socket connected to the Client
   */
  ClientThread(Socket socket) throws IOException {
    // Setting up resources for client-server communication
    this.socket = socket;
    this.connected = true;
    this.output = new ObjectOutputStream(socket.getOutputStream());
    this.input = new ObjectInputStream(socket.getInputStream());

    // Starting a new thread to listen for input from clients
    Thread thread = new Thread(this);
    thread.start();
  }

  /**
   * This method is run when a new thread of ClientThread is created It is responsible for
   * constantly listening for new input updates from the client
   */
  @Override
  public void run() {
    // Only listen when this client thread is connected to the client, otherwise don't be in loop
    while (this.connected) {
      try {
        Object object = this.input.readObject();

        if (object != null) {
          // Received a packet from client
          Packet packet = (Packet) object;
          logger.info("Received packet type " + packet.getType());

          if (packet.isEventType()) {
            // If the packet contains information to make an event, create an event and add it to
            // the queue
            EventManager.addEvent(createEvent(packet));
          } else {
            switch (packet.getType()) {
              case Packet.LOGINREQUEST:
                // Log in sendRequest
                int id = (Integer) packet.getItem();
                int logInConfirmation = computerServer.logIn(id);
                if (logInConfirmation != Packet.LOGINFAILED) {
                  this.loggedOn = true;
                  this.employeeID = id;
                  this.employeeType = logInConfirmation;
                }
                this.send(Packet.LOGINCONFIRMATION, logInConfirmation);
                break;
              case Packet.LOGOFF:
                // Client is logging off, client can still log back on
                logger.info(
                    "Employee type "
                        + this.getEmployeeType()
                        + "employee "
                        + this.getEmployeeID()
                        + " is logging off");
                this.logOff();
                break;
              case Packet.DISCONNECT:
                // Client instance is disconnecting the connection, should remove this clientThread
                // from clients in ComputerServer
                logger.info(
                    "Employee type "
                        + this.getEmployeeType()
                        + " employee "
                        + this.getEmployeeID()
                        + " is logging off");
                logger.info("Disconnecting socket");
                this.logOff();
                this.shutDown();
                this.connected = false;
                computerServer.removeClientThread(this.employeeID);
                break;
              case Packet.REQUESTNUMBEROFTABLES:
                // Client is requesting the number of tables
                logger.info("Sending number of tables");
                this.send(Packet.RECEIVENUMBEROFTABLES, TableManager.getNumberOfTables());
                break;
              case Packet.REQUESTMENU:
                // Client is requesting Menu's dishes
                logger.info("Sending menu");
                Menu menu = Menu.getMenu();
                this.send(Packet.RECEIVEMENU, menu.getDishes());
                break;
              case Packet.REQUESTINVENTORY:
                {
                  // Client is requesting Inventory
                  logger.info("Sending inventory");
                  Inventory inventory = getInstance();
                  this.send(Packet.RECEIVEINVENTORY, inventory.getIngredientsInventory());
                  break;
                }
              case Packet.REQUESTTABLEOCCUPANCY:
                // Client is requesting an ArrayList<boolean> of table occupancy
                logger.info("Sending table occupancy");
                this.send(Packet.RECEIVETABLEOCCUPANCY, TableManager.getTableOccupancy());
                break;
              case Packet.REQUESTREQUEST:
                // Client is requesting which ingredients are low in stock and need to be requested
                logger.info("Sending request");
                this.send(Packet.RECEIVEREQUEST, inventory.getRequests());
                break;
              case Packet.REQUESTDISHESINPROGRESS:
                // Client is requesting dishesInProgress queue
                logger.info("Sending dishesInProgress");
                this.send(
                    Packet.RECEIVEDISHESINPROGRESS,
                    ServiceEmployee.getOrderQueue().getDishesInProgress());
                break;
              case Packet.REQUESTORDERSINQUEUE:
                // Client is requesting ordersInQueue
                logger.info("Sending ordersInQueue");
                this.send(
                    Packet.RECEIVEORDERSINQUEUE,
                    ServiceEmployee.getOrderQueue().getOrdersInQueue());
                break;
              case Packet.REQUESTTABLE:
                // Client is requesting the Table with the given table index
                logger.info("Sending table");
                this.send(Packet.RECEIVETABLE, TableManager.getTable((int) packet.getItem()));
                break;
              case Packet.REQUESTDISHESCOMPLETED:
                // Client is requesting dishesCompleted
                logger.info("Sending dishes completed");
                this.send(
                    Packet.RECEIVEDISHESCOMPLETED,
                    ServiceEmployee.getOrderQueue().getDishesCompleted());
                break;
              case Packet.REQUESTQUANTITIES:
                // Client is requesting ingredient quantities
                logger.info("Sending ingredient quantities");
                HashMap<String, InventoryIngredient> inventoryIngredients =
                    inventory.getIngredientsInventory();
                HashMap<String, Integer> quantities = new HashMap<>();
                for (String ingredientName : inventoryIngredients.keySet()) {
                  quantities.put(
                      ingredientName, inventoryIngredients.get(ingredientName).getQuantity());
                }
                this.send(Packet.RECEIVEQUANTITIES, quantities);
                break;
              case Packet.REQUESTBILL:
                // Client is requesting bill
                int tableIndex = (int) packet.getItem();
                logger.info("Sending all dishes delivered to table index " + tableIndex);
                this.send(
                    Packet.RECEIVEBILL,
                    TableManager.getTable(tableIndex)
                        .getAllDeliveredDishes()); // TODO: Changed from getDishes
                break;
              case Packet.ADJUSTINGREDIENT:
                {
                  // Client is adjusting ingredient
                  logger.info("Adjusting ingredient");
                  Object[] infoArray = (Object[]) packet.getItem();
                  ArrayList<DishIngredient> dishIngredients =
                      (ArrayList<DishIngredient>) infoArray[0];
                  boolean decrease = (Boolean) infoArray[1];
                  Inventory inventory = Inventory.getInstance();
                  HashMap newIngredientQuantities =
                      inventory.modifyIngredientRunningQuantity(dishIngredients, decrease);
                  computerServer.broadcast(
                      Packet.RECEIVERUNNINGQUANTITYADJUSTMENT, newIngredientQuantities);
                  break;
                }
              case Packet.ADJUSTINDIVIDUALINGREDIENT:
                {
                  // Client is sending an individual ingredient to be adjusted
                  logger.info("Adjusting individual ingredient");
                  Object[] infoArray = (Object[]) packet.getItem();
                  DishIngredient ingredient = (DishIngredient) infoArray[0];
                  int quantity = (int) infoArray[1];
                  HashMap newIngredientQuantities =
                      inventory.modifyIngredientRunningQuantity(ingredient.getName(), quantity);
                  computerServer.broadcast(
                      Packet.SERVERTYPE,
                      Packet.RECEIVERUNNINGQUANTITYADJUSTMENT,
                      newIngredientQuantities);
                  break;
                }
            }
          }
        }
      } catch (IOException e) {
        logger.warning("Shutting down the ComputerServer");
        this.connected = false;
      } catch (Exception e) {
        logger.warning("Socket closed");
      }
    }

    // Closing the resources when client thread is shutting down
    logger.warning("This client thread is closing");
    try {
      this.input.close();
      this.output.close();
      this.socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Send an object to the client
   *
   * @param type is the type of the message
   * @param object is what is being sent
   */
  void send(int type, Object object) {
    //    System.out.println("Sending " + object.getClass() + ": \"" + object + "\" to employee type
    // " + this.employeeType + " employee " + this.employeeID);

    Packet packet = new Packet(type, object);
    try {
      this.output.reset();
      this.output.writeObject(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Send just the message type to the client
   *
   * @param type is the type of the message
   */
  void send(int type) {
    //    System.out.println("Sending " + type + " to employee type" + this.employeeType + "
    // employee " + this.employeeID);

    Packet packet = new Packet(type);
    try {
      this.output.writeObject(packet);
      this.output.reset();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Create an Event to put into the system
   *
   * @param packet is the packet received from the Client
   * @return the ProcessableEvent created from the information received
   */
  private ProcessableEvent createEvent(Packet packet) {
    int methodName = packet.getType();
    ArrayList parameters = (ArrayList) packet.getItem();
    return new ProcessableEvent(this.employeeType, this.employeeID, methodName, parameters);
  }

  /** Resetting everything to before client logged in */
  private void logOff() {
    this.loggedOn = false;
    this.employeeID = -1; // Default value
    this.employeeType = -1; // Default value
  }

  /** ComputerServer can call this method to shut down the client thread */
  private void shutDown() {
    // Pausing a little before shutting down
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    try {
      this.input.close();
      this.output.close();
      this.socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.connected = false;
    this.logOff();
  }

  /**
   * Getter for isLoggedOn
   *
   * @return if this user has logged on
   */
  boolean isLoggedOn() {
    return loggedOn;
  }

  /**
   * Getter for employeeID
   *
   * @return the employee's ID
   */
  public int getEmployeeID() {
    return employeeID;
  }

  /**
   * Getter for employeeType 100: Cook 101: Manager 102: Server Outlined in Packet.java
   *
   * @return an integer representation of the employee type
   */
  public int getEmployeeType() {
    return employeeType;
  }
}
