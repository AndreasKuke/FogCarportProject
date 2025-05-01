package app.entities;

public class User {

    int user_ID;
    String username;
    String password;
    String email;
    String phonenumber;
    boolean role;

    public User(User user) {
        this.user_ID = user.user_ID;
    }

    public User(User user, String username, String password, String email, String phonenumber) {
        this.user_ID = user.user_ID;
        this.username = user.username;
        this.password = password;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    public User(User user, String username, String password, String email, String phonenumber, boolean role) {
        this.user_ID = user.user_ID;
        this.username = user.username;
        this.password = password;
        this.email = email;
        this.phonenumber = phonenumber;
        this.role = role;
    }

    public int getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(int user_ID) {
        this.user_ID = user_ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }
}
