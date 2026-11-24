package com.example.myapplication;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

final class StudentForm {
    private final View page;
    private final Student original;
    private final EditText username;
    private final EditText password;
    private final EditText number;
    private final EditText last;
    private final EditText first;
    private final EditText middle;
    private final EditText program;
    private final EditText specialization;
    private final Spinner year;

    StudentForm(View page, Student original) {
        this.page = page;
        this.original = original;
        username = page.findViewById(R.id.form_username);
        password = page.findViewById(R.id.form_password);
        number = page.findViewById(R.id.form_number);
        last = page.findViewById(R.id.form_last);
        first = page.findViewById(R.id.form_first);
        middle = page.findViewById(R.id.form_middle);
        program = page.findViewById(R.id.form_program);
        specialization = page.findViewById(R.id.form_specialization);
        year = page.findViewById(R.id.form_year);
        ScreenFields.bindPasswordToggle(password, page.findViewById(R.id.form_visibility));

        String[] levels = page.getResources().getStringArray(R.array.year_levels);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(page.getContext(), R.layout.item_year, levels);
        adapter.setDropDownViewResource(R.layout.item_year);
        year.setAdapter(adapter);

        if (original == null) {
            ((TextView) page.findViewById(R.id.screen_title)).setText(R.string.add_information);
        } else {
            ((TextView) page.findViewById(R.id.form_password_label)).setText(R.string.new_password);
            password.setHint(R.string.keep_password_hint);
            username.setText(original.username);
            number.setText(original.number);
            last.setText(original.lastName);
            first.setText(original.firstName);
            middle.setText(original.middleName);
            program.setText(original.program);
            specialization.setText(original.specialization);
            year.setSelection(Math.max(0, Arrays.asList(levels).indexOf(original.yearLevel)));
        }
    }

    Student readStudent() {
        for (EditText field : new EditText[]{username, number, last, first, program}) {
            if (ScreenFields.input(field).isEmpty()) {
                showError(field, R.string.enter_required);
                return null;
            }
        }

        String enteredPassword = newPassword();
        if (original == null && enteredPassword.isEmpty()) {
            showError(password, R.string.enter_required);
            return null;
        }
        if (!enteredPassword.isEmpty() && enteredPassword.trim().length() < 8) {
            showError(password, R.string.password_length);
            return null;
        }

        String unavailable = page.getContext().getString(R.string.not_available);
        return new Student(ScreenFields.input(number), ScreenFields.input(first),
                ScreenFields.input(middle), ScreenFields.input(last), ScreenFields.input(program),
                ScreenFields.input(specialization), (String) year.getSelectedItem(),
                original == null ? unavailable : original.department,
                original == null ? unavailable : original.section, ScreenFields.input(username));
    }

    String newPassword() {
        return password.getText().toString();
    }

    void showDuplicateNumber() {
        showError(number, R.string.duplicate_number);
    }

    void showDuplicateUsername() {
        showError(username, R.string.duplicate_username);
    }

    boolean isDirty() {
        if (password.length() != 0) {
            return true;
        }
        EditText[] fields = {username, number, last, first, middle, program, specialization};
        if (original == null) {
            for (EditText field : fields) {
                if (!ScreenFields.input(field).isEmpty()) {
                    return true;
                }
            }
            return year.getSelectedItemPosition() != 0;
        }

        String[] previous = {original.username, original.number, original.lastName, original.firstName,
                original.middleName, original.program, original.specialization};
        for (int i = 0; i < fields.length; i++) {
            if (!ScreenFields.input(fields[i]).equals(previous[i])) {
                return true;
            }
        }
        return !original.yearLevel.equals(year.getSelectedItem());
    }

    private void showError(EditText field, int message) {
        field.requestFocus();
        field.setError(page.getContext().getString(message));
    }
}
