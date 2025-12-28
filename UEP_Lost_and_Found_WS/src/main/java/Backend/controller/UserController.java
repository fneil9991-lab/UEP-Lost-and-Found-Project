package Backend.controller;

import Backend.model.User;
import Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (user.getType().equals("Admin") && user.isRequestAdmin()) {
                user.setType("Student"); // Set to Student until approved
            }
            User savedUser = userService.saveUser(user);
            response.put("message", "User registered successfully");
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String username = loginData.get("username");
        String password = loginData.get("password");

        Optional<User> userOpt = userService.authenticate(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("user", user);
            response.put("message", "Login successful");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"Admin".equals(currentUser.getType())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
        }

        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(Map.of("users", users));
    }


    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-admins")
    public ResponseEntity<Map<String, Object>> getPendingAdmins(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"Admin".equals(currentUser.getType())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
        }

        List<User> pendingAdmins = userService.getPendingAdmins();
        return ResponseEntity.ok(Map.of("pending_admins", pendingAdmins));
    }

    @PostMapping("/approve-admin")
    public ResponseEntity<Map<String, Object>> approveAdmin(@RequestBody Map<String, String> data, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"Admin".equals(currentUser.getType())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
        }

        String username = data.get("username");
        String approverUsername = data.get("approver_username");

        try {
            userService.approveAdmin(username);
            return ResponseEntity.ok(Map.of("message", "Admin approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody User updatedUser, @RequestParam String username, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !currentUser.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
        }

        try {
            User savedUser = userService.updateUser(username, updatedUser);
            session.setAttribute("user", savedUser);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestParam String username, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || !"Admin".equals(currentUser.getType())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
        }

        try {
            userService.deleteUser(username);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
