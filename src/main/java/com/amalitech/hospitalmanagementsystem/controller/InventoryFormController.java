
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.InventoryItem;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class InventoryFormController {
    @FXML private TextField nameField, categoryField, quantityField, unitField, reorderField;

    private InventoryItem existing;

    public void setExisting(InventoryItem i) {
        this.existing = i;
        if (i != null) {
            nameField.setText(i.getName());
            categoryField.setText(i.getCategory());
            quantityField.setText(String.valueOf(i.getQuantity()));
            unitField.setText(i.getUnit());
            reorderField.setText(String.valueOf(i.getReorderLevel()));
        }
    }

    public InventoryItem collectResult() {
        String name = val(nameField.getText(), "Name is required");
        String unit = val(unitField.getText(), "Unit is required");
        int qty = parseInt(quantityField.getText(), "Quantity must be a non-negative integer", 0);
        int reorder = parseInt(reorderField.getText(), "Reorder level must be a non-negative integer", 0);
        String cat = safe(categoryField.getText());

        InventoryItem out = new InventoryItem();
        out.setName(name);
        out.setUnit(unit);
        out.setQuantity(qty);
        out.setReorderLevel(reorder);
        out.setCategory(cat);
        if (existing != null) out.setItemId(existing.getItemId());
        return out;
    }

    private String val(String s, String msg) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(msg);
        return s.trim();
    }
    private String safe(String s) { return s == null ? "" : s.trim(); }
    private int parseInt(String s, String msg, int min) {
        try {
            int v = Integer.parseInt(val(s, msg));
            if (v < min) throw new RuntimeException();
            return v;
        } catch (Exception ex) {
            throw new IllegalArgumentException(msg);
        }
    }
}
