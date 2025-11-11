package com.example.proj16v2;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PastSessions extends AppCompatActivity {

    private DatabaseHelper db;
    private int tutorId;
    private PastSessionsAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_past_sessions); // has btnBack + rvPast
        db = new DatabaseHelper(this);
        tutorId = getIntent().getIntExtra("user_id", -1);

        ((Button)findViewById(R.id.btnBack)).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvPast);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PastSessionsAdapter();
        rv.setAdapter(adapter);

        load();
    }

    private void load() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String now   = new SimpleDateFormat("HH:mm", Locale.US).format(new Date());

        List<PastSessionsAdapter.Item> items = new ArrayList<>();
        Cursor c = db.getPastSessionsForTutor(tutorId, today, now);
        while (c.moveToNext()) {
            long id = c.getLong(0);
            String course = c.getString(2);
            String date   = c.getString(3);
            String start  = c.getString(4);
            String end    = c.getString(5);
            String first  = c.getString(7);
            String last   = c.getString(8);
            String name   = ((first==null?"":first) + " " + (last==null?"":last)).trim();
            items.add(new PastSessionsAdapter.Item(id, name, course, date, start, end));
        }
        c.close();
        adapter.setData(items);
    }
}
