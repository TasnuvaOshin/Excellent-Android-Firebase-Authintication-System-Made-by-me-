package searchme.iadc.searchme.Model;

public class VerificationModel {
    String nickname;
    String hobby;
    String friend;
    String email;

    public VerificationModel() {
    }

    public VerificationModel(String nickname, String hobby, String friend, String email) {
        this.nickname = nickname;
        this.hobby = hobby;
        this.friend = friend;
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
