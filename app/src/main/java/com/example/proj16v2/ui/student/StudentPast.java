package com.example.proj16v2.ui.student;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.R;
import com.example.proj16v2.Data.DatabaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StudentPast extends AppCompatActivity {

    private int studentId;
    private DatabaseHelper db;
    private TextView tvEmpty;
    private StudentPastAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_student_past);

        studentId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentPastAdapter(this);
        rv.setAdapter(adapter);

        load();
    }

    private void load() {
        List<StudentPastAdapter.Item> items = new ArrayList<>();
        Cursor c = db.getSessionsForStudent(studentId);
        while (c.moveToNext()) {
            long sessionId = c.getLong(0);
            String course  = c.getString(3);
            String date    = c.getString(4);
            String start   = c.getString(5);
            String end     = c.getString(6);
            String status  = c.getString(7);

            boolean isPast = "completed".equals(status)
                    || "cancelled".equals(status)
                    || isPastTime(date, end);

            if (!isPast) continue;

            boolean canRate = "completed".equals(status) && !db.hasRating(sessionId);

            items.add(new StudentPastAdapter.Item(
                    sessionId,
                    course,
                    status,
                    date,
                    start,
                    end,
                    canRate
            ));
        }
        c.close();

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }

    private boolean isPastTime(String ymd, String hm) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                    .parse(ymd + " " + hm);
            return d != null && d.before(new Date());
        } catch (ParseException e) {
            return false;
        }
    }
}
