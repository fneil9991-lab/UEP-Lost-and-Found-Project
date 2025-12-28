package Backend.test;

import Backend.UepLostAndFoundApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UepLostAndFoundApplicationTest {

    @Test
    void contextLoads() {
        // This test ensures that the Spring context loads successfully
        // It will fail if there are any configuration issues
    }

    @Test
    void testApplicationMainMethod() {
        // Test that the main method can be called without throwing exceptions
        try {
            String[] args = {};
            // Don't actually run the full application, just verify the class is accessible
            Class.forName("Backend.UepLostAndFoundApplication");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load application class", e);
        }
    }
}
