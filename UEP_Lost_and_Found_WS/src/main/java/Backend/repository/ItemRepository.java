
package Backend.repository;

import Backend.config.Database;
import Backend.model.Item;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete ItemRepository implementation using Database class
 * Replaces JPA interface with direct MySQL operations
 */
@Repository
public class ItemRepository {
    
    private static final Logger logger = Logger.getLogger(ItemRepository.class.getName());
    private Database database;

    public ItemRepository() {
        this.database = new Database();
    }

    /**
     * Find item by ID
     */
    public Optional<Item> findById(Long id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding item by ID: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Find all items
     */
    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY date_reported DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all items", e);
        }
        return items;
    }

    /**
     * Find items by status
     */
    public List<Item> findByStatus(String status) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE status = ? ORDER BY date_reported DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding items by status: " + status, e);
        }
        return items;
    }

    /**
     * Find items by reported by user
     */
    public List<Item> findByReportedBy(String reportedBy) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE reported_by = ? ORDER BY date_reported DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, reportedBy);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding items by reporter: " + reportedBy, e);
        }
        return items;
    }

    /**
     * Find items by name containing search term
     */
    public List<Item> findByNameContaining(String searchTerm) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE name LIKE ? ORDER BY date_reported DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding items by name containing: " + searchTerm, e);
        }
        return items;
    }

    /**
     * Find items by description containing search term
     */
    public List<Item> findByDescriptionContaining(String searchTerm) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE description LIKE ? ORDER BY date_reported DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding items by description containing: " + searchTerm, e);
        }
        return items;
    }

    /**
     * Save (create or update) item
     */
    public Item save(Item item) {
        if (item.getId() == null) {
            return create(item);
        } else {
            return update(item);
        }
    }

    /**
     * Create new item
     */
    private Item create(Item item) {
        String sql = "INSERT INTO items (name, description, status, reported_by, image, user_id, date_reported) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDesc());
            stmt.setString(3, item.getStatus());
            stmt.setString(4, item.getReportedBy());
            stmt.setString(5, item.getImage());
            stmt.setLong(6, item.getUserId());
            
            // Add null check for dateReported to prevent NullPointerException
            if (item.getDateReported() != null) {
                stmt.setDate(7, java.sql.Date.valueOf(item.getDateReported().toLocalDate()));
            } else {
                // Use current date if dateReported is null
                stmt.setDate(7, java.sql.Date.valueOf(java.time.LocalDate.now()));
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating item failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getLong(1));
                }
            }
            
            logger.info("Item created successfully with ID: " + item.getId());
            return item;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating item", e);
            throw new RuntimeException("Failed to create item", e);
        }
    }

    /**
     * Update existing item
     */
    private Item update(Item item) {
        String sql = "UPDATE items SET name = ?, description = ?, status = ?, reported_by = ?, " +
                     "image = ?, user_id = ?, date_reported = ? WHERE id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDesc());
            stmt.setString(3, item.getStatus());
            stmt.setString(4, item.getReportedBy());
            stmt.setString(5, item.getImage());
            stmt.setLong(6, item.getUserId());
            stmt.setDate(7, java.sql.Date.valueOf(item.getDateReported().toLocalDate()));
            stmt.setLong(8, item.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating item failed, no rows affected.");
            }
            
            logger.info("Item updated successfully with ID: " + item.getId());
            return item;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating item with ID: " + item.getId(), e);
            throw new RuntimeException("Failed to update item", e);
        }
    }

    /**
     * Delete item by ID
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM items WHERE id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Item deleted successfully with ID: " + id);
            } else {
                logger.warning("No item found to delete with ID: " + id);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting item with ID: " + id, e);
            throw new RuntimeException("Failed to delete item", e);
        }
    }

    /**
     * Count total items
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM items";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting items", e);
        }
        return 0;
    }

    /**
     * Count items by status
     */
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM items WHERE status = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting items by status: " + status, e);
        }
        return 0;
    }

    /**
     * Check if item exists by ID
     */
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    /**
     * Map ResultSet to Item entity
     */
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setName(rs.getString("name"));
        item.setDesc(rs.getString("description")); // Map database column to model field
        item.setStatus(rs.getString("status"));
        item.setReportedBy(rs.getString("reported_by")); // Map database column to model field
        item.setImage(rs.getString("image"));
        item.setUserId(rs.getLong("user_id")); // Map database column to model field
        
        // Handle potential null values for date
        Date date = rs.getDate("date_reported");
        if (date != null) {
            item.setDateReported(date.toLocalDate().atStartOfDay());
        } else {
            item.setDateReported(LocalDateTime.now());
        }
        
        return item;
    }

    /**
     * Get database instance for testing
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Set database instance for testing
     */
    public void setDatabase(Database database) {
        this.database = database;
    }
}
