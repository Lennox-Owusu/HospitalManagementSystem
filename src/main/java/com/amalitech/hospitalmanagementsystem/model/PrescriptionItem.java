
package com.amalitech.hospitalmanagementsystem.model;

import java.util.Objects;

public class PrescriptionItem {
    private Long itemId;
    private Long prescriptionId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private String instructions;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public void validate() {
        if (prescriptionId == null || prescriptionId <= 0) throw new IllegalArgumentException("Prescription required.");
        if (medicationName == null || medicationName.isBlank()) throw new IllegalArgumentException("Medication name required.");
        if (dosage == null || dosage.isBlank()) throw new IllegalArgumentException("Dosage required.");
        if (frequency == null || frequency.isBlank()) throw new IllegalArgumentException("Frequency required.");
        if (durationDays == null || durationDays <= 0) throw new IllegalArgumentException("Duration days must be > 0.");
        if (instructions != null && instructions.length() > 500)
            throw new IllegalArgumentException("Instructions must be <= 500 chars.");
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrescriptionItem)) return false;
        PrescriptionItem that = (PrescriptionItem) o;
        return Objects.equals(itemId, that.itemId);
    }
    @Override public int hashCode() { return Objects.hash(itemId); }
}
