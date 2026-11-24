package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class LocalAccountsTest {
    @Test
    public void verifiesDemoAndRejectsWrongCredentials() {
        LocalAccounts accounts = LocalAccounts.samples();
        assertEquals(0, accounts.authenticate("ADRIAN.SANTOS", LocalAccounts.SAMPLE_PASSWORD));
        assertEquals(-1, accounts.authenticate("adrian.santos", "wrong password"));
        assertEquals(-1, accounts.authenticate("mikaela.villanueva", LocalAccounts.SAMPLE_PASSWORD));
    }

    @Test
    public void savedAccountSignsInAndAnEditKeepsItsPassword() {
        String password = "study plan 7";
        Student student = student("2026-101", "ella.ng");
        LocalAccounts accounts = LocalAccounts.samples().withSaved(-1, student, PasswordCredential.create(password));
        int index = accounts.findUsername("ella.ng");
        assertNotEquals(-1, index);
        assertEquals(index, accounts.authenticate("ELLA.NG", password));

        Student edited = student("2026-102", "ella.new");
        accounts = accounts.withSaved(index, edited, null);
        assertEquals(-1, accounts.authenticate("ella.ng", password));
        assertEquals(index, accounts.authenticate("ella.new", password));
        assertEquals("2026-102", accounts.entry(index).student.number);
    }

    @Test
    public void passwordChangeReplacesSampleCredentials() {
        LocalAccounts accounts = LocalAccounts.samples();
        String replacement = "my new password";
        accounts = accounts.withSaved(0, accounts.entry(0).student, PasswordCredential.create(replacement));
        assertEquals(-1, accounts.authenticate("adrian.santos", LocalAccounts.SAMPLE_PASSWORD));
        assertEquals(0, accounts.authenticate("adrian.santos", replacement));
        assertNotEquals(PasswordCredential.create(replacement), PasswordCredential.create(replacement));
    }

    private Student student(String number, String username) {
        return new Student(number, "Ella", "", "Ng", "Computer Science", "", "1st Year",
                "Computing", "", username);
    }
}
