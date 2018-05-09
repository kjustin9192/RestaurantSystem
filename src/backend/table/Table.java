package backend.table;

import backend.employee.Server;
import backend.inventory.Dish;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Table class represents each backend.table in the restaurant.
 *
 * <p>Table class methods include storing the customer's orders, adding orders, adding price of the
 * order.
 */
public class Table implements Serializable {
  private int tableNum;
  private int numOfCustomer;
  private float cost;
  private boolean occupied;
  private Server server;
  private ArrayList<Order> order;

  /**
   * initializes the backend.table with its number and price of 0
   *
   * @param tableNum the backend.table number
   */
  public Table(int tableNum) {
    this.tableNum = tableNum;
    this.occupied = false;
    order = new ArrayList<>();
    cost = 0;
    numOfCustomer = 0;
  }

  /** clears the backend.table for new customer no server and empty order */
  public void clear() {
    server = null;
    cost = 0;
    order = new ArrayList<>();
    this.occupied = false;
  }

  /**
   * serves this backend.table
   *
   * @param numOfCustomer number of customers sitting on this table
   * @param server the server that serving this backend.table
   */
  public void serve(Server server, int numOfCustomer) {
    this.server = server;
    this.numOfCustomer = numOfCustomer;
    this.occupied = true;
  }

  /**
   * adds the dish price to backend.table's price
   *
   * @param d a dish object
   */
  public void addCost(Dish d) {
    cost += d.getPrice();
  }

  /**
   * adds this order to this backend.table also add backend.table to this order
   *
   * @param newOrder the order that made by the customer
   */
  public void addOrder(Order newOrder) {
    order.add(newOrder);
    newOrder.assignDishToTable(this);
  }

  /**
   * returns the backend.table number
   *
   * @return backend.table number
   */
  public int getTableNum() {
    return tableNum;
  }

  /**
   * Returns list of all dishes in the order.
   *
   * @return list of all dishes in the order.
   */
  public ArrayList<Dish> printBill() {
    // TODO: revert back. printBill is a bad name to return arrayList.
    ArrayList<Dish> dishes = new ArrayList<>();
    for (Order order : this.order) {
      dishes.addAll(order.getDishes());
    }
    return dishes;
  }

  /**
   * Returns list of all dishes in the order.
   *
   * @return list of all dishes in the order.
   */
  public ArrayList<Dish> getAllDeliveredDishes() {
    ArrayList<Dish> dishes = new ArrayList<>();
    for (Order order : this.order) {
      for (Dish d : order.getDishes()) {
        if (d.hasBeenDelivered()) {
          dishes.add(d);
        }
      }
    }
    return dishes;
  }

  /**
   * returns the server id if the server is not null
   *
   * @return the server id, -1 if no server
   */
  public int getServerId() {
    return (server == null) ? -1 : server.getId();
  }

  public int getNumOfCustomer() {
    return numOfCustomer;
  }

  /**
   * Returns true if table is occupied, false if not
   *
   * @return the boolean
   */
  public boolean isOccupied() {
    return occupied;
  }
}
