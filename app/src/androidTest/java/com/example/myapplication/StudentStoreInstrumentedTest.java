package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class StudentStoreInstrumentedTest {
    @Test
    public void reloadsSavedStudentWithWorkingCredentials() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String name = "student_store_test_" + UUID.randomUUID();
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        try {
            StudentStore store = new StudentStore(preferences);
            Student student = new Student("2026-101", "Ella", "", "Ng", "Computer Science",
                    "Systems", "1st Year", "Computing", "CS-1A", "ella.ng");
            String password = "study plan 7";
            store.save(store.load().withSaved(-1, student, PasswordCredential.create(password)));

            LocalAccounts reloaded = new StudentStore(preferences).load();
            int index = reloaded.findNumber("2026-101");
            Student restored = reloaded.entry(index).student;
            assertEquals("Ella Ng", restored.fullName());
            assertEquals("Computer Science", restored.program);
            assertEquals("Systems", restored.specialization);
            assertEquals("1st Year", restored.yearLevel);
            assertEquals(index, reloaded.authenticate("ELLA.NG", password));
            assertEquals(-1, reloaded.authenticate("ella.ng", "other password"));
        } finally {
            context.deleteSharedPreferences(name);
        }
    }
}
