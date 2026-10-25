package com.dms.model;

import com.dms.common.Auditable;
import com.dms.exception.InsufficientResourceException;
import com.dms.model.enums.ResourceType;
import com.dms.model.enums.ResourceUnit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entity representing relief supply items (e.g. food, water, medical supplies, blankets).
 * Demonstrates OOP Encapsulation with synchronized stock manipulation and custom exception handling.
 */
@Entity
@Table(name = "resources")
public class Resource implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Resource name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Resource type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResourceType type;

    @NotNull
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity = 0;

    @NotNull(message = "Unit of measurement is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResourceUnit unit;

    @NotBlank(message = "Storage location is required")
    @Column(name = "storage_location", nullable = false, length = 150)
    private String storageLocation;

    @Column(name = "warehouse_code", length = 50)
    private String warehouseCode;

    @NotNull
    @Column(name = "min_threshold", nullable = false)
    private Integer minThreshold = 50;

    @Column(nullable = false, length = 30)
    private String status = "IN_STOCK"; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Resource() {
    }

    public Resource(String name, ResourceType type, Integer quantity, ResourceUnit unit,
                    String storageLocation, String warehouseCode, Integer minThreshold) {
        this.name = name;
        this.type = type;
        this.quantity = quantity != null ? quantity : 0;
        this.unit = unit;
        this.storageLocation = storageLocation;
        this.warehouseCode = warehouseCode;
        this.minThreshold = minThreshold != null ? minThreshold : 50;
        updateStatus();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    // OOP Domain Logic & Invariant Protection
    public synchronized void deductStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to deduct must be positive.");
        }
        if (amount > this.quantity) {
            throw new InsufficientResourceException(this.id, amount, this.quantity);
        }
        this.quantity -= amount;
        updateStatus();
    }

    public synchronized void addStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount to add must be positive.");
        }
        this.quantity += amount;
        updateStatus();
    }

    public void updateStatus() {
        if (this.quantity == null || this.quantity <= 0) {
            this.status = "OUT_OF_STOCK";
        } else if (this.quantity <= this.minThreshold) {
            this.status = "LOW_STOCK";
        } else {
            this.status = "IN_STOCK";
        }
    }

    public boolean isLowStock() {
        return this.quantity != null && this.quantity <= this.minThreshold;
    }

    // Encapsulation: Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        updateStatus();
    }

    public ResourceUnit getUnit() {
        return unit;
    }

    public void setUnit(ResourceUnit unit) {
        this.unit = unit;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Integer getMinThreshold() {
        return minThreshold;
    }

    public void setMinThreshold(Integer minThreshold) {
        this.minThreshold = minThreshold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
