package Backend.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database Connection Manager
 * Java equivalent of the PHP Database class
 * Handles MySQL database connections with environment variable support
 */
public class Database {
    private String host;
    private String dbName;
    private String username;
    private String password;
    private Connection conn;
    private static final Logger logger = Logger.getLogger(Database.class.getName());

    /**
     * Constructor that initializes database configuration from environment variables
     * Falls back to default values if environment variables are not set
     */
    public Database() {
        sa
        
        logger.info("Database configuration initialized:");
        logger.info("Host: " + this.host);
        logger.info("Database: " + this.dbName);
        logger.info("Username: " + this.username);
    }

    /**
     * Gets environment variable or returns default value
     * @param envVar Environment variable name
     * @param defaultValue Default value if environment variable is not set
     * @return Environment variable value or default
     */
    private String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    /**
     * Establishes and returns a database connection
     * @return Connection object or null if connection fails
     */
    public Connection getConnection() {
        this.conn = null;

        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Build connection URL with UTF8MB4 charset
            String url = String.format(
                "jdbc:mysql://%s/%s?charset=utf8mb4&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                this.host,
                this.dbName
            );
            
            // Establish connection
            this.conn = DriverManager.getConnection(url, this.username, this.password);
            
            logger.info("Database connection established successfully");
            
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            System.err.println("Database driver error: MySQL JDBC Driver not found. Please ensure MySQL connector is in classpath.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Connection error: " + e.getMessage(), e);
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Please check your database configuration and ensure MySQL server is running.");
        }

        return this.conn;
    }

    /**
     * Closes the database connection
     */
    public void closeConnection() {
        if (this.conn != null) {
            try {
                this.conn.close();
                logger.info("Database connection closed successfully");
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }

    /**
     * Gets the current connection
     * @return Current Connection object
     */
    public Connection getConn() {
        return this.conn;
    }

    /**
     * Sets the connection manually (useful for testing)
     * @param conn Connection object to set
     */
    public void setConn(Connection conn) {
        this.conn = conn;
    }

    /**
     * Gets database configuration details
     * @return String representation of database configuration
     */
    public String getDatabaseInfo() {
        return String.format(
            "Database Configuration:\n" +
            "Host: %s\n" +
            "Database: %s\n" +
            "Username: %s\n" +
            "Password: %s\n" +
            "Connection: %s",
            this.host,
            this.dbName,
            this.username,
            this.password.replaceAll(".", "*"),
            (this.conn != null) ? "Active" : "Not Connected"
        );
    }

    /**
     * Tests the database connection
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            Connection testConn = this.getConnection();
            if (testConn != null && !testConn.isClosed()) {
                logger.info("Database connection test successful");
                this.closeConnection();
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database connection test failed", e);
        }
        return false;
    }
}
