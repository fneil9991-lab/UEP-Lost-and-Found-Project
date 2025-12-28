package Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull
    private Integer claimantId;

    @NotBlank
    private String claimantUsername;

    @NotBlank
    @Column(length = 500)
    private String claimDescription;

    @NotBlank
    @Column(length = 20)
    private String status = "Pending"; // Pending, Approved, Rejected

    @NotNull
    private LocalDateTime dateSubmitted = LocalDateTime.now();

    private String approverUsername;

    private LocalDateTime dateApproved;

    // Constructors
    public Claim() {}

    public Claim(Item item, Integer claimantId, String claimDescription) {
        this.item = item;
        this.claimantId = claimantId;
        this.claimDescription = claimDescription;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Integer getClaimantId() { return claimantId; }
    public void setClaimantId(Integer claimantId) { this.claimantId = claimantId; }

    public String getClaimantUsername() { return claimantUsername; }
    public void setClaimantUsername(String claimantUsername) { this.claimantUsername = claimantUsername; }

    public String getClaimDescription() { return claimDescription; }
    public void setClaimDescription(String claimDescription) { this.claimDescription = claimDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDateSubmitted() { return dateSubmitted; }
    public void setDateSubmitted(LocalDateTime dateSubmitted) { this.dateSubmitted = dateSubmitted; }

    public String getApproverUsername() { return approverUsername; }
    public void setApproverUsername(String approverUsername) { this.approverUsername = approverUsername; }

    public LocalDateTime getDateApproved() { return dateApproved; }
    public void setDateApproved(LocalDateTime dateApproved) { this.dateApproved = dateApproved; }
}
