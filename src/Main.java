public class Main {
  private static final String DB_URL = "jdbc:sqlite:test.db";

  public static void main(String[] args) {

    PasswordEntry pass = new PasswordEntry("hi2", "a", "b", "c");
    DatabaseUtil db = new DatabaseUtil(DB_URL);
    db.connectDB();
    db.insertEntry(pass);

    String[][] entries = db.getEntries();
    for (String[] x : entries) {
      for (String i : x) {
        System.out.println(i);
      }
    }
    db.closeDB();
  }
}
