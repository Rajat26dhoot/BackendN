package com.example.notes.service;

import com.example.notes.model.Note;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotesService {
    private static final String NOTES_FILE = "notes.json";
    private final ObjectMapper objectMapper;

    public NotesService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<Note> getAllNotes() {
        try {
            File file = new File(NOTES_FILE);
            if (file.exists()) {
                return objectMapper.readValue(file, new TypeReference<List<Note>>() {});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Note createNote(Note note) {
        List<Note> notes = getAllNotes();
        notes.add(note);
        saveNotes(notes);
        return note;
    }

    public Optional<Note> getNoteById(String id) {
        return getAllNotes().stream()
                .filter(note -> note.getId().equals(id))
                .findFirst();
    }

    public Optional<Note> updateNote(String id, Note updatedNote) {
        List<Note> notes = getAllNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(id)) {
                Note existingNote = notes.get(i);
                if (updatedNote.getTitle() != null) {
                    existingNote.setTitle(updatedNote.getTitle());
                }
                if (updatedNote.getContent() != null) {
                    existingNote.setContent(updatedNote.getContent());
                }
                existingNote.updateTimestamp();
                notes.set(i, existingNote);
                saveNotes(notes);
                return Optional.of(existingNote);
            }
        }
        return Optional.empty();
    }

    public boolean deleteNote(String id) {
        List<Note> notes = getAllNotes();
        boolean removed = notes.removeIf(note -> note.getId().equals(id));
        if (removed) {
            saveNotes(notes);
        }
        return removed;
    }

    private void saveNotes(List<Note> notes) {
        try {
            objectMapper.writeValue(new File(NOTES_FILE), notes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}