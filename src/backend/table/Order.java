package backend.table;

import backend.inventory.Dish;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Order class represents orders made by the customers sitting on a backend.table.
 *
 * <p>Order class methods include assigning a Dish to a Table,
 */
public class Order implements Serializable {
  private ArrayList<Dish> dishes;
  private Table table;

  /** Constructor for the order with an empty ArrayList of Dishes */
  public Order() {
    dishes = new ArrayList<>();
  }

  /**
   * adds this dish to this Order
   *
   * @param d a dish
   */
  public void addDish(Dish d) {
    dishes.add(d);
  }

  /**
   * returns all dishes in this order
   *
   * @return all dishes in this order
   */
  public String toString() {
    ArrayList<String> result = new ArrayList<>();

    int i = 0;
    for (; i < dishes.size(); i++) {
      if (dishes.get(i).hasBeenDelivered()) {
        result.add(dishes.get(i).toString());
      }
    }

    return String.join("\n", result);
  }

  /**
   * returns the backend.table number that the order is belong to
   *
   * @return the backend.table number
   */
  public int getTableNum() {
    return table.getTableNum();
  }

  /**
   * assigns the order to this backend.table and assigns all dishes in order to this backend.table
   *
   * @param table the backend.table that made the order
   */
  public void assignDishToTable(Table table) {
    this.table = table;
    for (Dish dish : dishes) {
      dish.assignDishToTable(table);
    }
  }

  /**
   * returns the name and price of all the dishes in this order
   *
   * @return all dish name with its price
   */
  public String dishesToString() {
    ArrayList<String> result = new ArrayList<>();
    for (Dish dish : dishes) {
      result.add(dish.getName() + "(Dish #: " + dish.getDishNumber() + ")");
    }
    return String.join(", ", result);
  }

  /**
   * returns an ArrayList of dishes
   *
   * @return dishes in an ArrayList
   */
  public ArrayList<Dish> getDishes() {
    return dishes;
  }

  /** returns an id of each dish in this order */
  public void assignDishNumber() {
    for (Dish d : dishes) {
      d.assignDishNumber();
    }
  }

  public void remove(Dish dish){
    dishes.remove(dish);
  }

  public boolean isEmpty() {
      return dishes.isEmpty();
  }
}
