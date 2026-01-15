
package com.amalitech.hospitalmanagementsystem.nosql;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.IndexOptions;
import java.util.stream.StreamSupport;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;

public class PatientNoteRepository {

    private final MongoCollection<Document> notes;

    public PatientNoteRepository() {
        MongoDatabase db = MongoConnectionUtil.db();
        this.notes = db.getCollection("patient_notes");
        ensureIndexes();
    }


    private void ensureIndexes() {
        // --- 1) TEXT INDEX on "content" (for full‑text search) ---
        MongoIterable<Document> existing = notes.listIndexes();

        boolean hasContentText = StreamSupport.stream(existing.spliterator(), false)
                .anyMatch(doc -> {
                    // For text indexes, the "key" is {_fts:"text", _ftsx:1}, and the "weights" doc lists fields.
                    Document weights = doc.get("weights", Document.class);
                    return weights != null && weights.containsKey("content");
                });

        if (!hasContentText) {
            try {
                notes.createIndex(
                        Indexes.text("content"),
                        new IndexOptions().name("content_text")   // stable name
                );
            } catch (MongoCommandException e) {
                if (e.getCode() == 85) {
                    // Same index already exists but with a different name — ignore.
                    System.err.println("[PatientNoteRepository] text index already exists (different name). Skipping.");
                } else {
                    throw e;
                }
            }
        }

        //COMPOUND INDEX on (patientId ASC, createdAt DESC) for fast listing

        existing = notes.listIndexes();

        boolean hasPatientCreatedAt = StreamSupport.stream(existing.spliterator(), false)
                .anyMatch(doc -> {
                    Document key = doc.get("key", Document.class);
                    return key != null
                            && Integer.valueOf(1).equals(key.get("patientId"))   // ASC
                            && Integer.valueOf(-1).equals(key.get("createdAt")); // DESC
                });

        if (!hasPatientCreatedAt) {
            try {
                notes.createIndex(
                        Indexes.compoundIndex(Indexes.ascending("patientId"), Indexes.descending("createdAt")),
                        new IndexOptions().name("patientId_1_createdAt_-1")
                );
            } catch (MongoCommandException e) {
                if (e.getCode() == 85) {
                    System.err.println("[PatientNoteRepository] compound index already exists (different name). Skipping.");
                } else {
                    throw e;
                }
            }
        }
    }


    private Document toDoc(PatientNote n) {
        return new Document()
                .append("_id", n.getId() == null ? new ObjectId() : n.getId())
                .append("patientId", n.getPatientId())
                .append("doctorId", n.getDoctorId())
                .append("noteType", n.getNoteType())
                .append("content", n.getContent())
                .append("tags", n.getTags())
                .append("createdAt", n.getCreatedAt() == null ? LocalDateTime.now().toString()
                        : n.getCreatedAt().toString());
    }

    private PatientNote fromDoc(Document d) {
        PatientNote n = new PatientNote();
        n.setId(d.getObjectId("_id"));
        n.setPatientId(d.getLong("patientId"));
        n.setDoctorId(d.getLong("doctorId"));
        n.setNoteType(d.getString("noteType"));
        n.setContent(d.getString("content"));
        n.setTags((List<String>) d.get("tags"));
        String ts = d.getString("createdAt");
        n.setCreatedAt(ts == null ? null : LocalDateTime.parse(ts));
        return n;
    }

    public ObjectId create(PatientNote note) {
        if (note.getCreatedAt() == null) note.setCreatedAt(LocalDateTime.now());
        Document d = toDoc(note);
        notes.insertOne(d);
        return d.getObjectId("_id");
    }

    public List<PatientNote> findByPatient(Long patientId) {
        List<PatientNote> out = new ArrayList<>();
        for (Document d : notes.find(eq("patientId", patientId)).sort(descending("createdAt"))) {
            out.add(fromDoc(d));
        }
        return out;
    }

    /** Full‑text search (requires the text index on "content"). */
    public List<PatientNote> searchByText(String query) {
        List<PatientNote> out = new ArrayList<>();
        if (query == null || query.isBlank()) return out;

        // Use Filters.text helper per driver docs. [2](https://www.mongodb.com/docs/drivers/java/sync/current/crud/query-documents/text/)
        for (Document d : notes.find(text(query)).sort(descending("createdAt"))) {
            out.add(fromDoc(d));
        }
        return out;
    }

    public boolean delete(ObjectId id) {
        return notes.deleteOne(eq("_id", id)).getDeletedCount() > 0;
    }
}
