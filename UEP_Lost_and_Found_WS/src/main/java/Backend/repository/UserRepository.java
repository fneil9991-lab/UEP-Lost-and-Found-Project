package Backend.repository;

import Backend.config.Database;
import Backend.model.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UserRepository implementation using MySQL database
 */
@Repository
public class UserRepository {
    
    private static final Logger logger = Logger.getLogger(UserRepository.class.getName());
    private Database database;

    public UserRepository() {
        this.database = new Database();
    }

    /**
     * Find user by ID
     */
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding user by ID: " + id, e);
        }
        return Optional.empty();
    }

    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding user by username: " + username, e);
        }
        return Optional.empty();
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding user by email: " + email, e);
        }
        return Optional.empty();
    }

    /**
     * Find all users
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY lname, fname";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding all users", e);
        }
        return users;
    }

    /**
     * Find users who requested admin access
     */
    public List<User> findByRequestAdminTrue() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE request_admin = 1 ORDER BY lname, fname";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding users requesting admin access", e);
        }
        return users;
    }

    /**
     * Save (create or update) user
     */
    public User save(User user) {
        if (user.getId() == null) {
            return create(user);
        } else {
            return update(user);
        }
    }

    /**
     * Create new user
     */
    private User create(User user) {
        String sql = "INSERT INTO users (fname, mname, lname, type, email, username, password, request_admin) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getFname());
            stmt.setString(2, user.getMname());
            stmt.setString(3, user.getLname());
            stmt.setString(4, user.getType());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getUsername());
            stmt.setString(7, user.getPassword());
            stmt.setBoolean(8, user.isRequestAdmin());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }
            
            logger.info("User created successfully with ID: " + user.getId());
            return user;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating user", e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    /**
     * Update existing user
     */
    private User update(User user) {
        String sql = "UPDATE users SET fname = ?, mname = ?, lname = ?, type = ?, email = ?, " +
                     "username = ?, password = ?, request_admin = ? WHERE id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFname());
            stmt.setString(2, user.getMname());
            stmt.setString(3, user.getLname());
            stmt.setString(4, user.getType());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getUsername());
            stmt.setString(7, user.getPassword());
            stmt.setBoolean(8, user.isRequestAdmin());
            stmt.setLong(9, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            
            logger.info("User updated successfully with ID: " + user.getId());
            return user;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating user with ID: " + user.getId(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    /**
     * Delete user by User object
     */
    public void delete(User user) {
        if (user != null && user.getId() != null) {
            deleteById(user.getId());
        }
    }

    /**
     * Delete user by ID
     */
    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("User deleted successfully with ID: " + id);
            } else {
                logger.warning("No user found to delete with ID: " + id);
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting user with ID: " + id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    /**
     * Delete user by username
     */
    public void deleteByUsername(String username) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            deleteById(userOpt.get().getId());
        }
    }

    /**
     * Count total users
     */
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting users", e);
        }
        return 0;
    }

    /**
     * Check if username exists
     */
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    /**
     * Map ResultSet to User entity
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFname(rs.getString("fname"));
        user.setMname(rs.getString("mname"));
        user.setLname(rs.getString("lname"));
        user.setType(rs.getString("type"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRequestAdmin(rs.getBoolean("request_admin"));
        return user;
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
