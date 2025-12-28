
package Backend;

import Backend.config.Database;
import Backend.config.DatabaseMigration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UepLostAndFoundApplication {

    public static void main(String[] args) {
        try {
            // Run database migration first
            System.out.println("Running database migration...");
            Database database = new Database();
            DatabaseMigration.runMigration(database);
            System.out.println("Database migration completed");
            
            // Then start the Spring Boot application
            SpringApplication.run(UepLostAndFoundApplication.class, args);
            
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
