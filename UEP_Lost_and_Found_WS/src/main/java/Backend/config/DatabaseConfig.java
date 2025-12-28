

package Backend.config;

import org.springframework.context.annotation.Configuration;

/**
 * Database Configuration for MySQL
 * The datasource is configured in application.properties
 * This ensures Spring Boot uses the MySQL configuration
 */
@Configuration
public class DatabaseConfig {
    // Database configuration is handled by Spring Boot through application.properties
    // No explicit DataSource bean needed as Spring Boot auto-configures from properties
}

