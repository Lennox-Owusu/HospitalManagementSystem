
package com.amalitech.hospitalmanagementsystem.dao;

import com.amalitech.hospitalmanagementsystem.model.InventoryItem;
import java.util.List;
import java.util.Optional;

public interface InventoryDao {
    Long create(InventoryItem i);
    boolean update(InventoryItem i);
    boolean deleteById(Long itemId);
    Optional<InventoryItem> findById(Long itemId);
    List<InventoryItem> findAll();
    List<InventoryItem> searchByNameOrCategory(String term);
    boolean adjustQuantity(Long itemId, int delta); // +/- stock movements
}
