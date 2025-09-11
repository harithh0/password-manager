public class PasswordEntry {

  private String username;
  private String password;
  private String site;
  private String notes;

  public PasswordEntry(String username, String password, String site, String notes) {
    this.username = username;
    this.password = hashPassword(password);
    this.site = site;
    this.notes = notes;
  }

  private String hashPassword(String plaintext_password) {
    return "";
  }
}
