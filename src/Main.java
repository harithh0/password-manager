import java.util.Scanner;

public class Main {
  private static final String DB_URL = "jdbc:sqlite:test.db";

  public static void main(String[] args) {

    PasswordEntry pass = new PasswordEntry("hi2", "a", "b", "c");
    DatabaseUtil db = new DatabaseUtil(DB_URL);
    Scanner scanner = new Scanner(System.in);

    CliUtil cli = new CliUtil();

    db.connectDB();

    System.out.println("Welcome to password manager");
    System.out.print("Login (0) or Signup (1): ");
    int user_choice = scanner.nextInt();
    scanner.nextLine();

    // WARN: CHANGE
    String username = "harith";
    String password = "123";
    // System.out.print("Username: ");
    // username = scanner.nextLine();
    // System.out.print("Password: ");
    // password = scanner.nextLine();

    System.out.printf("DEBUG: %s %s %d\n", username, password, user_choice);
    if (user_choice == 0) {
      int result = db.handleLogin(username, password);
      if (result != 0) {
        db.closeDB();
        // TODO: should do clean function
        System.exit(1);
      }
    } else if (user_choice == 1) {
      db.handleSignup(username, password);
    }

    int user_action_choice;
    boolean running = true;
    while (running) {
      System.out.printf(
          "Choices: \n"
              + "0) View entries\n"
              + "1) Add entry\n"
              + "2) Delete entry\n"
              + "3) Show password for entry\n"
              + "4) Copy password of entry to clipboard\n"
              + "5) Exit\n"
              + "Enter choice: ");

      user_action_choice = scanner.nextInt();
      scanner.nextLine(); // consume the leftover newline
      String[][] entries;
      switch (user_action_choice) {
        case 0 -> {
          entries = db.getEntries();
          if (entries == null) {
            System.out.println("~~ No entries found for your account ~~");
          } else {
            cli.displayEntries(entries);
          }
        }
        case 1 -> {
          PasswordEntry entry_data = cli.handleAddEntry();
          db.insertEntry(entry_data);
        }
        case 2 -> {}
        case 3 -> {
          int choice;
          System.out.print("Enter entry ID: ");
          choice = scanner.nextInt();
          System.out.println(Integer.toString(choice));
          entries = db.getEntries();
          for (String[] l : entries) {
            if (l[0].equals(Integer.toString(choice))) {
              System.out.println("Password: " + l[3]);
            }
          }
        }
        case 5 -> {
          running = false;
          break; // breaks from switch
        }
        default -> System.out.println("Wrong choice buck");
      }
    }

    db.closeDB();
    scanner.close();

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

    // String[][] entries = db.getEntries();
    // cli.displayEntries(entries);

  }
}
