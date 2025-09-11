public class Main {
  private static final String DB_URL = "jdbc:sqlite:test.db";

  public static void main(String[] args) {

    PasswordEntry pass = new PasswordEntry("hi", "a", "b", "c");
    DatabaseUtil db = new DatabaseUtil(DB_URL);
    db.connectDB();
    db.closeDB();
  }
}
