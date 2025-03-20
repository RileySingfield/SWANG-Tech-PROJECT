package Back_end;


public class ProductUser {
    private String username;
    private String passwordHash;
    private String password;

    public ProductUser(String username, String password) {
        this.username = username;
        this.password= password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}

