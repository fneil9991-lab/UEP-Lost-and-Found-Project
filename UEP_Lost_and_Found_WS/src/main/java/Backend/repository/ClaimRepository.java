package Backend.repository;

import Backend.config.Database;
import Backend.model.Claim;
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
 * Concrete ClaimRepository implementation using Database class
 * Replaces JPA interface with direct MySQL operations
 */
@Repository
public class ClaimRepository {
    
    private static final Logger logger = Logger.getLogger(ClaimRepository.class.getName());
    private Database database;
    private ItemRepository itemRepository;

    public ClaimRepository() {
        this.database = new Database();
        this.itemRepository = new ItemRepository();
    }

    /**
     * Find claim by ID
     */
    public Optional<Claim> findById(Long id) {
        String sql = "SELECT * FROM claims WHERE id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToClaim(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding claim by ID: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Find all claims
     */
    public List<Claim> findAll() {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT * FROM claims ORDER BY created_at DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                claims.add(mapResultSetToClaim(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all claims", e);
        }
        return claims;
    }

    /**
     * Find claims by claimant ID
     */
    public List<Claim> findByClaimantId(Integer claimantId) {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT * FROM claims WHERE claimant_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, claimantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapResultSetToClaim(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding claims by claimant ID: " + claimantId, e);
        }
        return claims;
    }

    /**
     * Find claims by status
     */
    public List<Claim> findByStatus(String status) {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT * FROM claims WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapResultSetToClaim(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding claims by status: " + status, e);
        }
        return claims;
    }

    /**
     * Find all claims with their associated items (JOIN query)
     */
    public List<Claim> findAllWithItems() {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT c.*, i.* FROM claims c " +
                     "JOIN items i ON c.item_id = i.id " +
                     "ORDER BY c.created_at DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                claims.add(mapResultSetToClaimWithItem(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding claims with items", e);
        }
        return claims;
    }

    /**
     * Find claims by item ID
     */
    public List<Claim> findByItemId(Long itemId) {
        List<Claim> claims = new ArrayList<>();
        String sql = "SELECT * FROM claims WHERE item_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapResultSetToClaim(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding claims by item ID: " + itemId, e);
        }
        return claims;
    }

    /**
     * Save (create or update) claim
     */
    public Claim save(Claim claim) {
        if (claim.getId() == null) {
            return create(claim);
        } else {
            return update(claim);
        }
    }

    /**
     * Create new claim
     */
    private Claim create(Claim claim) {
        String sql = "INSERT INTO claims (item_id, claimant_id, claimant_username, claim_description, status, date_submitted) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set item_id from the item relationship
            if (claim.getItem() != null && claim.getItem().getId() != null) {
                stmt.setLong(1, claim.getItem().getId());
            } else {
                throw new SQLException("Item must be set before creating claim");
            }
            
            stmt.setInt(2, claim.getClaimantId());
            stmt.setString(3, claim.getClaimantUsername());
            stmt.setString(4, claim.getClaimDescription());
            stmt.setString(5, claim.getStatus());
            stmt.setDate(6, java.sql.Date.valueOf(claim.getDateSubmitted().toLocalDate()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating claim failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    claim.setId(generatedKeys.getLong(1));
                }
            }
            
            logger.info("Claim created successfully with ID: " + claim.getId());
            return claim;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating claim", e);
            throw new RuntimeException("Failed to create claim", e);
        }
    }

    /**
     * Update existing claim
     */
    private Claim update(Claim claim) {
        String sql = "UPDATE claims SET item_id = ?, claimant_id = ?, claimant_username = ?, claim_description = ?, status = ?, date_submitted = ? WHERE id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, claim.getItem().getId());
            stmt.setInt(2, claim.getClaimantId());
            stmt.setString(3, claim.getClaimantUsername());
            stmt.setString(4, claim.getClaimDescription());
            stmt.setString(5, claim.getStatus());
            stmt.setDate(6, java.sql.Date.valueOf(claim.getDateSubmitted().toLocalDate()));
            stmt.setLong(7, claim.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating claim failed, no rows affected.");
            }
            
            logger.info("Claim updated successfully with ID: " + claim.getId());
            return claim;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating claim with ID: " + claim.getId(), e);
            throw new RuntimeException("Failed to update claim", e);
        }
    }

    /**
     * Delete claim by ID
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM claims WHERE id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Claim deleted successfully with ID: " + id);
            } else {
                logger.warning("No claim found to delete with ID: " + id);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting claim with ID: " + id, e);
            throw new RuntimeException("Failed to delete claim", e);
        }
    }

    /**
     * Count total claims
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM claims";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting claims", e);
        }
        return 0;
    }

    /**
     * Count claims by status
     */
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM claims WHERE status = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting claims by status: " + status, e);
        }
        return 0;
    }

    /**
     * Check if claim exists by ID
     */
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    /**
     * Map ResultSet to Claim entity (basic mapping without item)
     */
    private Claim mapResultSetToClaim(ResultSet rs) throws SQLException {
        Claim claim = new Claim();
        claim.setId(rs.getLong("id"));
        
        // Set item relationship
        Long itemId = rs.getLong("item_id");
        if (!rs.wasNull()) {
            Optional<Item> itemOpt = itemRepository.findById(itemId);
            claim.setItem(itemOpt.orElse(null));
        }
        
        claim.setClaimantId(rs.getInt("claimant_id"));
        claim.setClaimantUsername(rs.getString("claimant_username"));
        claim.setClaimDescription(rs.getString("claim_description"));
        claim.setStatus(rs.getString("status"));
        
        // Handle date fields - use date_submitted column
        Date submittedDate = rs.getDate("date_submitted");
        if (submittedDate != null) {
            claim.setDateSubmitted(submittedDate.toLocalDate().atStartOfDay());
        } else {
            claim.setDateSubmitted(LocalDateTime.now());
        }
        
        return claim;
    }

    /**
     * Map ResultSet to Claim entity with item (for JOIN queries)
     */
    private Claim mapResultSetToClaimWithItem(ResultSet rs) throws SQLException {
        Claim claim = new Claim();
        claim.setId(rs.getLong("id"));
        
        // Create Item from JOIN result
        Item item = new Item();
        item.setId(rs.getLong("i.id")); // Use alias from JOIN
        item.setName(rs.getString("i.name"));
        item.setDescription(rs.getString("i.description"));
        item.setStatus(rs.getString("i.status"));
        item.setReportedBy(rs.getString("i.reported_by"));
        item.setImage(rs.getString("i.image"));
        
        Timestamp itemDateReported = rs.getTimestamp("i.date_reported");
        if (itemDateReported != null) {
            item.setDateReported(itemDateReported.toLocalDateTime());
        }
        
        claim.setItem(item);
        claim.setClaimantId(rs.getInt("claimant_id"));
        claim.setClaimantUsername(rs.getString("claimant_username"));
        claim.setClaimDescription(rs.getString("claim_description"));
        claim.setStatus(rs.getString("status"));
        
        // Handle date fields - use date_submitted column
        Date submittedDate = rs.getDate("date_submitted");
        if (submittedDate != null) {
            claim.setDateSubmitted(submittedDate.toLocalDate().atStartOfDay());
        } else {
            claim.setDateSubmitted(LocalDateTime.now());
        }
        
        return claim;
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

    /**
     * Set item repository for testing
     */
    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
}
