
package com.amalitech.hospitalmanagementsystem.model;

import java.time.LocalDateTime;

public class InventoryItem {
    private Long itemId;
    private String name;
    private String category;
    private Integer quantity;
    private String unit;
    private Integer reorderLevel;
    private LocalDateTime updatedAt;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Integer getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void validate() {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (unit == null || unit.isBlank()) throw new IllegalArgumentException("Unit required");
        if (quantity == null || quantity < 0) throw new IllegalArgumentException("Quantity >= 0 required");
        if (reorderLevel == null || reorderLevel < 0) throw new IllegalArgumentException("Reorder level >= 0 required");
    }
}
