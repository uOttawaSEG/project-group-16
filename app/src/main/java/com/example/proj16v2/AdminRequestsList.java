package com.example.proj16v2;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AdminRequestsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_requests_list); // updated layout name ✅

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Link XML views (match new layout IDs)
        Button pendingButton = findViewById(R.id.btnPending);
        Button approvedButton = findViewById(R.id.btnApproved);
        Button rejectedButton = findViewById(R.id.btnRejected);
        RecyclerView recyclerView = findViewById(R.id.requestsRecyclerView);
        TextView emptyState = findViewById(R.id.emptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RequestsAdapter adapter = new RequestsAdapter(
                userId -> {
                    dbHelper.setUserStatus(userId, "approved");
                    loadUsers(dbHelper, recyclerView, emptyState, "pending", null);
                },
                userId -> {
                    dbHelper.setUserStatus(userId, "rejected");
                    loadUsers(dbHelper, recyclerView, emptyState, "pending", null);
                }
        );
        recyclerView.setAdapter(adapter);

        // Default tab
        loadUsers(dbHelper, recyclerView, emptyState, "pending", null);

        // Switch between tabs
        pendingButton.setOnClickListener(v -> loadUsers(dbHelper, recyclerView, emptyState, "pending", null));
        approvedButton.setOnClickListener(v -> loadUsers(dbHelper, recyclerView, emptyState, "approved", null));
        rejectedButton.setOnClickListener(v -> loadUsers(dbHelper, recyclerView, emptyState, "rejected", null));
    }

    private void loadUsers(DatabaseHelper db, RecyclerView rv, TextView empty, String status, @Nullable String role) {
        try (Cursor cursor = db.getUsers(status, role)) {
            List<UserRow> list = new ArrayList<>();

            while (cursor.moveToNext()) {
                list.add(new UserRow(
                        cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("user_role")),
                        cursor.getString(cursor.getColumnIndexOrThrow("registration_status"))
                ));
            }

            ((RequestsAdapter) rv.getAdapter()).submitList(list);
            empty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    class UserRow {
        long id;
        String first, last, email, role, status;

        UserRow(long id, String f, String l, String e, String r, String s) {
            this.id = id;
            this.first = f;
            this.last = l;
            this.email = e;
            this.role = r;
            this.status = s;
        }
    }

}