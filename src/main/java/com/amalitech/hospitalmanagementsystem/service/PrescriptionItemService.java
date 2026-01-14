
package com.amalitech.hospitalmanagementsystem.service;

import com.amalitech.hospitalmanagementsystem.model.PrescriptionItem;
import java.util.List;
import java.util.Optional;

public interface PrescriptionItemService {
    Long create(PrescriptionItem item);
    boolean update(PrescriptionItem item);
    boolean remove(Long itemId);
    Optional<PrescriptionItem> getById(Long itemId);
    List<PrescriptionItem> findByPrescription(Long prescriptionId);
    boolean removeAllForPrescription(Long prescriptionId);
}
