package Backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 100)
    private String name;

    @NotBlank
    @Column(length = 500, name = "description")
    private String description;

    @NotBlank
    @Column(length = 10)
    private String status; // Lost or Found

    @NotBlank
    private String reportedBy; // Username of reporter

    @Column(length = 255)
    private String image; // Path to image

    @NotNull
    private Long userId; // User who reported the item

    @NotNull
    private LocalDateTime dateReported = LocalDateTime.now();

    // Constructors
    public Item() {}



    public Item(String name, String description, String status, String reportedBy, String image) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.reportedBy = reportedBy;
        this.image = image;
        this.dateReported = LocalDateTime.now(); // Ensure date is properly initialized
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }



    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    // Backward compatibility alias
    public String getDesc() { return description; }
    public void setDesc(String desc) { this.description = desc; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReportedBy() { return reportedBy; }
    public void setReportedBy(String reportedBy) { this.reportedBy = reportedBy; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getDateReported() { return dateReported; }
    public void setDateReported(LocalDateTime dateReported) { this.dateReported = dateReported; }
}
