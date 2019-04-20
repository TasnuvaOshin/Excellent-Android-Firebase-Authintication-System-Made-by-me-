package searchme.iadc.searchme.Model;

public class QuickAccessModel {
    String email;
    String macaddress;

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

    public QuickAccessModel() {
    }

    public QuickAccessModel(String email, String macaddress) {
        this.email = email;
        this.macaddress = macaddress;
    }
}
