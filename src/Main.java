import de.vandermeer.asciitable.AsciiTable;

public class Main {
  private static final String DB_URL = "jdbc:sqlite:test.db";

  public static void main(String[] args) {

    AsciiTable at = new AsciiTable();
    at.addRule();
    at.addRow("ID", "Site", "Username", "Password", "Notes");
    at.addRule();
    at.addRow("1", "gmail.com", "alice", "pass123", "work email");
    at.addRule();
    at.addRow("2", "twitter.com", "bob", "hunter2", "personal");
    at.addRule();

    System.out.println(at.render());
    PasswordEntry pass = new PasswordEntry("hi2", "a", "b", "c");
    DatabaseUtil db = new DatabaseUtil(DB_URL);

    CliUtil cli = new CliUtil();

    db.connectDB();
    // db.insertEntry(pass);
    db.deleteEntry(1);

    String[][] entries = db.getEntries();
    for (String[] x : entries) {
      for (String i : x) {
        System.out.println(i);
      }
    }
    db.closeDB();
  }
}
