
// for generating salt
import java.security.SecureRandom;
// for hashing password + salt
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

// for deriving encryption key from password + salt
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.*;

public class DatabaseUtil {

  private String DB_URL;
  private Connection connection;
  private SecretKey user_key;

  DatabaseUtil(String db_url) {
    this.DB_URL = db_url;
  }

  public int connectDB() {
    try {
      // Load SQLite JDBC driver
      Class.forName("org.sqlite.JDBC");

      // Connect (creates DB file if it doesn't exist)
      this.connection = DriverManager.getConnection(DB_URL);

      String create_password_entries_table = """
          CREATE TABLE IF NOT EXISTS password_entries (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            site BLOB NOT NULL,
            username BLOB NOT NULL,
            password BLOB NOT NULL,
            notes BLOB,
            FOREIGN KEY(user_id) REFERENCES users(id)
          );
          """;

      String create_users_table = """
          CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            salt BLOB NOT NULL
          );
          """;

      try (Statement stmt = this.connection.createStatement()) {
        stmt.execute(create_password_entries_table);
        stmt.execute(create_users_table);
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
    String sql = "INSERT INTO password_entries(site, username, password, notes) VALUES (?, ?, ?, ?)";

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
  public static String hashPassword(String password, byte[] salt) throws Exception {
    int iterations = 65536;
    int keyLength = 256; // bits
    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] hash = skf.generateSecret(spec).getEncoded();
    return Base64.getEncoder().encodeToString(hash);
  }

  public static SecretKey deriveEncryptionKey(String password, byte[] salt) throws Exception {
    int iterations = 65536;
    int keyLength = 256;
    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
    SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    byte[] keyBytes = skf.generateSecret(spec).getEncoded();
    return new SecretKeySpec(keyBytes, "AES");
  }

  public int handleLogin(String username, String password) {
    /*
     * WARN: Current issues:
     * - Timing vuln: user doesnt exist returns way faster response bc not checking
     * - Compare hashes more securly
     * - Clear the memory or zero out sensitive variables
     * 
     * TODO:
     * - Add rate limiting, lockouts
     *
     */

    String user_exist_sql = "SELECT COUNT(*) FROM users WHERE username=?";

    try (PreparedStatement stmt = this.connection.prepareStatement(user_exist_sql)) {
      stmt.setString(1, username);

      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
        int user_exists = rs.getInt(1);
        if (user_exists == 1) {
          // continue
          ;

        } else {
          // WARN: change
          System.out.println("User does not exist");
          return 1;
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    String user_data_sql = "SELECT * FROM users WHERE username=?";
    try (PreparedStatement stmt = this.connection.prepareStatement(user_data_sql)) {
      stmt.setString(1, username);

      ResultSet result = stmt.executeQuery();

      if (result.next()) {
        byte[] salt = result.getBytes(4);
        try {

          String user_provided_password_hashed = hashPassword(password, salt);
          String hashed_password = result.getString(3);
          if (user_provided_password_hashed.equals(hashed_password)) {
            this.user_key = deriveEncryptionKey(password, salt);
            System.out.println("Correct");
            return 0;
          } else {
            System.out.println("Wrong");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

      }

    } catch (SQLException e) {
      e.printStackTrace();
    }

    // return non 0 (err)
    return 1;
    /*
     * 1. check if user exists in table
     * 2. check if given password + users salt match the hashed password
     * 3. derive key from password if correct
     */

  }

  public void createUser(String username, String hashed_password, byte[] salt) {

    String sql_smnt = "INSERT INTO users(username, password, salt) VALUES(?, ?, ?)";

    try (PreparedStatement stmt = this.connection.prepareStatement(sql_smnt)) {
      stmt.setString(1, username);
      stmt.setString(2, hashed_password);
      stmt.setBytes(3, salt);

      stmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  public void handleSignup(String username, String password) {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    try {
      String hashed_password = hashPassword(password, salt);
      SecretKey key = deriveEncryptionKey(password, salt);

      // write hashed password, username, and salt in users table
      createUser(username, hashed_password, salt);
      this.user_key = key;

    } catch (Exception e) {
      System.err.println(e);
    }

  }
}
