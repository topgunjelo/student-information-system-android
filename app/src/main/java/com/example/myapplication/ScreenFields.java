package com.example.myapplication;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

final class ScreenFields {
    private ScreenFields() {}

    static void text(View page, int id, CharSequence text) {
        ((TextView) page.findViewById(id)).setText(text);
    }

    static String input(EditText field) {
        return field.getText().toString().trim();
    }

    static void bindPasswordToggle(EditText field, ImageButton toggle) {
        toggle.setOnClickListener(view -> {
            boolean reveal = field.getTransformationMethod() instanceof PasswordTransformationMethod;
            field.setTransformationMethod(reveal ? null : PasswordTransformationMethod.getInstance());
            field.setSelection(field.length());
            toggle.setImageResource(reveal ? R.drawable.ic_eye_off : R.drawable.ic_eye);
            toggle.setContentDescription(field.getContext().getString(
                    reveal ? R.string.hide_password : R.string.show_password));
        });
    }
}
