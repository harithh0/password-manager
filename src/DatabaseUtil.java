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

  /*
   * Handles DB functions
   *
   * Methods to have
   * Get row passwordentries
   * Save password entry
   * Delete password entry
   */
}
