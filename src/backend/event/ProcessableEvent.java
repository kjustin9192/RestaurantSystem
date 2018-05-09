package backend.event;

import backend.employee.*;
import backend.inventory.Inventory;
import backend.logger.RestaurantLogger;
import backend.server.Packet;
import backend.table.Order;
import backend.table.Table;
import backend.table.TableManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import static backend.RestaurantSystem.computerServer;

/**
 * EventProcessor - This takes and backend.event and calls the appropriate method with the
 * parameters
 *
 * <p>Created by Ang Li on Mar. 1st, 2018
 */
public class ProcessableEvent extends Event {

  private static final Logger logger = Logger.getLogger(RestaurantLogger.class.getName());
  private static final Inventory inventory = Inventory.getInstance();

  private LinkedList ordersInQueue = ServiceEmployee.getOrderQueue().getOrdersInQueue();
  private LinkedList dishesInProgress = ServiceEmployee.getOrderQueue().getDishesInProgress();
  private LinkedList dishesCompleted = ServiceEmployee.getOrderQueue().getDishesCompleted();

  /**
   * Constructor for ProcessableEvent
   *
   * @param employeeType is the type of employee
   * @param employeeID is the employee ID
   * @param methodName is the name of the method to call
   * @param parameters are parameters required by the method call
   */
  public ProcessableEvent(int employeeType, int employeeID, int methodName, ArrayList parameters) {
    super(employeeType, employeeID, methodName, parameters);
  }

  /** Find which type of Employee to cast this backend.employee to and call their methods */
  @Override
  void process() {
    System.out.println("An event is being processed");
    Employee employee = EmployeeManager.getEmployeeById(this.employeeID);

    if (this.methodName == Packet.RECEIVEINGREDIENT) {
      String ingredientName = (String) this.parameters.get(0);
      int quantity = (int) this.parameters.get(1);
      if (employee != null) {
        employee.receiveIngredient(ingredientName, quantity);
        System.err.println("Null Pointer Exception: Employee cannot be null");
      }
      HashMap<String, Integer> newRunningQuantities = new HashMap<>();
      newRunningQuantities.put(
          ingredientName, inventory.getIngredient(ingredientName).getRunningQuantity());
      computerServer.broadcast(
          Packet.SERVERTYPE, Packet.RECEIVERUNNINGQUANTITYADJUSTMENT, newRunningQuantities);
    } else {

      switch (this.employeeType) {
        case Packet.COOKTYPE:
          if (employee instanceof Cook) {
            this.processCookEvent((Cook) employee);
          } else {
            logger.warning(
                "*** Employee #" + this.employeeID + " is not a " + this.employeeType + " ***");
          }
          break;
        case Packet.MANAGERTYPE:
          if (employee instanceof Manager) {
            this.processManagerEvent((Manager) employee);
          } else {
            logger.warning(
                "*** Employee #" + this.employeeID + " is not a " + this.employeeType + " ***");
          }
          break;
        case Packet.SERVERTYPE:
          if (employee instanceof Server) {
            this.processServerEvent((Server) employee);
          } else {
            logger.warning(
                "*** Employee #" + this.employeeID + " is not a " + this.employeeType + " ***");
          }
          break;
        default:
          logger.warning("*** " + this.employeeType + " is an invalid Employee type ***");
          break;
      }
    }
  }

  /**
   * Call cook's method based on this backend.event's method in backend.event.txt
   *
   * @param cook is the Cook whose method will be called
   */
  private void processCookEvent(Cook cook) {
    switch (this.methodName) {
      case Packet.ORDERRECEIVED:
        cook.orderReceived();
        computerServer.broadcast(Packet.COOKTYPE, Packet.RECEIVEORDERSINQUEUE, ordersInQueue);
        computerServer.broadcast(Packet.COOKTYPE, Packet.RECEIVEDISHESINPROGRESS, dishesInProgress);
        break;
      case Packet.DISHREADY:
        {
          int dishNumber = (int) this.parameters.get(0);
          cook.dishReady(dishNumber);
          computerServer.broadcast(
              Packet.COOKTYPE, Packet.RECEIVEDISHESINPROGRESS, dishesInProgress);
          computerServer.broadcast(
              Packet.SERVERTYPE, Packet.RECEIVEDISHESCOMPLETED, dishesCompleted);
          break;
        }
      default:
        logger.warning("*** Cook has no " + this.methodName + " method ***");
        break;
    }
  }

  /**
   * Call manager's method based on this backend.event's method in backend.event.txt
   *
   * @param manager is the Manager whose method will be called
   */
  private void processManagerEvent(Manager manager) {
    switch (this.methodName) {
      case Packet.CHECKINVENTORY:
        manager.checkInventory();
        break;
      default:
        logger.warning("*** Manager has no " + this.methodName + " method ***");
        break;
    }
  }

  /**
   * Call server's method based on this backend.event's method in backend.event.txt
   *
   * @param server is the ComputerServer whose method will be called
   */
  private void processServerEvent(Server server) {
    switch (this.methodName) {
      case Packet.TAKESEAT:
        {
          int tableNumber = (int) this.parameters.get(0) - 1;
          int numberOfCustomers = (int) this.parameters.get(1);
          Table table = TableManager.getTable(tableNumber);
          server.takeSeat(table, numberOfCustomers);
          computerServer.broadcast(
              Packet.SERVERTYPE, Packet.RECEIVETABLEOCCUPANCY, TableManager.getTableOccupancy());
          break;
        }
      case Packet.ENTERMENU:
        {
          int tableNumber = (int) this.parameters.get(0) - 1;
          Table table = TableManager.getTable(tableNumber);
          Order order = (Order) this.parameters.get(1);
          server.enterMenu(table, order);
          computerServer.broadcast(Packet.COOKTYPE, Packet.RECEIVEORDERSINQUEUE, ordersInQueue);
          break;
        }
      case Packet.DELIVERDISHCOMPLETED:
        {
          int dishNumber = (int) this.parameters.get(0);
          server.deliverDishCompleted(dishNumber);
          computerServer.broadcast(
              Packet.SERVERTYPE, Packet.RECEIVEDISHESCOMPLETED, dishesCompleted);
          break;
        }
      case Packet.DELIVERDISHFAILED:
        {
          int dishNumber = (int) this.parameters.get(0);
          server.deliverDishFailed(dishNumber);
          computerServer.broadcast(
              Packet.SERVERTYPE, Packet.RECEIVEDISHESCOMPLETED, dishesCompleted);
          break;
        }
      case Packet.PRINTBILL:
        {
          int tableNumber = (int) this.parameters.get(0) - 1;
          Table table = TableManager.getTable(tableNumber);
          server.printBill(table);
          break;
        }
      case Packet.CLEARTABLE:
        {
          int tableNumber = (int) this.parameters.get(0) - 1;
          Table table = TableManager.getTable(tableNumber);
          server.clearTable(table);
          computerServer.broadcast(
              Packet.SERVERTYPE, Packet.RECEIVETABLEOCCUPANCY, TableManager.getTableOccupancy());
          break;
        }
      default:
        logger.warning("*** ComputerServer has no " + this.methodName + " method ***");
        break;
    }
  }
}
