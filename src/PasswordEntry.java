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

  public String getUsername() {
    return this.username;
  }
  ;

  public String getPassword() {
    return this.password;
  }
  ;

  public String getSite() {
    return this.site;
  }
  ;

  public String getNotes() {
    return this.notes;
  }
  ;
}
