package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

final class LocalAccounts {
    static final String SAMPLE_PASSWORD = "student2026";

    static final class Entry {
        final Student student;
        final String credential;

        Entry(Student student, String credential) {
            this.student = student;
            this.credential = credential;
        }
    }

    private final List<Entry> entries;

    LocalAccounts(List<Entry> entries) {
        this.entries = new ArrayList<>(entries);
    }

    static LocalAccounts samples() {
        List<Entry> entries = new ArrayList<>();
        for (Student student : SampleStudents.create()) {
            entries.add(new Entry(student, ""));
        }
        return new LocalAccounts(entries);
    }

    int size() {
        return entries.size();
    }

    Entry entry(int index) {
        return entries.get(index);
    }

    boolean sampleAccessAvailable() {
        return !entries.isEmpty() && entry(0).credential.isEmpty();
    }

    int authenticate(String username, String password) {
        int index = findUsername(username);
        if (index < 0) {
            return -1;
        }
        boolean valid = index == 0 && sampleAccessAvailable()
                ? SAMPLE_PASSWORD.equals(password)
                : PasswordCredential.matches(entry(index).credential, password);
        return valid ? index : -1;
    }

    int findUsername(String username) {
        for (int i = 0; i < size(); i++) {
            if (entry(i).student.username.equalsIgnoreCase(username)) {
                return i;
            }
        }
        return -1;
    }

    int findNumber(String number) {
        for (int i = 0; i < size(); i++) {
            if (entry(i).student.number.equalsIgnoreCase(number)) {
                return i;
            }
        }
        return -1;
    }

    LocalAccounts withSaved(int index, Student student, String credential) {
        List<Entry> updated = new ArrayList<>(entries);
        if (index < 0) {
            updated.add(new Entry(student, credential));
        } else {
            String retained = credential == null ? entry(index).credential : credential;
            updated.set(index, new Entry(student, retained));
        }
        return new LocalAccounts(updated);
    }
}
