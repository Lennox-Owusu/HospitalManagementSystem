
package com.amalitech.hospitalmanagementsystem.nosql;

import org.bson.types.ObjectId;

import java.util.List;

public class PatientNoteService {

    private final PatientNoteRepository repo = new PatientNoteRepository();

    public void addNote(PatientNote note) {
        validate(note, true);
        repo.create(note);
    }

    public List<PatientNote> getNotesForPatient(Long patientId) {
        return repo.findByPatient(patientId);
    }

    public List<PatientNote> search(String term) {
        return repo.searchByText(term);
    }

    public boolean delete(ObjectId id) {
        return repo.delete(id);
    }

    private void validate(PatientNote n, boolean creating) {
        if (n == null) throw new IllegalArgumentException("Note is required");
        if (n.getPatientId() == null || n.getPatientId() <= 0)
            throw new IllegalArgumentException("patientId is required");
        if (n.getContent() == null || n.getContent().isBlank())
            throw new IllegalArgumentException("content is required");
        if (n.getNoteType() == null || n.getNoteType().isBlank())
            n.setNoteType("General");
    }
}

