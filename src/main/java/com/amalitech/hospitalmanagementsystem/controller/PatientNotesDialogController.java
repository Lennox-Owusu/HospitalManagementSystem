
package com.amalitech.hospitalmanagementsystem.controller;

import com.amalitech.hospitalmanagementsystem.nosql.PatientNote;
import com.amalitech.hospitalmanagementsystem.nosql.PatientNoteService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.bson.types.ObjectId;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PatientNotesDialogController {

    @FXML private TextField searchField, typeField, tagsField;
    @FXML private TextArea contentArea;
    @FXML private TableView<PatientNote> table;
    @FXML private TableColumn<PatientNote, String> colCreated, colType, colContent, colDoctor, colTags;

    private PatientNoteService service;

    private final ObservableList<PatientNote> data = FXCollections.observableArrayList();
    private Long patientId; // set by caller

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private PatientNoteService service() {
        if (service == null) {
            try {
                service = new PatientNoteService();
            } catch (Exception ex) {
                // If creation fails, keep service null and surface a friendly message when used
                System.err.println("[NotesDialog] Failed to create PatientNoteService: " + ex.getMessage());
                ex.printStackTrace();
                showError("Notes service failed to initialize.\n" + ex.getMessage());
            }
        }
        return service;
    }

    @FXML
    public void initialize() {
        System.out.println("[NotesDialog] initialize() start");

        colCreated.setCellValueFactory(c ->
                Bindings.createStringBinding(
                        () -> c.getValue().getCreatedAt() == null
                                ? ""
                                : TS_FMT.format(c.getValue().getCreatedAt())
                )
        );
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNoteType()));
        colContent.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getContent()));
        colDoctor.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDoctorId() == null ? "" : String.valueOf(c.getValue().getDoctorId())
        ));
        colTags.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTags() == null ? "" : String.join(",", c.getValue().getTags())
        ));
        table.setItems(data);

        System.out.println("[NotesDialog] initialize() done");
    }

    public void setPatientId(Long patientId) {
        System.out.println("[NotesDialog] setPatientId = " + patientId);
        this.patientId = patientId;
        reload();
    }

    @FXML
    private void onRefresh() {
        reload();
    }

    @FXML
    private void onSearch() {
        String q = searchField.getText();
        if (q == null || q.isBlank()) {
            reload();
            return;
        }
        try {
            var svc = service();
            if (svc == null) return; // already alerted in service()
            data.setAll(svc.search(q)); // Mongo text search
        } catch (Exception ex) {
            handleDataError("Search failed", ex);
        }
    }

    public PatientNote collectToCreate() {
        String type = typeField.getText();
        String content = contentArea.getText();
        List<String> tags = null;
        String rawTags = tagsField.getText();
        if (rawTags != null && !rawTags.isBlank()) {
            tags = Arrays.stream(rawTags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
        }
        PatientNote n = new PatientNote();
        n.setPatientId(patientId);
        n.setNoteType(type == null || type.isBlank() ? "General" : type.trim());
        n.setContent(content == null ? "" : content.trim());
        n.setTags(tags);
        return n;
    }

    /** Called by the Dialog after OK / Delete button hit */
    public Result handleDialogResult(ButtonType type) {
        try {
            if (type.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                PatientNote n = collectToCreate();
                var svc = service();
                if (svc == null) return Result.IGNORED;
                svc.addNote(n);
                reload();
                clearForm();
                return Result.RELOADED;

            } else if (type.getButtonData() == ButtonBar.ButtonData.OTHER) { // Delete Selected
                PatientNote sel = table.getSelectionModel().getSelectedItem();
                if (sel == null) return Result.IGNORED;
                ObjectId id = sel.getId();
                if (id != null) {
                    var svc = service();
                    if (svc == null) return Result.IGNORED;
                    svc.delete(id);
                }
                reload();
                return Result.RELOADED;
            }
            return Result.CLOSED;

        } catch (Exception ex) {
            handleDataError("Operation failed", ex);
            return Result.IGNORED;
        }
    }

    private void reload() {
        try {
            System.out.println("[NotesDialog] reload() for patientId = " + patientId);
            var svc = service();
            if (svc == null) return; // already alerted
            var list = svc.getNotesForPatient(patientId);
            System.out.println("[NotesDialog] reload -> " + (list == null ? "null" : list.size()) + " notes");
            data.setAll(list);
        } catch (Exception ex) {
            handleDataError("Failed to load notes", ex);
        }
    }

    private void clearForm() {
        typeField.clear();
        tagsField.clear();
        contentArea.clear();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Patient Notes");
        a.show();
    }

    private void handleDataError(String title, Exception ex) {
        System.err.println("[NotesDialog] " + title + ": " + ex.getMessage());
        ex.printStackTrace();
        showError(title + ":\n" + ex.getClass().getSimpleName() + " — " + (ex.getMessage() == null ? "(no message)" : ex.getMessage()));
    }

    public enum Result { RELOADED, CLOSED, IGNORED }
}
