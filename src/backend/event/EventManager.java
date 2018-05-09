package backend.event;

import java.util.LinkedList;
import java.util.Queue;

/**
 * EventManager - This class manages all the functionality of events
 *
 * <p>Created by Ang Li on Feb. 22nd, 2018
 */
public class EventManager implements Runnable {
  private static final String FILE = "src/backend/event/event.txt";
  private static boolean isRunning = false;
  private static volatile Queue<Event> eventQueue =
      new LinkedList<>(); // TODO: Some how get rid of volatile

  //  /**
  //   * Read the specified file in this class and add the events to the eventQueue
  //   *
  //   * @throws IOException because it reads from a text file
  //   */
  //  public void readFile() throws IOException {
  //    EventReader eventReader = new EventReader(FILE);
  //    eventReader.readFile(eventQueue);
  //  }

  /**
   * Setter for isRunning
   *
   * @param running is whether this is running or not
   */
  public static void setRunning(boolean running) {
    isRunning = running;
  }

  /**
   * Add an event to the eventQueue
   *
   * @param event is the event to be added
   */
  public static void addEvent(Event event) {
    System.out.println("Adding event");
    eventQueue.add(event);
  }

  /** Processes the events from eventQueue as long as if there are more events */
  @Override
  public void run() {
    while (isRunning) {
      if (!eventQueue.isEmpty()) {
        System.out.println("Processing an event");
        Event event = eventQueue.remove();
        event.process();
      }
    }

    System.out.println("Closing the EventManager"); // TODO: Terminate method
  }
}
