import java.util.Scanner;

// for copying to clipboard
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

// for Ansi Escape coloring
import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.Color.*;

import java.io.Console;

public class Main {
  private static final String DB_URL = "jdbc:sqlite:test.db";
  private static DatabaseUtil db = new DatabaseUtil(DB_URL);
  private static Scanner scanner = new Scanner(System.in);

  private static void cleanUp(boolean error) {
    db.closeDB();
    scanner.close();
    if (error) {
      System.exit(1);
    } else {
      System.exit(0);
    }
  }

  public static void main(String[] args) {

    CliUtil cli = new CliUtil();

    db.connectDB();

    System.out.println("Welcome to password manager");
    System.out.print("Login (0) or Signup (1): ");
    int user_choice = scanner.nextInt();
    scanner.nextLine();

    // WARN: CHANGE
    // String username = "harith";
    // String password = "123";
    String username, password;
    System.out.print("Username: ");
    username = scanner.nextLine();

    Console console = System.console();
    char[] passwordChars = console.readPassword("Password: ");
    password = new String(passwordChars);

    if (user_choice == 0) {
      int result = db.handleLogin(username, password);
      if (result != 0) {
        cleanUp(true);
      }
    } else if (user_choice == 1) {
      int result = db.handleSignup(username, password);
      if (result == 1) {
        System.out.println(Ansi.ansi().fg(RED).a("[-] Username is already taken").reset());
        cleanUp(true);
      } else if (result == 2) {
        System.out.println(Ansi.ansi().fg(RED).a("[-] Something went wrong").reset());
        cleanUp(true);
      }

    }

    int user_action_choice;
    boolean running = true;

    String[][] entries;
    while (running) {

      System.out.printf(
          "\u001B[33m" + // start yellow
              "Choices: \n" +
              "0) View entries\n" +
              "1) Add entry\n" +
              "2) Delete entry\n" +
              "3) Show password for entry\n" +
              "4) Copy password of entry to clipboard\n" +
              "5) Exit\n" +
              "Enter choice: " +
              "\u001B[0m" // reset color back to default
      );
      user_action_choice = scanner.nextInt();
      scanner.nextLine(); // consume the leftover newline

      // updates each loop
      entries = db.getEntries();

      switch (user_action_choice) {
        case 0 -> {
          if (entries == null) {
            System.out.println(Ansi.ansi().fg(RED).a("~~ No entries found for your account ~~").reset());
          } else {
            cli.displayEntries(entries);
          }
        }
        case 1 -> {
          PasswordEntry entry_data = cli.handleAddEntry();
          db.insertEntry(entry_data);
          System.out.println(Ansi.ansi().fg(BLUE).a("[+] Entry added successfully").reset());
        }
        case 2 -> {
          if (entries == null) {
            System.out.println(Ansi.ansi().fg(RED).a("~~ No entries found for your account ~~").reset());
            break;
          }
          String[] chosen_entry = cli.handleGetSpecificEntry(entries);
          if (chosen_entry != null) {
            db.deleteEntry(chosen_entry[0]);
            System.out.println(Ansi.ansi().fg(BLUE).a("[+] Entry deleted!").reset());
          } else {
            System.out.println(Ansi.ansi().fg(RED).a("[-] Entry does not exist").reset());

          }
        }
        case 3 -> {

          if (entries == null) {
            System.out.println(Ansi.ansi().fg(RED).a("~~ No entries found for your account ~~").reset());
            break;
          }
          String[] chosen_entry = cli.handleGetSpecificEntry(entries);
          if (chosen_entry != null) {
            cli.displayOneEntry(chosen_entry);
          } else {
            System.out.println(Ansi.ansi().fg(RED).a("[-] Entry does not exist").reset());
          }
        }
        case 4 -> {

          if (entries == null) {
            System.out.println(Ansi.ansi().fg(RED).a("~~ No entries found for your account ~~").reset());
            break;
          }
          String[] chosen_entry = cli.handleGetSpecificEntry(entries);
          if (chosen_entry != null) {

            // clipboard impl
            StringSelection selection = new StringSelection(chosen_entry[3]);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);

            System.out.println(Ansi.ansi().fg(BLUE).a("[+] Copied to clipboard!").reset());
          } else {
            System.out.println(Ansi.ansi().fg(RED).a("[-] Entry does not exist").reset());
          }

        }
        case 5 -> {
          running = false;
          break; // breaks from switch
        }
        default -> System.out.println("Wrong choice buck");
      }
    }

    // Do cleanup here
    cleanUp(false);

    /*
     * Workflow
     *
     * - Generate key from password + salt
     * - Hash that password + salt for the users table
     * - Use that key to encrypt columns
     *
     * Login
     * - Look at provided users salt, add it to the provided password and hash it,
     * if it matches stored password hash for user then password is correct
     * - Generate key from password + salt and decrypt columns
     *
     *
     */

  }
}
