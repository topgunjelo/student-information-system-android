package com.example.myapplication;

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public final class MainActivity extends AppCompatActivity {
    private static final String REMEMBERED_USERNAME = "remembered_username";

    private enum Screen {
        LOGIN(R.layout.screen_login, R.string.app_name, 0),
        DASHBOARD(R.layout.screen_dashboard, R.string.dashboard, R.id.nav_dashboard),
        DIRECTORY(R.layout.screen_directory, R.string.directory, R.id.nav_directory),
        ACADEMIC(R.layout.screen_academic, R.string.academic_information, R.id.nav_academic),
        PROFILE(R.layout.screen_profile, R.string.student_profile, R.id.nav_profile),
        STUDENT_DETAIL(R.layout.screen_profile, R.string.student_profile, 0),
        FORM(R.layout.screen_student_form, R.string.edit_information, 0);

        final int layout;
        final int title;
        final int tab;

        Screen(int layout, int title, int tab) {
            this.layout = layout;
            this.title = title;
            this.tab = tab;
        }
    }

    private ArrayList<Student> students = SampleStudents.create();
    private SharedPreferences preferences;
    private Screen screen = Screen.LOGIN;
    private String detailNumber = "";
    private String directoryQuery = "";
    private int directoryScroll;
    private boolean adding;
    private View appRoot;
    private ViewGroup host;
    private View content;
    private StudentForm form;

    @Override
    @SuppressWarnings({"deprecation", "unchecked"})
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (state != null) {
            ArrayList<Student> restored = (ArrayList<Student>) state.getSerializable("students");
            if (restored != null && !restored.isEmpty()) {
                students = restored;
            }
            screen = Screen.valueOf(state.getString("screen", Screen.LOGIN.name()));
            detailNumber = state.getString("detailNumber", "");
            directoryQuery = state.getString("directoryQuery", "");
            directoryScroll = state.getInt("directoryScroll");
            adding = state.getBoolean("adding");
        }

        preferences = getSharedPreferences("sis_demo", MODE_PRIVATE);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(Color.BLACK);
        }
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView())
                .setAppearanceLightNavigationBars(true);

        setContentView(R.layout.activity_main);
        appRoot = findViewById(R.id.app_root);
        host = findViewById(R.id.screen_host);
        ViewCompat.setOnApplyWindowInsetsListener(appRoot, (view, windowInsets) -> {
            Insets safe = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets keyboard = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
            view.setPadding(safe.left, safe.top, safe.right, Math.max(safe.bottom, keyboard.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(appRoot);

        for (Screen destination : Screen.values()) {
            if (destination.tab != 0) {
                findViewById(destination.tab).setOnClickListener(view -> navigate(destination));
            }
        }
        findViewById(R.id.up_button).setOnClickListener(view -> goBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goBack();
            }
        });
        render(false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        rememberDirectoryScroll();
        state.putSerializable("students", students);
        state.putString("screen", screen.name());
        state.putString("detailNumber", detailNumber);
        state.putString("directoryQuery", directoryQuery);
        state.putInt("directoryScroll", directoryScroll);
        state.putBoolean("adding", adding);
    }

    private void navigate(Screen next) {
        if (screen == next) {
            return;
        }
        rememberDirectoryScroll();
        hideKeyboard();
        screen = next;
        render(true);
    }

    private void rememberDirectoryScroll() {
        if (screen == Screen.DIRECTORY) {
            directoryScroll = content.getScrollY();
        }
    }

    private void render(boolean animate) {
        host.removeAllViews();
        form = null;
        content = getLayoutInflater().inflate(screen.layout, host, false);
        host.addView(content);
        content.setFocusableInTouchMode(true);
        content.requestFocus();
        ViewCompat.setAccessibilityPaneTitle(content, getString(
                screen == Screen.FORM && adding ? R.string.add_information : screen.title));

        appRoot.setBackgroundResource(screen == Screen.LOGIN ? R.drawable.bg_login : R.drawable.bg_window);
        boolean hasUp = screen == Screen.FORM || screen == Screen.STUDENT_DETAIL;
        findViewById(R.id.subpage_header).setVisibility(hasUp ? View.VISIBLE : View.GONE);
        findViewById(R.id.nav_container).setVisibility(screen.tab == 0 ? View.GONE : View.VISIBLE);
        for (Screen destination : Screen.values()) {
            if (destination.tab != 0) {
                findViewById(destination.tab).setSelected(destination == screen);
            }
        }

        switch (screen) {
            case LOGIN:
                bindLogin();
                break;
            case DASHBOARD:
                bindDashboard();
                break;
            case DIRECTORY:
                bindDirectory();
                break;
            case ACADEMIC:
                bindAcademic();
                break;
            case PROFILE:
            case STUDENT_DETAIL:
                bindProfile();
                break;
            case FORM:
                bindForm();
                break;
        }

        if (animate && (Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                || ValueAnimator.areAnimatorsEnabled())) {
            content.setAlpha(0.78f);
            content.setTranslationY(5 * getResources().getDisplayMetrics().density);
            content.animate().alpha(1f).translationY(0f).setDuration(165)
                    .setInterpolator(new DecelerateInterpolator()).start();
        }
    }

    private void bindLogin() {
        EditText username = content.findViewById(R.id.login_username);
        EditText password = content.findViewById(R.id.login_password);
        CheckBox remember = content.findViewById(R.id.remember_me);
        String remembered = preferences.getString(REMEMBERED_USERNAME, null);
        username.setText(remembered);
        remember.setChecked(remembered != null);
        ScreenFields.bindPasswordToggle(password, content.findViewById(R.id.login_visibility));
        Runnable signIn = () -> {
            String entered = ScreenFields.input(username);
            SharedPreferences.Editor editor = preferences.edit();
            if (remember.isChecked() && !entered.isEmpty()) {
                editor.putString(REMEMBERED_USERNAME, entered);
            } else {
                editor.remove(REMEMBERED_USERNAME);
            }
            editor.apply();
            navigate(Screen.DASHBOARD);
        };
        content.findViewById(R.id.sign_in).setOnClickListener(view -> signIn.run());
        password.setOnEditorActionListener((view, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                signIn.run();
                return true;
            }
            return false;
        });
        content.findViewById(R.id.forgot_password).setOnClickListener(view ->
                dialog(R.string.forgot_title, getString(R.string.forgot_body))
                        .setPositiveButton(R.string.close, null).show());
    }

    private void bindDashboard() {
        Student student = students.get(0);
        ScreenFields.text(content, R.id.dashboard_avatar, student.initials());
        ScreenFields.text(content, R.id.dashboard_name, student.fullName());
        ScreenFields.text(content, R.id.dashboard_number, getString(R.string.record_caption, student.number));
        ScreenFields.text(content, R.id.dashboard_program, student.program);
        ScreenFields.text(content, R.id.dashboard_year, student.yearLevel);
        ScreenFields.text(content, R.id.dashboard_specialization, student.displaySpecialization());

        dashboardAction(R.id.open_profile, () -> navigate(Screen.PROFILE));
        dashboardAction(R.id.open_academic, () -> navigate(Screen.ACADEMIC));
        dashboardAction(R.id.open_enrollment, () ->
                dialog(R.string.enrollment_overview, getString(R.string.dialog_enrollment_info,
                        getString(R.string.enrolled), getString(R.string.year_value),
                        getString(R.string.semester_value), student.section))
                        .setPositiveButton(R.string.close, null).show());
        dashboardAction(R.id.open_account, () ->
                dialog(R.string.account_information, getString(R.string.dialog_account_info, student.username))
                        .setNegativeButton(R.string.sign_out, (dialog, which) -> {
                            students = SampleStudents.create();
                            detailNumber = "";
                            directoryQuery = "";
                            directoryScroll = 0;
                            navigate(Screen.LOGIN);
                        })
                        .setPositiveButton(R.string.close, null).show());
    }

    private void dashboardAction(int id, Runnable action) {
        View card = content.findViewById(id);
        ViewCompat.setScreenReaderFocusable(card, true);
        card.setOnClickListener(view -> action.run());
    }

    private void bindProfile() {
        boolean isPrimary = screen == Screen.PROFILE;
        Student student = isPrimary ? students.get(0) : studentForNumber(detailNumber);
        if (student == null) {
            navigate(Screen.DIRECTORY);
            return;
        }
        ScreenFields.text(content, R.id.profile_avatar, student.initials());
        ScreenFields.text(content, R.id.profile_name, student.fullName());
        ScreenFields.text(content, R.id.profile_number, getString(R.string.record_caption, student.number));
        if (!isPrimary) {
            ((TextView) content.findViewById(R.id.screen_subtitle)).setText(R.string.profile_guest_subtitle);
        }
        LinearLayout fields = content.findViewById(R.id.profile_fields);
        addField(fields, R.string.student_number, student.number);
        addField(fields, R.string.last_name, student.lastName);
        addField(fields, R.string.first_name, student.firstName);
        addField(fields, R.string.middle_name, student.middleName.isEmpty() ? getString(R.string.not_available) : student.middleName);
        addField(fields, R.string.full_name, student.fullName());

        View edit = content.findViewById(R.id.edit_profile);
        edit.setVisibility(isPrimary ? View.VISIBLE : View.GONE);
        edit.setOnClickListener(view -> {
            adding = false;
            navigate(Screen.FORM);
        });
    }

    private void bindAcademic() {
        Student student = students.get(0);
        LinearLayout fields = content.findViewById(R.id.academic_fields);
        addField(fields, R.string.student_number, student.number);
        addField(fields, R.string.program, student.program);
        addField(fields, R.string.specialization, student.displaySpecialization());
        addField(fields, R.string.year_level, student.yearLevel);
        addField(fields, R.string.college, student.department);
        LinearLayout enrollment = content.findViewById(R.id.enrollment_fields);
        addField(enrollment, R.string.academic_year, getString(R.string.year_value));
        addField(enrollment, R.string.semester, getString(R.string.semester_value));
        addField(enrollment, R.string.section, student.section);
        addField(enrollment, R.string.enrollment_status, getString(R.string.enrolled));
    }

    private void addField(LinearLayout container, int label, String value) {
        View row = getLayoutInflater().inflate(R.layout.item_info_field, container, false);
        if (container.getChildCount() == 0) {
            row.findViewById(R.id.info_divider).setVisibility(View.GONE);
        }
        ((TextView) row.findViewById(R.id.info_label)).setText(label);
        ScreenFields.text(row, R.id.info_value, value);
        container.addView(row);
    }

    private void bindDirectory() {
        EditText search = content.findViewById(R.id.student_search);
        View clear = content.findViewById(R.id.clear_search);
        search.setText(directoryQuery);
        search.setSelection(search.length());
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                directoryQuery = text.toString();
                renderDirectoryResults();
            }

            @Override
            public void afterTextChanged(Editable text) {}
        });
        clear.setOnClickListener(view -> search.setText(""));
        search.setOnEditorActionListener((view, action, event) -> {
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard();
                return true;
            }
            return false;
        });
        content.findViewById(R.id.add_student).setOnClickListener(view -> {
            adding = true;
            navigate(Screen.FORM);
        });
        renderDirectoryResults();
        ScrollView scroll = (ScrollView) content;
        scroll.post(() -> scroll.scrollTo(0, directoryScroll));
    }

    private void renderDirectoryResults() {
        LinearLayout list = content.findViewById(R.id.student_list);
        list.removeAllViews();
        for (Student student : students) {
            if (!student.matches(directoryQuery)) {
                continue;
            }
            View item = getLayoutInflater().inflate(R.layout.item_student, list, false);
            ScreenFields.text(item, R.id.directory_avatar, student.initials());
            ScreenFields.text(item, R.id.directory_name, student.fullName());
            ScreenFields.text(item, R.id.directory_number, student.number);
            ScreenFields.text(item, R.id.directory_program, student.program);
            ScreenFields.text(item, R.id.directory_year, student.yearLevel);
            View profile = item.findViewById(R.id.view_profile);
            profile.setContentDescription(getString(R.string.view_student_profile, student.fullName()));
            profile.setOnClickListener(view -> {
                detailNumber = student.number;
                navigate(Screen.STUDENT_DETAIL);
            });
            list.addView(item);
        }

        int count = list.getChildCount();
        ScreenFields.text(content, R.id.student_count, count == 1
                ? getString(R.string.single_record) : getString(R.string.record_count, count));
        content.findViewById(R.id.clear_search).setVisibility(directoryQuery.isEmpty() ? View.GONE : View.VISIBLE);
        content.findViewById(R.id.empty_results).setVisibility(count == 0 ? View.VISIBLE : View.GONE);
    }

    private void bindForm() {
        form = new StudentForm(content, adding ? null : students.get(0));
        content.findViewById(R.id.cancel_form).setOnClickListener(view -> cancelForm());
        content.findViewById(R.id.save_information).setOnClickListener(view -> {
            Student updated = form.readStudent();
            if (updated == null) {
                return;
            }

            Student existing = studentForNumber(updated.number);
            if (existing != null && (adding || existing != students.get(0))) {
                form.showDuplicateNumber();
                return;
            }

            if (adding) {
                students.add(1, updated);
                directoryQuery = "";
                directoryScroll = 0;
                navigate(Screen.DIRECTORY);
                Toast.makeText(this, R.string.student_added, Toast.LENGTH_SHORT).show();
            } else {
                students.set(0, updated);
                navigate(Screen.PROFILE);
                Toast.makeText(this, R.string.edit_saved, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Student studentForNumber(String number) {
        for (Student student : students) {
            if (student.number.equalsIgnoreCase(number)) {
                return student;
            }
        }
        return null;
    }

    private void goBack() {
        switch (screen) {
            case LOGIN:
            case DASHBOARD:
                finish();
                break;
            case STUDENT_DETAIL:
                navigate(Screen.DIRECTORY);
                break;
            case FORM:
                cancelForm();
                break;
            default:
                navigate(Screen.DASHBOARD);
        }
    }

    private void cancelForm() {
        Runnable leave = () -> navigate(adding ? Screen.DIRECTORY : Screen.PROFILE);
        if (!form.isDirty()) {
            leave.run();
            return;
        }

        dialog(R.string.discard_title, getString(R.string.discard_body))
                .setNegativeButton(R.string.keep_editing, null)
                .setPositiveButton(R.string.discard, (dialog, which) -> leave.run())
                .show();
    }

    private MaterialAlertDialogBuilder dialog(int title, CharSequence message) {
        return new MaterialAlertDialogBuilder(this)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.bg_glass))
                .setTitle(title)
                .setMessage(message);
    }

    private void hideKeyboard() {
        WindowCompat.getInsetsController(getWindow(), appRoot).hide(WindowInsetsCompat.Type.ime());
    }
}
