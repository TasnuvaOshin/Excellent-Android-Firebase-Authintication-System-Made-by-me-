package searchme.iadc.searchme.Model;

public class UserDataModel {
    private String username;
    private String email;
    private String macaddress;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public UserDataModel() {
    }

    public UserDataModel(String username, String email, String macaddress) {
        this.username = username;
        this.email = email;
        this.macaddress = macaddress;
    }
}
