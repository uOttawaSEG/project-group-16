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
import java.text.SimpleDateFormat;
import java.util.*;

public class StudentFindSlots extends AppCompatActivity {

    private DatabaseHelper db;
    private int studentId;
    private FindSlotsAdapter adapter;
    private TextView tvEmpty;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_student_find_slots);

        studentId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        View back = findViewById(R.id.btnBack);
        if (back != null) back.setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvFindSlots);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FindSlotsAdapter(this::requestSlot);
        rv.setAdapter(adapter);

        tvEmpty = findViewById(R.id.tvEmpty);
        load();
    }

    private void load() {
        List<FindSlotsAdapter.Item> items = new ArrayList<>();
        Cursor c = db.getOpenSlots();

        while (c.moveToNext()) {
            long slotId  = c.getLong(0);
            long tutorId = c.getLong(1);
            String date  = c.getString(2);
            String start = c.getString(3);
            String end   = c.getString(4);
            boolean manual = c.getInt(5) == 1;

            String name = db.getUserFullName((int) tutorId);
            if (name == null) name = "Tutor #" + tutorId;

            float avg = db.getAverageRatingForTutor(tutorId);

            items.add(new FindSlotsAdapter.Item(
                    slotId, tutorId, date, start, end,
                    manual, name, avg
            ));
        }
        c.close();

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }


    private void requestSlot(FindSlotsAdapter.Item it) {
        // For D3: let student choose a courseCode later; for now use placeholder or prompt.
        String courseCode = "GEN-101";
        String status = it.isManual ? "requested" : "approved";
        String nowIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new Date());

        long id = db.createSession(
                studentId, it.tutorId, courseCode,
                it.date, it.start, it.end,
                status, nowIso
        );

        if (id == -1) {
            Toast.makeText(this, "Could not request slot", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, it.isManual ? "Requested (pending tutor approval)" : "Booked!", Toast.LENGTH_LONG).show();
            load(); // refresh list (slot disappears if requested/approved)
        }
    }
}
