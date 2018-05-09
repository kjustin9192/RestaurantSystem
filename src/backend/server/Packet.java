package backend.server;

import java.io.Serializable;

/**
 * Packet.java
 * Created by Ang Li
 * <p>
 * Class for Packets being sent between client and server
 * <p>
 * A packet is a serializable item that contains a type which is a protocol for what is being sent
 * It also contains an item which is what is being sent (e.g. a LinkedList of Orders, or null if just the protocol is needed)
 * <p>
 * Since packets contain arbitrary items, we decided to treat it as an Object and cast it when used
 * We did not choose to use generics because we are calling the send method directly instead of creating a Packet and then send it
 */
public class Packet implements Serializable {
  // Special protocols
  public static final int LOGOFF = 0;
  public static final int DISCONNECT = 1000;
  public static final int SERVERSHUTDOWN = 2000;

  // Client to Server resource requests
  public static final int LOGINREQUEST = 1;
  public static final int REQUESTNUMBEROFTABLES = 2;
  public static final int REQUESTMENU = 3;
  public static final int REQUESTINVENTORY = 4;
  public static final int REQUESTDISHESINPROGRESS = 5;
  public static final int REQUESTORDERSINQUEUE = 6;
  public static final int REQUESTTABLE = 7;
  public static final int REQUESTQUANTITIES = 8;
  public static final int REQUESTDISHESCOMPLETED = 9;
  public static final int REQUESTTABLEOCCUPANCY = 10;
  public static final int REQUESTREQUEST = 11;
  public static final int REQUESTBILL = 12;

  // Server to Client println resources
  public static final int LOGINCONFIRMATION = -1;
  public static final int RECEIVENUMBEROFTABLES = -2;
  public static final int RECEIVEMENU = -3;
  public static final int RECEIVEINVENTORY = -4;
  public static final int RECEIVEDISHESINPROGRESS = -5;
  public static final int RECEIVEORDERSINQUEUE = -6;
  public static final int RECEIVETABLE = -7;
  public static final int RECEIVEQUANTITIES = -8;
  public static final int RECEIVEDISHESCOMPLETED = -9;
  public static final int RECEIVETABLEOCCUPANCY = -10;
  public static final int RECEIVEREQUEST = -11;
  public static final int RECEIVEBILL = -12;

  // Adjust ingredient
  public static final int ADJUSTINGREDIENT = 30;
  public static final int ADJUSTINDIVIDUALINGREDIENT = 31;
  public static final int RECEIVERUNNINGQUANTITYADJUSTMENT = -30;

  // Event Type
  public static final int RECEIVEINGREDIENT = 55;

  // Cook Events
  public static final int ORDERRECEIVED = 50;
  public static final int DISHREADY = 51;

  // Manager Events
  public static final int CHECKINVENTORY = 60;

  // Server Events
  public static final int TAKESEAT = 70;
  public static final int ENTERMENU = 71;
  public static final int DELIVERDISHCOMPLETED = 72;
  public static final int DELIVERDISHFAILED = 73;
  public static final int PRINTBILL = 74;
  public static final int CLEARTABLE = 75;

  // Employee Type
  public static final int LOGINFAILED = -100;
  public static final int COOKTYPE = 100;
  public static final int MANAGERTYPE = 101;
  public static final int SERVERTYPE = 102;

  // Contents of this packet
  private int type;
  private Object item;

  /**
   * Packet constructor
   *
   * @param type is the type of the item
   * @param item is the information being sent
   */
  public Packet(int type, Object item) {
    this.type = type;
    this.item = item;
  }

  /**
   * Packet constructor without item
   *
   * @param type is the type of the item
   */
  public Packet(int type) {
    this.type = type;
  }

  /**
   * Getter for the type of the Packet
   *
   * @return the type of this Packet
   */
  public int getType() {
    return type;
  }

  /**
   * Checks if this Packet is an event type
   * Event type means that this Packet is an event that is supposed to be created in the backend
   *
   * @return true if this Packet is an event type otherwise false
   */
  public boolean isEventType() {
    return (this.type >= 50) && (this.type < 80);
  }

  /**
   * Checks if this Packet is a receive type
   * Receive types are used when Client is supposed to receive a resource from the client
   *
   * @return true if this Packet is a receive type, otherwise false
   */
  public boolean isReceiveType() {
    return (this.type <= -1) && (this.type >= -12);
  }

  /**
   * Checks if this Packet is an update type
   * An update type is also a receive type, but this Packet can be sent to the client without the client requesting it
   * It is used when other clients or the ComputerServer changed a variable in the backend that everyone should see
   * For example, when one Server puts down the request for fries and potatoes run out in running quantity, all Servers
   * should be able to see that they can't order fries anymore
   *
   * @return true if this Packet is an update type, false otherwise
   */
  public boolean isUpdateType() {
    return (this.type == RECEIVEDISHESINPROGRESS) ||
            (this.type == RECEIVEORDERSINQUEUE) ||
            (this.type == RECEIVEDISHESCOMPLETED) ||
            (this.type == RECEIVERUNNINGQUANTITYADJUSTMENT) ||
            (this.type == RECEIVETABLEOCCUPANCY) ||
            (this.type == SERVERSHUTDOWN);
  }

  /**
   * Getter for the item in this packet
   *
   * @return the item in this packet
   */
  public Object getItem() {
    return item;
  }
}
