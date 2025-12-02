package com.example.proj16v2.ui.student;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.Data.DatabaseHelper;
import com.example.proj16v2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentFindSlots extends AppCompatActivity {

    private int studentId;
    private DatabaseHelper db;
    private EditText etCourse;
    private TextView tvEmpty;
    private FindSlotsAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_student_find_slots);

        // studentId is passed from LogIn -> StudentHome -> here
        studentId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        etCourse = findViewById(R.id.etCourseFilter);
        tvEmpty = findViewById(R.id.tvEmpty);

        Button btnBack = findViewById(R.id.btnBack);
        Button btnSearch = findViewById(R.id.btnSearchCourse);

        RecyclerView rv = findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FindSlotsAdapter(this::onRequestSlot);
        rv.setAdapter(adapter);

        loadSlots(null);

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            String course = etCourse.getText()
                    .toString()
                    .trim()
                    .toUpperCase(Locale.US);

            if (TextUtils.isEmpty(course)) {
                // Empty search -> show ALL open slots
                loadSlots(null);
            } else {
                // Filter by this course code
                loadSlots(course);
            }
        });
    }

    /** Load all open availability slots for this course and feed the adapter. */
    private void loadSlots(String courseFilter) {
        List<FindSlotsAdapter.Item> items = new ArrayList<>();

        Cursor c = db.getOpenSlots(courseFilter);
        if (c != null) {
            while (c.moveToNext()) {
                long slotId  = c.getLong(0); // slot_id
                long tutorId = c.getLong(1); // tutor_id
                String date  = c.getString(2);
                String start = c.getString(3);
                String end   = c.getString(4);
                boolean manual = c.getInt(5) == 1;

                String tutorName = db.getUserFullName((int) tutorId);
                float avgRating  = db.getAverageRatingForTutor(tutorId);

                items.add(new FindSlotsAdapter.Item(
                        slotId,
                        tutorId,
                        courseFilter,   // course code for the session
                        date,
                        start,
                        end,
                        manual,
                        tutorName,
                        avgRating
                ));
            }
            c.close();
        }

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }

    /** Called when the user taps the Request button on a row. */
    private void onRequestSlot(FindSlotsAdapter.Item slot) {
        if (studentId == -1) {
            Toast.makeText(this,
                    "Error: missing student id. Please log in again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // 1) Prevent the student from double-booking the exact same slot
        if (db.studentHasActiveSessionForSlot(
                studentId,
                slot.tutorId,
                slot.date,
                slot.start
        )) {
            Toast.makeText(this,
                    "You already have a session at this time.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // 2) Create the session (requested or auto-approved)
        String nowIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                .format(new Date());

        String status = slot.isManual ? "requested" : "approved";

        long sessionId = db.createSession(
                studentId,
                slot.tutorId,
                slot.courseCode,
                slot.date,
                slot.start,
                slot.end,
                status,
                nowIso
        );

        if (sessionId == -1) {
            Toast.makeText(this,
                    "Failed to request session. The slot may have just been taken.",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (slot.isManual) {
                Toast.makeText(this,
                        "Request sent to tutor!",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,
                        "Session booked automatically!",
                        Toast.LENGTH_LONG).show();
            }
            // Refresh the list so this slot disappears if it’s no longer open
            loadSlots(slot.courseCode);
        }
    }
}
