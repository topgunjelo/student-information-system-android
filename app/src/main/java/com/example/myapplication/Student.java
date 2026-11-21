package com.example.myapplication;

import java.io.Serializable;
import java.util.Locale;
import java.util.StringJoiner;

final class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    final String number;
    final String firstName;
    final String middleName;
    final String lastName;
    final String program;
    final String specialization;
    final String yearLevel;
    final String department;
    final String section;
    final String username;

    Student(String number, String firstName, String middleName, String lastName,
            String program, String specialization, String yearLevel, String department,
            String section, String username) {
        this.number = number.trim();
        this.firstName = firstName.trim();
        this.middleName = middleName.trim();
        this.lastName = lastName.trim();
        this.program = program.trim();
        this.specialization = specialization.trim();
        this.yearLevel = yearLevel;
        this.department = department;
        this.section = section;
        this.username = username.trim();
    }

    String fullName() {
        StringJoiner name = new StringJoiner(" ");
        name.add(firstName);
        if (!middleName.isEmpty()) {
            name.add(middleName);
        }
        return name.add(lastName).toString();
    }

    String initials() {
        return (initial(firstName) + initial(lastName)).toUpperCase(Locale.ROOT);
    }

    private static String initial(String name) {
        return name.isEmpty() ? "" : name.substring(0, name.offsetByCodePoints(0, 1));
    }

    String displaySpecialization() {
        return specialization.isEmpty() ? "N/A" : specialization;
    }

    boolean matches(String query) {
        String record = (fullName() + " " + number + " " + program + " " + specialization
                + " " + yearLevel).toLowerCase(Locale.ROOT);
        for (String term : query.trim().toLowerCase(Locale.ROOT).split("\\s+")) {
            if (!record.contains(term)) {
                return false;
            }
        }
        return true;
    }
}
