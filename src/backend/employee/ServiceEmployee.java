package backend.employee;

/** A ServiceEmployee class. This class is parent class of Cook, ComputerServer class. */
public class ServiceEmployee extends Employee {
  // A order queue that all the cooks and servers share together.
  static OrderQueue orderQueue = new OrderQueue();

  ServiceEmployee(int id) {
    super(id);
  }

  /**
   * Returns the Order Queue
   * @return the Order Queue
   */

  public static OrderQueue getOrderQueue() {
    return orderQueue;
  }
}
