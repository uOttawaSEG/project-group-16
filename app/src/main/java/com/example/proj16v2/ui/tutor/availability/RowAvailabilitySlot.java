package com.example.proj16v2.ui.tutor.availability;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;                    // <-- add this
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.R;
import com.example.proj16v2.Data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class RowAvailabilitySlot extends AppCompatActivity {

    private DatabaseHelper db;
    private int tutorId;
    private SlotsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_row_availability_slot);

        tutorId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        // Back button
        View back = findViewById(R.id.btnBack);
        if (back != null) back.setOnClickListener(v -> finish());

        // RecyclerView
        RecyclerView rv = findViewById(R.id.rvMySlots);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SlotsAdapter(this::deleteSlot);
        rv.setAdapter(adapter);


        loadAllSlots();             // simple version
        // or: loadSlotsSafe(adapter, tutorId);  // if you kept the defensive helper
    }

    private void loadAllSlots() {
        List<SlotsAdapter.SlotItem> items = new ArrayList<>();
        Cursor c = db.getAllSlots(tutorId);
        while (c.moveToNext()) {
            long   slotId = c.getLong(0);   // slot_id
            String date   = c.getString(2); // date
            String start  = c.getString(3); // start_time
            String end    = c.getString(4); // end_time
            items.add(new SlotsAdapter.SlotItem(slotId, date, start, end));
        }
        c.close();
        adapter.setData(items);
    }

    private void deleteSlot(long slotId) {
        int rows = db.deleteSlot(slotId);
        Toast.makeText(this, rows > 0 ? "Deleted" : "Delete failed", Toast.LENGTH_SHORT).show();
        loadAllSlots();
    }
}
