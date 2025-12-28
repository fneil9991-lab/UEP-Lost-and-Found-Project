package Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 50)
    private String fname;

    @Column(length = 50)
    private String mname;

    @NotBlank
    @Column(length = 50)
    private String lname;

    @NotBlank
    @Column(length = 20)
    private String type; // Student, Faculty, UEP Staff, Admin

    @NotBlank
    @Email
    @Column(unique = true, length = 100)
    private String email;

    @NotBlank
    @Column(unique = true, length = 50)
    private String username;

    @NotBlank
    @Column(length = 255)
    private String password;

    @Column(name = "request_admin")
    private boolean requestAdmin = false;

    // Constructors
    public User() {}

    public User(String fname, String mname, String lname, String type, String email, String username, String password) {
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.type = type;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFname() { return fname; }
    public void setFname(String fname) { this.fname = fname; }

    public String getMname() { return mname; }
    public void setMname(String mname) { this.mname = mname; }

    public String getLname() { return lname; }
    public void setLname(String lname) { this.lname = lname; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isRequestAdmin() { return requestAdmin; }
    public void setRequestAdmin(boolean requestAdmin) { this.requestAdmin = requestAdmin; }
}
