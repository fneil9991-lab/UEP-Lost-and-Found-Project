package Backend.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Example usage of the Database class
 * Demonstrates how to use the Database class for database operations
 */
public class DatabaseExample {
    
    public static void main(String[] args) {
        // Create Database instance
        Database database = new Database();
        
        // Display database configuration
        System.out.println(database.getDatabaseInfo());
        System.out.println("\n" + "=".repeat(50));
        
        // Test database connection
        System.out.println("Testing database connection...");
        if (database.testConnection()) {
            System.out.println("✅ Database connection successful!");
            
            // Example: Execute a simple query
            performSampleQuery(database);
            
        } else {
            System.out.println("❌ Database connection failed!");
            System.out.println("Please check your database configuration.");
        }
        
        // Close connection
        database.closeConnection();
        System.out.println("\nConnection closed.");
    }
    
    /**
     * Example method showing how to execute a query using the Database class
     */
    private static void performSampleQuery(Database database) {
        Connection conn = database.getConnection();
        
        if (conn != null) {
            try {
                // Example: Get database version (simple test query)
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT VERSION() as mysql_version");
                
                if (rs.next()) {
                    System.out.println("MySQL Version: " + rs.getString("mysql_version"));
                }
                
                // Example: Show current database
                ResultSet rs2 = stmt.executeQuery("SELECT DATABASE() as current_db");
                if (rs2.next()) {
                    System.out.println("Current Database: " + rs2.getString("current_db"));
                }
                
                rs.close();
                rs2.close();
                stmt.close();
                
            } catch (Exception e) {
                System.err.println("Error executing sample query: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
