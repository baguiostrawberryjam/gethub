package com.example.gethub.models;

import java.util.List;
import java.util.ArrayList;

public class SystemData {
    private String studentId;
    private List<String> existingDocuments;
    private List<String> pendingRequests;

    // Constructor (usually used for creation/retrieval)
    public SystemData(String studentId) {
        this.studentId = studentId;
        this.existingDocuments = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
    }

    // Getters
    public String getStudentId() { return studentId; }
    public List<String> getExistingDocuments() { return existingDocuments; }
    public List<String> getPendingRequests() { return pendingRequests; }

    // Dummy Setter (for simulating data load)
    public void addDocument(String docName) {
        this.existingDocuments.add(docName);
    }
}