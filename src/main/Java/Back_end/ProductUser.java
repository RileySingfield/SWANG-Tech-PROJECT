package Back_end;

public class ProductUser {
    private String username;
    private String password;
    private String userType;

    public ProductUser (String username, String password, String userType){
        this.username= username;
        this.password= password;
        this.userType= userType;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(userType);
    }

}
