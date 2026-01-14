
package com.amalitech.hospitalmanagementsystem.dao;

import com.amalitech.hospitalmanagementsystem.model.PrescriptionItem;
import java.util.List;
import java.util.Optional;

public interface PrescriptionItemDao {
    Long create(PrescriptionItem item);
    boolean update(PrescriptionItem item);
    boolean deleteById(Long itemId);
    Optional<PrescriptionItem> findById(Long itemId);
    List<PrescriptionItem> findByPrescription(Long prescriptionId);
    boolean deleteByPrescription(Long prescriptionId);
}
