package com.example.myapplication;

import java.util.ArrayList;
import java.util.Arrays;

final class SampleStudents {
    private SampleStudents() {}

    static ArrayList<Student> create() {
        return new ArrayList<>(Arrays.asList(
                new Student("2025-001234", "Adrian", "Reyes", "Santos",
                        "Bachelor of Science in Computer Science", "Machine Learning", "2nd Year",
                        "College of Computing Studies", "CS-2A", "adrian.santos"),
                new Student("2024-000812", "Mikaela", "Cruz", "Villanueva",
                        "Bachelor of Science in Information Technology", "Web Development", "3rd Year",
                        "College of Computing Studies", "IT-3B", "mikaela.villanueva"),
                new Student("2026-001067", "Elijah", "Tan", "Flores",
                        "Bachelor of Science in Information Systems", "", "1st Year",
                        "College of Computing Studies", "IS-1A", "elijah.flores"),
                new Student("2023-000946", "Sofia", "Garcia", "Dela Cruz",
                        "Bachelor of Science in Business Administration", "Marketing Management", "4th Year",
                        "College of Business", "BA-4A", "sofia.delacruz")));
    }
}
