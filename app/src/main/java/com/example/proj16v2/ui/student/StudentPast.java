package com.example.proj16v2.ui.student;

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
import java.util.*;

public class StudentPast extends AppCompatActivity {

    private int studentId;
    private DatabaseHelper db;
    private TextView tvEmpty;
    private StudentPastAdapter adapter;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_student_past);

        studentId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvList);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentPastAdapter();
        rv.setAdapter(adapter);

        load();
    }

    private void load() {
        List<StudentPastAdapter.Item> items = new ArrayList<>();
        Cursor c = db.getSessionsForStudent(studentId);
        while (c.moveToNext()) {
            long id = c.getLong(0);
            String course = c.getString(3);
            String date   = c.getString(4);
            String start  = c.getString(5);
            String end    = c.getString(6);
            String status = c.getString(7);

            boolean past = "completed".equals(status) || "cancelled".equals(status) || isPast(date, end);
            if (past) items.add(new StudentPastAdapter.Item(id, course, status, date, start, end));
        }
        c.close();
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }

    private boolean isPast(String ymd, String hm) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).parse(ymd + " " + hm);
            return d != null && d.before(new Date());
        } catch (ParseException e) { return false; }
    }
}
