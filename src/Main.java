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

    String username, password;
    System.out.print("Username: ");
    username = scanner.nextLine();
    System.out.print("Password: ");
    password = scanner.nextLine();

    System.out.printf("%s %s %d", username, password, user_choice);
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

    db.closeDB();
  }
}
