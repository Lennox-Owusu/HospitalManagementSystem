
package com.amalitech.hospitalmanagementsystem.service;

import com.amalitech.hospitalmanagementsystem.model.InventoryItem;
import java.util.List;
import java.util.Optional;

public interface InventoryService {
    Long create(InventoryItem i);
    boolean update(InventoryItem i);
    boolean remove(Long itemId);
    Optional<InventoryItem> getById(Long itemId);
    List<InventoryItem> getAll();
    List<InventoryItem> search(String term);
    boolean adjustQuantity(Long itemId, int delta);
}
