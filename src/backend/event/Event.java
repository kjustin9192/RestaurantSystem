package backend.event;

import java.util.ArrayList;

/**
 * Event.java - An abstract class to represent an backend.event
 *
 * <p>Created by Bill Ang Li on Feb. 22nd, 2018
 */
abstract class Event {
  int employeeType;
  int employeeID;
  int methodName;
  ArrayList parameters = new ArrayList<>();

  /**
   * Constructor for Event
   *
   * @param employeeType is the type of employee
   * @param employeeID is the employee ID
   * @param methodName is the name of the method to call
   * @param parameters are parameters required by the method call
   */
  public Event(int employeeType, int employeeID, int methodName, ArrayList parameters) {
    this.employeeType = employeeType;
    this.employeeID = employeeID;
    this.methodName = methodName;
    this.parameters = parameters;
  }

  /** Tells the ProcessableEvent to process the backend.event */
  abstract void process();
}
