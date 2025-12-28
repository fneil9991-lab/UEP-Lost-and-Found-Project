package Backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Component
public class DataLoader implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("✅ Application started successfully!");
        System.out.println("🔧 DataLoader initialized - database schema should be ready.");
        
        // Ensure admin user has proper BCrypt password
        ensureAdminUser();
    }
    
    private void ensureAdminUser() {
        Database database = new Database();
        try (Connection conn = database.getConnection()) {
            // Check if admin user exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = 'admin' AND type = 'Admin'";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                 ResultSet rs = checkStmt.executeQuery()) {
                rs.next();
                int count = rs.getInt(1);
                
                if (count == 0) {
                    // Admin user doesn't exist, create it
                    createAdminUser(conn);
                } else {
                    // Admin user exists, update password to proper BCrypt
                    updateAdminPassword(conn);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error ensuring admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createAdminUser(Connection conn) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode("uep123");
        
        String insertQuery = "INSERT INTO users (fname, mname, lname, type, email, username, password, request_admin, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, "System");
            stmt.setString(2, "");
            stmt.setString(3, "Admin");
            stmt.setString(4, "Admin");
            stmt.setString(5, "admin@uep.edu.ph");
            stmt.setString(6, "admin");
            stmt.setString(7, hashedPassword);
            stmt.setBoolean(8, false);
            stmt.setString(9, "Active");
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Admin user created successfully with BCrypt password");
            }
        }
    }
    
    private void updateAdminPassword(Connection conn) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode("uep123");
        
        String updateQuery = "UPDATE users SET password = ? WHERE username = 'admin' AND type = 'Admin'";
        
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, hashedPassword);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Admin user password updated to BCrypt hash");
            }
        }
    }
}
