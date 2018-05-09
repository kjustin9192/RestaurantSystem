package backend.employee;

import backend.inventory.Inventory;

/** A manager class. This class represents a manager. */
public class Manager extends Employee {

  /**
   * A constructor. Takes an int type parameter.
   *
   * @param id id of this backend.employee.
   */
  public Manager(int id) {
    super(id);
  }

  /** Prints out the amount of all ingredients left in backend.inventory. */
  public void checkInventory() {
    Inventory inventory = Inventory.getInstance();
    inventory.inventoryToString();
  }

  /**
   * Returns a string representation of this backend.employee.
   *
   * <p>The string representation consists of its employ type (= Manager) and its id.
   *
   * @return a string representation of this backend.employee.
   */
  public String toString() {
    return "Manager, id:" + getId();
  }
}
