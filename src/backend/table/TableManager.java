package backend.table;

import java.util.ArrayList;

/**
 * Table manager manages all the tables.
 *
 * Table manager methods include setting up the backend.table, and returning a backend.table by its id.
 */
public class TableManager {
  private static Table[] tables;

  /**
   * sets up the manager with numOfTables tables
   * @param numOfTables the number of tables
   */
  public static void tableSetUp(int numOfTables) {
    tables = new Table[numOfTables];
    // Note that backend.table number starts from 1.
    for (int i = 0; i < numOfTables; i++) {
      tables[i] = new Table(i+1);
    }
  }

  /**
   * returns the backend.table with id i
   * @param i the id of a backend.table
   * @return the backend.table with the id i
   */
  public static Table getTable(int i) {
    return tables[i];
  }

  /**
   * Getter for number of tables
   *
   * @return the number of tables
   */
  public static int getNumberOfTables() {
    return tables.length;
  }

  public static ArrayList<Boolean> getTableOccupancy() {
    ArrayList<Boolean> tableOccupancy = new ArrayList<>();
    for (Table table : tables) {
      tableOccupancy.add(table.isOccupied());
    }
    return tableOccupancy;
  }
}
