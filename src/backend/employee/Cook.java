package backend.employee;

/** A cook class. This class represents the cook. */
public class Cook extends ServiceEmployee {

  /**
   * A constructor. Takes an int type parameter.
   *
   * @param id id of this backend.employee.
   */
  public Cook(int id) {
    super(id);
  }

  /**
   * Cook confirms whether the order has been seen.
   *
   * <p>If there are multiple orders in the queue, to be seen by the cook, when any cook confirms
   * that he/she has seen the order, ALL the orders in queue are confirmed that they are seen.
   */
  public void orderReceived() {
    // logging is done in orderQueue.confirmFirstOrderInQueue() method.
    orderQueue.confirmFirstOrderInQueue(getId());
  }

  /**
   * Cook confirms whether the food is ready to be delivered by the server.
   *
   * @param dishNumber the number of dish that is cooked and ready to be delivered.
   */
  public void dishReady(int dishNumber) {
    // the log is in orderQueue.dishCompleted() method.
    orderQueue.dishCompleted(dishNumber, getId());
  }

  /**
   * Returns a string representation of this backend.employee.
   *
   * <p>The string representation consists of its employ type (= Cook) and its id.
   *
   * @return a string representation of this backend.employee.
   */
  public String toString() {
    return "Cook, id:" + getId();
  }
}
