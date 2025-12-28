package Backend.config;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database migration script to fix missing columns
 */
public class DatabaseMigration {
    
    private static final Logger logger = Logger.getLogger(DatabaseMigration.class.getName());
    
    public static void runMigration(Database database) {
        logger.info("Starting database migration...");
        
        try (Connection conn = database.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // === USERS TABLE MIGRATION ===
            
            // Add request_admin column if it doesn't exist
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN request_admin BOOLEAN DEFAULT FALSE");
                logger.info("Added request_admin column to users table");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name")) {
                    logger.info("request_admin column already exists");
                } else {
                    throw e;
                }
            }
            
            // Add status column if it doesn't exist
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN status VARCHAR(20) DEFAULT 'Active'");
                logger.info("Added status column to users table");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name")) {
                    logger.info("status column already exists");
                } else {
                    throw e;
                }
            }
            
            // Add created_at column if it doesn't exist
            try {
                stmt.execute("ALTER TABLE users ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
                logger.info("Added created_at column to users table");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name")) {
                    logger.info("created_at column already exists");
                } else {
                    throw e;
                }
            }
            
            // Update existing admin user
            stmt.execute("UPDATE users SET request_admin = FALSE WHERE username = 'admin' AND type = 'Admin'");
            logger.info("Updated existing admin user");
            
            // === ITEMS TABLE MIGRATION ===
            
            // Add missing columns to items table if they don't exist
            try {
                stmt.execute("ALTER TABLE items ADD COLUMN reported_by VARCHAR(50) NOT NULL DEFAULT 'Unknown'");
                logger.info("Added reported_by column to items table");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name") || e.getMessage().contains("already exists")) {
                    logger.info("reported_by column already exists");
                } else {
                    logger.warning("Could not add reported_by column: " + e.getMessage());
                }
            }
            
            try {
                stmt.execute("ALTER TABLE items ADD COLUMN user_id INT NOT NULL DEFAULT 1");
                logger.info("Added user_id column to items table");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name") || e.getMessage().contains("already exists")) {
                    logger.info("user_id column already exists");
                } else {
                    logger.warning("Could not add user_id column: " + e.getMessage());
                }
            }
            
            // Add foreign key constraint if it doesn't exist
            try {
                stmt.execute("ALTER TABLE items ADD CONSTRAINT fk_items_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE");
                logger.info("Added foreign key constraint for user_id in items table");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate key name") || e.getMessage().contains("already exists") || e.getMessage().contains("Duplicate constraint name")) {
                    logger.info("Foreign key constraint already exists");
                } else {
                    logger.warning("Could not add foreign key constraint: " + e.getMessage());
                }
            }
            
            // === CLAIMS TABLE MIGRATION ===
            
            // Add claimant_username column if it doesn't exist
            try {
                stmt.execute("ALTER TABLE claims ADD COLUMN claimant_username VARCHAR(50) NOT NULL DEFAULT ''");
                logger.info("Added claimant_username column to claims table");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name") || e.getMessage().contains("already exists")) {
                    logger.info("claimant_username column already exists");
                } else {
                    logger.warning("Could not add claimant_username column: " + e.getMessage());
                }
            }
            
            // Add date_submitted column if it doesn't exist
            try {
                stmt.execute("ALTER TABLE claims ADD COLUMN date_submitted DATE NOT NULL DEFAULT '2024-01-01'");
                logger.info("Added date_submitted column to claims table");
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate column name") || e.getMessage().contains("already exists")) {
                    logger.info("date_submitted column already exists");
                } else {
                    logger.warning("Could not add date_submitted column: " + e.getMessage());
                }
            }
            
            logger.info("Database migration completed successfully");
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database migration failed", e);
            throw new RuntimeException("Database migration failed", e);
        }
    }
}
