package com.example.proj16v2.ui.tutor.requests;

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
import java.util.ArrayList;
import java.util.List;

public class ActivityPendingRequests extends AppCompatActivity {

    private DatabaseHelper db;
    private int tutorId;
    private RowSessionRequest adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        tutorId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        View back = findViewById(R.id.btnBack);
        if (back != null) back.setOnClickListener(v -> finish());

        tvEmpty = findViewById(R.id.tvEmpty);

        RecyclerView rv = findViewById(R.id.rvPending);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RowSessionRequest(
                this::approve,
                this::reject
        );
        rv.setAdapter(adapter);

        load();
    }

    private void load() {
        // Sessions.status=requested AND slot is manual-approval for THIS tutor
        // Join: Sessions s, Users u (student name), AvailabilitySlots a (manual flag)
        String sql =
                "SELECT s.session_id, s.course_code, s.date, s.start_time, s.end_time, " +
                        "       u.first_name, u.last_name, a.is_manual_approval " +
                        "FROM Sessions s " +
                        "JOIN Users u ON u.user_id = s.student_id " +
                        "JOIN AvailabilitySlots a " +
                        "     ON a.tutor_id = s.tutor_id AND a.date = s.date AND a.start_time = s.start_time " +
                        "WHERE s.tutor_id = ? AND s.status = 'requested' AND a.is_manual_approval = 1 " +
                        "ORDER BY s.date ASC, s.start_time ASC";

        Cursor c = db.getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(tutorId)});
        List<RowSessionRequest.Item> items = new ArrayList<>();
        while (c.moveToNext()) {
            long   id    = c.getLong(0);
            String course= c.getString(1);
            String date  = c.getString(2);
            String start = c.getString(3);
            String end   = c.getString(4);
            String first = c.getString(5);
            String last  = c.getString(6);
            String name  = ((first==null?"":first) + " " + (last==null?"":last)).trim();
            items.add(new RowSessionRequest.Item(id, name, course, date, start, end));
        }
        c.close();

        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.setData(items);
    }

    private void approve(long sessionId) {
        int rows = db.updateSessionStatus(sessionId, "approved", null, null);
        Toast.makeText(this, rows > 0 ? "Approved" : "Failed", Toast.LENGTH_SHORT).show();
        load();
    }

    private void reject(long sessionId) {
        int rows = db.updateSessionStatus(sessionId, "rejected", null, null);
        Toast.makeText(this, rows > 0 ? "Rejected" : "Failed", Toast.LENGTH_SHORT).show();
        load();
    }
}
