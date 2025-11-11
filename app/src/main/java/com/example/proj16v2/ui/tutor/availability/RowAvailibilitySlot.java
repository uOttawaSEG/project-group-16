package com.example.proj16v2.ui.tutor.availability;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.Data.DatabaseHelper;
import com.example.proj16v2.R;
import com.example.proj16v2.ui.tutor.AvailibilitySlotsAdapter;

import java.util.ArrayList;
import java.util.List;

public class RowAvailibilitySlot extends AppCompatActivity {

    private DatabaseHelper db;
    private int tutorId;
    private AvailibilitySlotsAdapter adapter; // or SlotsAdapter if that's your adapter name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_availibility_slot); // screen layout with rvMySlots + btnBack

        tutorId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvMySlots);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AvailibilitySlotsAdapter(this::deleteSlot);
        rv.setAdapter(adapter);

        load();
    }

    private void load() {
        List<AvailibilitySlotsAdapter.Item> items = new ArrayList<>();
        android.database.Cursor c = db.getAllSlots(tutorId);
        while (c.moveToNext()) {
            long slotId = c.getLong(0);    // slot_id
            String date = c.getString(2);  // date
            String start = c.getString(3); // start_time
            String end = c.getString(4);   // end_time
            items.add(new AvailibilitySlotsAdapter.Item(slotId, date, start, end));
        }
        c.close();
        adapter.setData(items);
    }

    private void deleteSlot(long slotId) {
        int rows = db.deleteSlot(slotId);
        if (rows > 0) {
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            load();
        } else {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }
}
