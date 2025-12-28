package Backend.controller;

import Backend.model.Item;
import Backend.model.User;
import Backend.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    private final String UPLOAD_DIR = "uploads/";

    @GetMapping
    public ResponseEntity<Map<String, Object>> getItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(Map.of("items", items));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> reportItem(
            @RequestParam("status") String status,
            @RequestParam("name") String name,
            @RequestParam("desc") String description,
            @RequestParam("reportedBy") String reportedBy,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session) {

        try {
            User currentUser = (User) session.getAttribute("user");
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
            }

            // Validate required parameters
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Item name is required"));
            }
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Item description is required"));
            }
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Item status is required"));
            }

            String imagePath = null;
            if (image != null && !image.isEmpty()) {
                try {
                    // Ensure upload directory exists
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // Save file
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.write(filePath, image.getBytes());
                    imagePath = filePath.toString();
                } catch (IOException e) {
                    return ResponseEntity.status(500).body(Map.of("error", "Failed to upload image: " + e.getMessage()));
                }
            }

            Item item = new Item(name.trim(), description.trim(), status.trim(), reportedBy, imagePath);
            item.setUserId(currentUser.getId());
            
            Item savedItem = itemService.saveItem(item);

            return ResponseEntity.ok(Map.of("message", "Item reported successfully", "item", savedItem));
            
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error reporting item: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(500).body(Map.of(
                "error", "Failed to report item: " + e.getMessage(),
                "details", "Please contact system administrator if the problem persists"
            ));
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteItem(@RequestParam("id") Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"Admin".equals(currentUser.getType())) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }

        Optional<Item> itemOpt = itemService.getItemById(id);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            if (item.getImage() != null) {
                try {
                    Files.deleteIfExists(Paths.get(item.getImage()));
                } catch (IOException e) {
                    // Log error but continue
                }
            }
            itemService.deleteItem(id);
            return ResponseEntity.ok(Map.of("message", "Item deleted successfully"));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Item not found"));
        }
    }
}
