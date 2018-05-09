package backend.logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/** A Logger that logs all the information of things done in backend. */
public class RestaurantLogger {
  // Logger.getLogger() follows Factory Pattern.
  private static final Logger logger = Logger.getLogger(RestaurantLogger.class.getName());

  private static FileHandler fileHandler = null;

  /** Initialize this logger. This should be called before logger is used. */
  public static void init() {
    // Disables default root logger's console handler.
    logger.setUseParentHandlers(false);

    try {
      fileHandler = new FileHandler("logFile.log", true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // If fileHandler format is not set to SimpleFormat, then the log is kept in XML format.
    // SimpleFormat makes the log to be kept in text format.
    fileHandler.setFormatter(new SimpleFormatter());

    //    Logger rootLogger = Logger.getLogger("");
    //    rootLogger.addHandler(fileHandler);
    logger.addHandler(fileHandler);
  }
}
