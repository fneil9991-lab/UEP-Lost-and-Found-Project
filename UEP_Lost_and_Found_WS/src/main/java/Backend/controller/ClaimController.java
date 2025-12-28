package Backend.controller;

import Backend.model.Claim;
import Backend.model.User;
import Backend.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createClaim(@RequestBody Map<String, Object> claimData, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        String action = (String) claimData.get("action");
        if ("create".equals(action)) {
            Long itemId = Long.valueOf(claimData.get("item_id").toString());
            String claimantUsername = (String) claimData.get("claimant_username");
            String claimDescription = (String) claimData.get("claim_description");

            try {
                Claim claim = claimService.createClaim(itemId, claimantUsername, claimDescription);
                return ResponseEntity.ok(Map.of("message", "Claim submitted successfully", "claim", claim));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        } else if ("approve".equals(action)) {
            Long claimId = Long.valueOf(claimData.get("id").toString());
            String approverUsername = (String) claimData.get("approver_username");

            if (!"Admin".equals(currentUser.getType())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            try {
                claimService.approveClaim(claimId, approverUsername);
                return ResponseEntity.ok(Map.of("message", "Claim approved successfully"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        } else if ("reject".equals(action)) {
            Long claimId = Long.valueOf(claimData.get("id").toString());
            String approverUsername = (String) claimData.get("approver_username");

            if (!"Admin".equals(currentUser.getType())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            try {
                claimService.rejectClaim(claimId, approverUsername);
                return ResponseEntity.ok(Map.of("message", "Claim rejected successfully"));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Invalid action"));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getClaims(@RequestParam(value = "action", required = false) String action,
                                                         @RequestParam(value = "username", required = false) String username,
                                                         HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        if ("user".equals(action) && username != null) {
            if (!currentUser.getUsername().equals(username) && !"Admin".equals(currentUser.getType())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            List<Claim> claims = claimService.getClaimsByUser(username);
            return ResponseEntity.ok(Map.of("claims", claims));
        } else if ("pending".equals(action)) {
            if (!"Admin".equals(currentUser.getType())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            List<Claim> claims = claimService.getPendingClaims();
            return ResponseEntity.ok(Map.of("claims", claims));
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));
    }
}
