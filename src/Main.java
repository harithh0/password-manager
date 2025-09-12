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
      db.handleLogin(username, password);
    } else if (user_choice == 1) {
      db.handleSignup(username, password);
    }

    /*
     * Workflow
     * Signup -> salt generated + added to password = key generated
     * Encrypt password and save it to users db along with username and salt
     *
     * Login -> username provided hash is looked up added to given password = key
     * generatd if key generated can decrypt that users password then allow if not
     * then wrong
     * password
     *
     *
     *
     */

    // String[][] entries = db.getEntries();
    // cli.displayEntries(entries);

    db.closeDB();
  }
}
