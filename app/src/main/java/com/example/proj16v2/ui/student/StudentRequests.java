package com.example.proj16v2.ui.student;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentRequests extends AppCompatActivity {

    private int studentId;
    private DatabaseHelper db;
    private TextView tvEmpty;
    private StudentRequestsAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_student_requests);

        studentId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvRequests);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudentRequestsAdapter(this::cancelSession);
        rv.setAdapter(adapter);

        load();
    }

    private void load() {
        List<StudentRequestsAdapter.Item> items = new ArrayList<>();

        Cursor c = db.getSessionsForStudent(studentId);
        while (c.moveToNext()) {
            long sessionId = c.getLong(0);
            String course  = c.getString(3);
            String date    = c.getString(4);
            String start   = c.getString(5);
            String end     = c.getString(6);
            String status  = c.getString(7);

            // We only show "active" sessions: requested or approved, AND in the future
            boolean activeStatus = "requested".equals(status) || "approved".equals(status);
            if (!activeStatus) continue;
            if (!isFuture(date, end)) continue;

            boolean canCancel = true; // for now: any future requested/approved can be cancelled

            items.add(new StudentRequestsAdapter.Item(
                    sessionId, course, status, date, start, end, canCancel
            ));
        }
        c.close();

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }

    private boolean isFuture(String ymd, String hm) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                    .parse(ymd + " " + hm);
            return d != null && d.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    private void cancelSession(long sessionId) {
        int rows = db.updateSessionStatus(sessionId, "cancelled", "student", nowIso());
        if (rows > 0) {
            Toast.makeText(this, "Session cancelled.", Toast.LENGTH_SHORT).show();
            load();
        } else {
            Toast.makeText(this, "Failed to cancel session.", Toast.LENGTH_SHORT).show();
        }
    }

    private String nowIso() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                .format(new Date());
    }
}
