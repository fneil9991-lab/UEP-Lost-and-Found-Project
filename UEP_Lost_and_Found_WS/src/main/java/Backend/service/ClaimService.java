

package Backend.service;

import Backend.model.Claim;
import Backend.model.Item;
import Backend.model.User;
import Backend.repository.ClaimRepository;
import Backend.repository.ItemRepository;
import Backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClaimService {

    private ClaimRepository claimRepository;
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    public ClaimService() {
        this.claimRepository = new ClaimRepository();
        this.itemRepository = new ItemRepository();
        this.userRepository = new UserRepository();
    }

    // Constructor for dependency injection (if needed)
    public ClaimService(ClaimRepository claimRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }


    public List<Claim> getClaimsByUser(String username) {
        // Find user by username to get the claimant ID
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            return claimRepository.findByClaimantId(userOpt.get().getId().intValue());
        }
        return List.of(); // Return empty list if user not found
    }

    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatus("Pending");
    }

    public Claim createClaim(Long itemId, String claimantUsername, String description) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        Optional<User> userOpt = userRepository.findByUsername(claimantUsername);
        
        if (itemOpt.isPresent() && userOpt.isPresent()) {
            Claim claim = new Claim();
            claim.setItem(itemOpt.get());
            claim.setClaimantId(userOpt.get().getId().intValue());
            claim.setClaimantUsername(claimantUsername);
            claim.setClaimDescription(description);
            claim.setStatus("Pending");
            claim.setDateSubmitted(LocalDateTime.now());
            
            return claimRepository.save(claim);
        }
        throw new RuntimeException("Item or user not found");
    }

    public void approveClaim(Long claimId, String approverUsername) {
        Optional<Claim> claimOpt = claimRepository.findById(claimId);
        if (claimOpt.isPresent()) {
            Claim claim = claimOpt.get();
            claim.setStatus("Approved");
            claim.setApproverUsername(approverUsername);
            claim.setDateApproved(LocalDateTime.now());
            claimRepository.save(claim);
        } else {
            throw new RuntimeException("Claim not found");
        }
    }

    public void rejectClaim(Long claimId, String approverUsername) {
        Optional<Claim> claimOpt = claimRepository.findById(claimId);
        if (claimOpt.isPresent()) {
            Claim claim = claimOpt.get();
            claim.setStatus("Rejected");
            claim.setApproverUsername(approverUsername);
            claim.setDateApproved(LocalDateTime.now());
            claimRepository.save(claim);
        } else {
            throw new RuntimeException("Claim not found");
        }
    }
}
