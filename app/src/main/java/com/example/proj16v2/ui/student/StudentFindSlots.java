package com.example.proj16v2.ui.student;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.Data.DatabaseHelper;
import com.example.proj16v2.R;

import java.util.ArrayList;
import java.util.List;

public class StudentFindSlots extends AppCompatActivity {

    private int studentId;
    private DatabaseHelper db;

    private EditText etCourseFilter;
    private TextView tvEmpty;
    private FindSlotsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_find_slots);

        studentId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        etCourseFilter = findViewById(R.id.etCourseFilter);
        Button btnSearch = findViewById(R.id.btnSearchCourse);
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FindSlotsAdapter(item -> {
            // This is where you already create the Session when the student taps "Request"
            // (I’m not touching your existing request code)
            // e.g. createSession(studentId, item.tutorId, selectedCourse, ...)
            // and then maybe reload();
        });
        rv.setAdapter(adapter);

        // initial load: show ALL open slots
        loadSlots(null);

        btnSearch.setOnClickListener(v -> {
            String course = etCourseFilter.getText().toString().trim();
            if (course.isEmpty()) {
                loadSlots(null);          // no filter
            } else {
                loadSlots(course);        // filtered
            }
        });
    }

    private void loadSlots(String courseFilter) {
        List<FindSlotsAdapter.Item> items = new ArrayList<>();

        Cursor c;
        if (courseFilter == null || courseFilter.trim().isEmpty()) {
            c = db.getOpenSlots();
        } else {
            c = db.getOpenSlotsForCourse(courseFilter.trim());
        }

        while (c.moveToNext()) {
            long slotId  = c.getLong(0);        // slot_id
            long tutorId = c.getLong(1);        // tutor_id
            String date  = c.getString(2);      // date
            String start = c.getString(3);      // start_time
            String end   = c.getString(4);      // end_time
            boolean manual = c.getInt(5) == 1;  // is_manual_approval

            // --- NEW: look up tutor name + rating ---
            String tutorName = db.getUserFullName((int) tutorId);
            if (tutorName == null) {
                tutorName = "Tutor #" + tutorId;
            }

            float avgRating = db.getAverageRatingForTutor(tutorId);

            // For display, course is just the filter the student used.
            // (Open slots themselves aren't tied to a single course.)
            String courseDisplay = (courseFilter == null || courseFilter.trim().isEmpty())
                    ? ""
                    : courseFilter.trim().toUpperCase(java.util.Locale.US);

            items.add(new FindSlotsAdapter.Item(
                    slotId,
                    tutorId,
                    date,
                    start,
                    end,
                    manual,
                    tutorName,
                    avgRating,
                    courseDisplay
            ));
        }
        c.close();

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }

}
