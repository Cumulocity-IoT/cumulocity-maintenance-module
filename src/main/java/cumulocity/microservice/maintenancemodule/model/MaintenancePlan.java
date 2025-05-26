package cumulocity.microservice.maintenancemodule.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Complete maintenance plan with all fields including generated ID
 * 
 * @author APES
 */
public class MaintenancePlan {
    private Integer id;
    private String name;
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime endDate;
    
    private Boolean active;
    private List<MaintenanceTrigger> on;

    // Default constructor
    public MaintenancePlan() {}

    // Constructor with all fields
    public MaintenancePlan(Integer id, String name, String description, LocalDateTime startDate, 
                          LocalDateTime endDate, Boolean active, List<MaintenanceTrigger> on) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.on = on;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<MaintenanceTrigger> getOn() {
        return on;
    }

    public void setOn(List<MaintenanceTrigger> on) {
        this.on = on;
    }

    @Override
    public String toString() {
        return "MaintenancePlan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", active=" + active +
                ", on=" + on +
                '}';
    }
}
