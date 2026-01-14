
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.model.Prescription;
import com.amalitech.hospitalmanagementsystem.model.PrescriptionItem;
import com.amalitech.hospitalmanagementsystem.service.PrescriptionItemService;
import com.amalitech.hospitalmanagementsystem.service.PrescriptionService;
import com.amalitech.hospitalmanagementsystem.service.impl.PrescriptionItemServiceImpl;
import com.amalitech.hospitalmanagementsystem.service.impl.PrescriptionServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class PrescriptionTableController {
    @FXML private TableView<Prescription> table;
    @FXML private TableColumn<Prescription, Long> colId, colPatient, colDoctor;
    @FXML private TableColumn<Prescription, java.time.LocalDateTime> colIssued;
    @FXML private TableColumn<Prescription, String> colNotes;
    @FXML private TextField searchField;

    private final PrescriptionService service = new PrescriptionServiceImpl();
    private final PrescriptionItemService itemService = new PrescriptionItemServiceImpl();
    private final ObservableList<Prescription> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("prescriptionId"));
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colIssued.setCellValueFactory(new PropertyValueFactory<>("issuedAt"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));

        colIssued.setCellFactory(col -> new TableCell<>() {
            final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override protected void updateItem(java.time.LocalDateTime dt, boolean empty) {
                super.updateItem(dt, empty);
                setText(empty || dt == null ? null : fmt.format(dt));
            }
        });

        table.setItems(data);
        loadAll();
        addContextMenu();
    }

    private void loadAll() { data.setAll(service.getAll()); }

    @FXML private void onRefresh() { loadAll(); }

    @FXML private void onSearch() {
        String t = searchField.getText();
        if (t == null || t.isBlank()) { loadAll(); return; }
        // quick filter: allow numeric search for patient/doctor IDs
        try {
            long id = Long.parseLong(t.trim());
            data.setAll(service.getById(id).map(List::of).orElseGet(List::of));
        } catch (NumberFormatException e) {
            // could extend to search by date etc.; for now, show all
            loadAll();
        }
    }

    @FXML private void onAdd() {
        Optional<Prescription> res = showFormDialog(null);
        res.ifPresent(p -> {
            try {
                Long id = service.create(p);
                // optional: prompt to add items
                askAddItems(id);
                loadAll();
            } catch (RuntimeException ex) {
                showAlert(Alert.AlertType.ERROR, "Create failed", ex.getMessage());
            }
        });
    }

    @FXML private void onEdit() {
        Prescription selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No selection", "Select a prescription"); return; }
        Optional<Prescription> res = showFormDialog(selected);
        res.ifPresent(p -> {
            p.setPrescriptionId(selected.getPrescriptionId());
            boolean ok = service.update(p);
            if (!ok) showAlert(Alert.AlertType.ERROR, "Update failed", "Could not update");
            loadAll();
        });
    }

    @FXML private void onDelete() {
        Prescription selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "No selection", "Select a prescription"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected prescription (and its items)?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                // delete items first (FK has cascade, but belt & braces)
                itemService.removeAllForPrescription(selected.getPrescriptionId());
                boolean ok = service.remove(selected.getPrescriptionId());
                if (!ok) showAlert(Alert.AlertType.ERROR, "Delete failed", "Could not delete");
                loadAll();
            }
        });
    }

    private Optional<Prescription> showFormDialog(Prescription existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/prescription_form.fxml"));
            DialogPane pane = loader.load();
            PrescriptionFormController ctrl = loader.getController();
            ctrl.setExisting(existing);
            Dialog<ButtonType> dlg = new Dialog<>();
            dlg.setDialogPane(pane);
            dlg.setTitle(existing == null ? "Add Prescription" : "Edit Prescription");
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

    private void addContextMenu() {
        MenuItem manageItems = new MenuItem("Manage Items…");
        manageItems.setOnAction(e -> {
            Prescription p = table.getSelectionModel().getSelectedItem();
            if (p == null) { showAlert(Alert.AlertType.WARNING, "No selection", "Select a prescription"); return; }
            manageItemsDialog(p.getPrescriptionId());
        });
        ContextMenu cm = new ContextMenu(manageItems);
        table.setContextMenu(cm);
    }

    private void manageItemsDialog(Long prescriptionId) {
        // simplistic dialog: add one item at a time (you can make a richer table dialog later)
        Optional<PrescriptionItem> res = showItemDialog(prescriptionId, null);
        res.ifPresent(item -> {
            try { itemService.create(item); }
            catch (RuntimeException ex) { showAlert(Alert.AlertType.ERROR, "Add item failed", ex.getMessage()); }
        });
    }

    private Optional<PrescriptionItem> showItemDialog(Long prescId, PrescriptionItem existing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/prescription_item_form.fxml"));
            DialogPane pane = loader.load();
            PrescriptionItemFormController ctrl = loader.getController();
            ctrl.setContext(prescId, existing);
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

    private void askAddItems(Long prescId) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Prescription created. Add items now?", ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null);
        a.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) manageItemsDialog(prescId);
        });
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}
