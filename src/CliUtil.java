import de.vandermeer.asciitable.AsciiTable;

public class CliUtil {

  /*
   * Handles output / formatting
   *
   */

  public void displayEntries(String[][] entries) {

    AsciiTable at = new AsciiTable();
    at.addRule();
    at.addRow("ID", "Site", "Username", "Password", "Notes");

    for (String[] x : entries) {

      at.addRule();
      at.addRow(x[0], x[1], x[2], "*****", x[4]);
    }

    at.addRule();

    System.out.println(at.render());
  }
}
