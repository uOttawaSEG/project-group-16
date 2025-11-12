package com.example.proj16v2.ui.tutor.sessions;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class PastSessions extends AppCompatActivity {

    private DatabaseHelper db;
    private int tutorId;
    private TextView tvEmpty;
    private RowSessionPast adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_past_sessions);

        tutorId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        View back = findViewById(R.id.btnBack);
        if (back != null) back.setOnClickListener(v -> finish());

        tvEmpty = findViewById(R.id.tvEmpty);
        RecyclerView rv = findViewById(R.id.rvPast);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RowSessionPast();
        rv.setAdapter(adapter);

        load();
    }

    private void load() {
        Cursor c = db.getSessionsForTutor(tutorId);
        List<RowSessionPast.Item> items = new ArrayList<>();

        while (c.moveToNext()) {
            long sessionId = c.getLong(0);
            long studentId = c.getLong(1);
            String course  = c.getString(3);
            String date    = c.getString(4);
            String start   = c.getString(5);
            String end     = c.getString(6);
            String status  = c.getString(7);

            boolean byStatus = "completed".equals(status) || "cancelled".equals(status);
            if (byStatus || isPast(date, end)) {
                String studentName = "Student #" + studentId; // (optional) look up name later
                items.add(new RowSessionPast.Item(sessionId, studentName, course, date, start, end, status));
            }
        }
        c.close();

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }

    private boolean isPast(String date, String end) {
        try {
            Date when = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                    .parse(date + " " + end);
            return when != null && when.before(new Date());
        } catch (ParseException e) {
            return false;
        }
    }
}
