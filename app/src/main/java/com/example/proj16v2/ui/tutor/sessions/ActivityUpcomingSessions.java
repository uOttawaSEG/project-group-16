package com.example.proj16v2.ui.tutor.sessions;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.R;
import com.example.proj16v2.Data.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityUpcomingSessions extends AppCompatActivity {

    private DatabaseHelper db;
    private int tutorId;
    private TextView tvEmpty;
    private RowSessionUpcoming adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_sessions);

        tutorId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        View back = findViewById(R.id.btnBack);
        if (back != null) back.setOnClickListener(v -> finish());

        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvUpcoming);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // ↓↓↓ THIS is the "activity callback" wiring ↓↓↓
        adapter = new RowSessionUpcoming(
                this::cancelSession,    // onCancel
                this::completeSession   // onComplete
        );
        rv.setAdapter(adapter);

        load();
    }

    private void load() {
        Cursor c = db.getSessionsForTutor(tutorId);
        List<RowSessionUpcoming.Item> items = new ArrayList<>();

        while (c.moveToNext()) {
            long sessionId = c.getLong(0);
            long studentId = c.getLong(1);
            String course  = c.getString(3);
            String date    = c.getString(4);
            String start   = c.getString(5);
            String end     = c.getString(6);
            String status  = c.getString(7);

            if (!"approved".equals(status)) continue;
            if (!isFuture(date, start)) continue;

            String studentName = "Student #" + studentId; // or look up real name
            items.add(new RowSessionUpcoming.Item(sessionId, studentName, course, date, start, end));
        }
        c.close();

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }

    // ---------- helpers & callbacks ----------

    private boolean isFuture(String ymd, String hm) {
        try {
            Date when = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                    .parse(ymd + " " + hm);
            return when != null && when.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private String nowIso() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new Date());
    }

    private void cancelSession(long sessionId) {
        int rows = db.updateSessionStatus(sessionId, "cancelled", "tutor", nowIso());
        Toast.makeText(this, rows > 0 ? "Session cancelled" : "Failed to cancel", Toast.LENGTH_SHORT).show();
        load();
    }

    private void completeSession(long sessionId) {
        int rows = db.updateSessionStatus(sessionId, "completed", null, null);
        Toast.makeText(this, rows > 0 ? "Marked as completed" : "Failed to update", Toast.LENGTH_SHORT).show();
        load();
    }
}
