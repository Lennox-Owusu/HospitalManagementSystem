
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.InventoryItem;
import com.amalitech.hospitalmanagementsystem.service.InventoryService;
import com.amalitech.hospitalmanagementsystem.service.impl.InventoryServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class InventoryTableController {
    @FXML private TableView<InventoryItem> table;
    @FXML private TableColumn<InventoryItem, Long> colId;
    @FXML private TableColumn<InventoryItem, String> colName, colCategory, colUnit;
    @FXML private TableColumn<InventoryItem, Integer> colQty, colReorder;
    @FXML private TableColumn<InventoryItem, java.time.LocalDateTime> colUpdated;
    @FXML private TextField searchField, deltaField;

    private final InventoryService service = new InventoryServiceImpl();
    private final ObservableList<InventoryItem> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colReorder.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));
        colUpdated.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));

        colUpdated.setCellFactory(col -> new TableCell<>() {
            final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override protected void updateItem(java.time.LocalDateTime dt, boolean empty) {
                super.updateItem(dt, empty);
                setText(empty || dt == null ? null : fmt.format(dt));
            }
        });

        table.setItems(data);
        loadAll();

        // Visual hint for low stock
        table.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    if (item.getQuantity() <= item.getReorderLevel()) {
                        setStyle("-fx-background-color: rgba(255,0,0,0.08);");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void loadAll() { data.setAll(service.getAll()); }

    @FXML private void onRefresh() { loadAll(); }

    @FXML private void onSearch() {
        String term = searchField.getText();
        data.setAll(service.search(term));
    }

    @FXML private void onAdd() {
        Optional<InventoryItem> res = showFormDialog(null);
        res.ifPresent(i -> {
            try { service.create(i); loadAll(); }
            catch (RuntimeException ex) { showAlert(Alert.AlertType.ERROR, "Create failed", ex.getMessage()); }
        });
    }

    @FXML private void onEdit() {
        InventoryItem selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No selection", "Select an item"); return; }
        Optional<InventoryItem> res = showFormDialog(selected);
        res.ifPresent(i -> {
            i.setItemId(selected.getItemId());
            boolean ok = service.update(i);
            if (!ok) showAlert(Alert.AlertType.ERROR, "Update failed", "Could not update item");
            loadAll();
        });
    }

    @FXML private void onDelete() {
        InventoryItem selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No selection", "Select an item"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected item?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                boolean ok = service.remove(selected.getItemId());
                if (!ok) showAlert(Alert.AlertType.ERROR, "Delete failed", "Could not delete");
                loadAll();
            }
        });
    }

    @FXML private void onAdjustQuantity() {
        InventoryItem selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No selection", "Select an item"); return; }
        String deltaTxt = deltaField.getText();
        if (deltaTxt == null || deltaTxt.isBlank()) { showAlert(Alert.AlertType.WARNING, "No delta", "Enter +N or -N"); return; }
        int delta;
        try { delta = Integer.parseInt(deltaTxt.trim()); }
        catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Invalid number", "Use integers like +10 or -5"); return; }

        boolean ok = service.adjustQuantity(selected.getItemId(), delta);
        if (!ok) showAlert(Alert.AlertType.ERROR, "Adjust failed", "Could not adjust quantity");
        loadAll();
        deltaField.clear();
    }

    private Optional<InventoryItem> showFormDialog(InventoryItem existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/inventory_form.fxml"));
            DialogPane pane = loader.load();
            InventoryFormController ctrl = loader.getController();
            ctrl.setExisting(existing);
            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setDialogPane(pane);
            dlg.setTitle(existing == null ? "Add Item" : "Edit Item");
            Optional<ButtonType> res = dlg.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                try { return Optional.of(ctrl.collectResult()); }
                catch (IllegalArgumentException ex) { showAlert(Alert.AlertType.ERROR, "Validation error", ex.getMessage()); }
            }
            return Optional.empty();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Dialog error", e.getMessage());
            return Optional.empty();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}

