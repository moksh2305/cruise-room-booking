package cruise.model;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String mobile;
    private String email;

    public User(String name, String mobile, String email) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
    }

    public String getName() { return name; }
    public String getMobile() { return mobile; }
    public String getEmail() { return email; }
}
