
package com.amalitech.hospitalmanagementsystem.service.impl;

import com.amalitech.hospitalmanagementsystem.dao.PrescriptionItemDao;
import com.amalitech.hospitalmanagementsystem.dao.impl.PrescriptionItemDaoImpl;
import com.amalitech.hospitalmanagementsystem.model.PrescriptionItem;
import com.amalitech.hospitalmanagementsystem.service.PrescriptionItemService;

import java.util.List;
import java.util.Optional;

public class PrescriptionItemServiceImpl implements PrescriptionItemService {
    private final PrescriptionItemDao dao;
    public PrescriptionItemServiceImpl() { this.dao = new PrescriptionItemDaoImpl(); }

    @Override public Long create(PrescriptionItem item) { item.validate(); return dao.create(item); }
    @Override public boolean update(PrescriptionItem item) {
        if (item.getItemId() == null) throw new IllegalArgumentException("Item ID required");
        item.validate();
        return dao.update(item);
    }
    @Override public boolean remove(Long itemId) {
        if (itemId == null || itemId <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.deleteById(itemId);
    }
    @Override public Optional<PrescriptionItem> getById(Long itemId) {
        if (itemId == null || itemId <= 0) throw new IllegalArgumentException("Valid ID required");
        return dao.findById(itemId);
    }
    @Override public List<PrescriptionItem> findByPrescription(Long prescriptionId) {
        return dao.findByPrescription(prescriptionId);
    }
    @Override public void removeAllForPrescription(Long prescriptionId) {
        dao.deleteByPrescription(prescriptionId);
    }
}
