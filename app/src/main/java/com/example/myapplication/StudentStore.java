package com.example.myapplication;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final class StudentStore {
    private static final String RECORDS = "student_accounts_v1";
    private final SharedPreferences preferences;

    StudentStore(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    LocalAccounts load() throws JSONException {
        String saved = preferences.getString(RECORDS, null);
        if (saved == null) {
            return LocalAccounts.samples();
        }

        JSONArray data = new JSONArray(saved);
        if (data.length() == 0) {
            throw new JSONException("No student accounts in storage");
        }
        List<LocalAccounts.Entry> entries = new ArrayList<>(data.length());
        for (int i = 0; i < data.length(); i++) {
            JSONObject row = data.getJSONObject(i);
            Student student = new Student(row.getString("number"), row.getString("first"),
                    row.getString("middle"), row.getString("last"), row.getString("program"),
                    row.getString("specialization"), row.getString("year"), row.getString("department"),
                    row.getString("section"), row.getString("username"));
            entries.add(new LocalAccounts.Entry(student, row.getString("credential")));
        }
        return new LocalAccounts(entries);
    }

    void save(LocalAccounts accounts) throws JSONException {
        JSONArray data = new JSONArray();
        for (int i = 0; i < accounts.size(); i++) {
            LocalAccounts.Entry entry = accounts.entry(i);
            Student student = entry.student;
            data.put(new JSONObject()
                    .put("number", student.number)
                    .put("first", student.firstName)
                    .put("middle", student.middleName)
                    .put("last", student.lastName)
                    .put("program", student.program)
                    .put("specialization", student.specialization)
                    .put("year", student.yearLevel)
                    .put("department", student.department)
                    .put("section", student.section)
                    .put("username", student.username)
                    .put("credential", entry.credential));
        }
        preferences.edit().putString(RECORDS, data.toString()).apply();
    }
}
