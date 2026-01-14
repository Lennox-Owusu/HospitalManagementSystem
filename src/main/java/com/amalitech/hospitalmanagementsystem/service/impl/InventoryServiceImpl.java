
package com.amalitech.hospitalmanagementsystem.service.impl;

import com.amalitech.hospitalmanagementsystem.dao.InventoryDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.InventoryDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.InventoryItem;
import com.amalitech.hospitalmanagementsystem.service.InventoryService;

import java.util.List;
import java.util.Optional;

public class InventoryServiceImpl implements InventoryService {
    private final InventoryDao dao;
    public InventoryServiceImpl() { this.dao = new InventoryDaoImpl(); }
    public InventoryServiceImpl(InventoryDao dao) { this.dao = dao; }

    @Override public Long create(InventoryItem i) { i.validate(); return dao.create(i); }
    @Override public boolean update(InventoryItem i) {
        if (i.getItemId() == null) throw new IllegalArgumentException("ID required");
        i.validate();
        return dao.update(i);
    }
    @Override public boolean remove(Long itemId) {
        if (itemId == null || itemId <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.deleteById(itemId);
    }
    @Override public Optional<InventoryItem> getById(Long itemId) {
        if (itemId == null || itemId <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.findById(itemId);
    }
    @Override public List<InventoryItem> getAll() { return dao.findAll(); }
    @Override public List<InventoryItem> search(String term) { return dao.searchByNameOrCategory(term); }
    @Override public boolean adjustQuantity(Long itemId, int delta) { return dao.adjustQuantity(itemId, delta); }
}
