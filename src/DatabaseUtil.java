import java.sql.*;

public class DatabaseUtil {

  private String DB_URL;
  private Connection connection;

  DatabaseUtil(String db_url) {
    this.DB_URL = db_url;
  }

  public int connectDB() {
    try {
      // Load SQLite JDBC driver
      Class.forName("org.sqlite.JDBC");

      // Connect (creates DB file if it doesn't exist)
      this.connection = DriverManager.getConnection(DB_URL);

      String createTable =
          """
          CREATE TABLE IF NOT EXISTS password_entries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            site TEXT NOT NULL,
            username TEXT NOT NULL,
            password TEXT NOT NULL,
            notes TEXT
          );
          """;

      try (Statement stmt = this.connection.createStatement()) {
        stmt.execute(createTable);
      }

      return 0;
    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
  }

  public int closeDB() {
    try {
      this.connection.close();
      return 0;
    } catch (SQLException e) {
      e.printStackTrace();
      return 1;
    }
  }

  public void insertEntry(PasswordEntry entry) {
    String sql =
        "INSERT INTO password_entries(site, username, password, notes) VALUES (?, ?, ?, ?)";

    // NOTE: Put expression inside 'try' so it can automatically close at the end of
    // the block, even if it throws error. Similar to 'with' in python

    try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
      stmt.setString(1, entry.getSite());
      stmt.setString(2, entry.getUsername());
      stmt.setString(3, entry.getPassword());
      stmt.setString(4, entry.getNotes());

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String[][] getEntries() {

    // 1.) Count how many entires there are
    String sql_row_count_query = "SELECT COUNT(*) FROM password_entries";
    int count = 0;
    try (Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql_row_count_query)) {
      if (rs.next()) {
        count = rs.getInt(1); // get value of the first column
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    // 2.) Create 2D array to hold x amount of entries
    String[][] entries = new String[count][5];

    // 3.) Fill 2D array with entries
    String sql_query = "SELECT * FROM password_entries";
    try (Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql_query)) {
      int i = 0;
      while (rs.next()) {
        int id = rs.getInt(1);
        String site = rs.getString("site");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String notes = rs.getString("notes");
        entries[i][0] = Integer.toString(id);
        entries[i][1] = site;
        entries[i][2] = username;
        entries[i][3] = password;
        entries[i][4] = notes;
        i++;
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return entries;
  }

  public void deleteEntry(int entry_id) {
    String sql_stmt = "DELETE FROM password_entries WHERE id=?";

    try (PreparedStatement stmt = this.connection.prepareStatement(sql_stmt)) {
      stmt.setInt(1, entry_id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  // WARN: insecure, cause of .formatted use can do ' OR 1=1 --
  // public void insertEntry(PasswordEntry entry) {
  // String sql_entry =
  // """
  // INSERT INTO password_entries
  // VALUES (%s, %s, %s, %s)
  // """
  // .formatted(entry.getSite(), entry.getUsername(), entry.getPassword(),
  // entry.getNotes());
  //
  // try {
  //
  // try (Statement stmt = this.connection.createStatement()) {
  // stmt.execute(sql_entry);
  // }
  // } catch (SQLException e) {
  // e.getStackTrace();
  // }
  // }

  /*
   * Handles DB functions
   *
   * Methods to have
   * Get row passwordentries
   * Save password entry
   * Delete password entry
   */
}
