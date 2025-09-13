import de.vandermeer.asciitable.AsciiTable;
import java.io.Console;
import java.util.Scanner;

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

  public PasswordEntry handleAddEntry() {
    Console console = System.console();

    if (console == null) {
      System.out.println("No console available. Run from terminal, not IDE.");
      System.exit(1);
    }

    Scanner scanner = new Scanner(System.in);
    String site, username, password, notes;
    System.out.printf("Enter site: ");
    site = scanner.nextLine();
    System.out.printf("Enter username: ");
    username = scanner.nextLine();
    // securly get password (hidden)
    char[] passwordChars = console.readPassword("Enter password: ");
    password = new String(passwordChars);
    System.out.printf("Enter notes (optional): ");
    notes = scanner.nextLine();

    System.out.println("Entry added successfully");
    PasswordEntry password_entry = new PasswordEntry(username, password, site, notes);
    return password_entry;
  }
}
