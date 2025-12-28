package Backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * H2 Database Connection Manager
 * Compatible with Spring Boot's H2 in-memory database
 */
public class DatabaseH2 {
    private static final Logger logger = Logger.getLogger(DatabaseH2.class.getName());

    /**
     * Gets H2 database connection using Spring Boot's DataSource
     * This method integrates with Spring Boot's H2 configuration
     */
    public static Connection getConnection() {
        try {
            // Load H2 JDBC driver
            Class.forName("org.h2.Driver");
            
            // Connect to H2 in-memory database (same as configured in application.properties)
            String url = "jdbc:h2:mem:uep_lost_found;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
            String username = "sa";
            String password = "";
            
            Connection conn = DriverManager.getConnection(url, username, password);
            
            logger.info("H2 Database connection established successfully");
            return conn;
            
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "H2 JDBC Driver not found", e);
            System.err.println("H2 driver error: " + e.getMessage());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "H2 Connection error: " + e.getMessage(), e);
            System.err.println("H2 connection failed: " + e.getMessage());
        }

        return null;
    }

    /**
     * Test H2 connection
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                logger.info("H2 Database connection test successful");
                conn.close();
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "H2 Database connection test failed", e);
        }
        return false;
    }
}
