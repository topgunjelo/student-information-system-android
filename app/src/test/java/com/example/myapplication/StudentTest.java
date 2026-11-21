package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StudentTest {
    private final Student student = new Student("2025-001234", " Adrian ", "  ", " Santos ",
            "Computer Science", " ", "2nd Year", "Computing", "CS-2A", "adrian.santos");

    @Test
    public void formatsNamesAndMissingSpecialization() {
        assertEquals("Adrian Santos", student.fullName());
        assertEquals("AS", student.initials());
        assertEquals("N/A", student.displaySpecialization());
    }

    @Test
    public void searchesAcrossNameAndAcademicDetails() {
        assertTrue(student.matches("  SANTOS computer  "));
        assertTrue(student.matches("2025-001234 2nd"));
        assertFalse(student.matches("Santos nursing"));
    }
}
