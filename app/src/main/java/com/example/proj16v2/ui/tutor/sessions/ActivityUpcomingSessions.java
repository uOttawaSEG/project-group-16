package com.example.proj16v2.ui.tutor.sessions;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.Data.DatabaseHelper;
import com.example.proj16v2.R;

import java.text.ParseException;
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
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_upcoming_sessions);

        tutorId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        View back = findViewById(R.id.btnBack);
        if (back != null) back.setOnClickListener(v -> finish());

        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rv = findViewById(R.id.rvUpcoming);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RowSessionUpcoming(this::cancelSession);
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
            String date    = c.getString(4); // yyyy-MM-dd
            String start   = c.getString(5); // HH:mm
            String end     = c.getString(6); // HH:mm
            String status  = c.getString(7); // approved/rejected/...

            if (!"approved".equals(status)) continue;
            if (isFuture(date, start)) {
                String studentName = "Student #" + studentId; // (optional) look up name later
                items.add(new RowSessionUpcoming.Item(sessionId, studentName, course, date, start, end));
            }
        }
        c.close();

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }

    private boolean isFuture(String date, String start) {
        try {
            Date when = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                    .parse(date + " " + start);
            return when != null && when.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    private void cancelSession(long sessionId) {
        int rows = db.updateSessionStatus(sessionId, "cancelled", "tutor",
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new Date()));
        Toast.makeText(this, rows > 0 ? "Cancelled" : "Failed", Toast.LENGTH_SHORT).show();
        load();
    }
}
