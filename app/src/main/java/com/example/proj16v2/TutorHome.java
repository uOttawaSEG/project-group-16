package com.example.proj16v2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TutorHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);

        // receive from LogIn.java
        final int userId = getIntent().getIntExtra("user_id", -1);

        Button btnCreateSlot       = findViewById(R.id.btnCreateSlot);
        Button btnPendingRequests  = findViewById(R.id.btnPendingRequests);
        Button btnUpcoming         = findViewById(R.id.btnUpcoming);
        Button btnPast             = findViewById(R.id.btnPast);
        Button btnMySlots          = findViewById(R.id.btnMySlots);

        // You can route both "Create Slot" and "My Availability Slots" to the same screen
        btnCreateSlot.setOnClickListener(v -> {
            Intent i = new Intent(TutorHome.this, ActivityManageAvailability.class);
            i.putExtra("user_id", userId);
            startActivity(i);
        });

        btnMySlots.setOnClickListener(v -> {
            Intent i = new Intent(TutorHome.this, RowAvailibilitySlot.class);
            i.putExtra("user_id", userId);
            startActivity(i);
        });

        // Stubs for D3 screens (create these activities or change targets)
        btnPendingRequests.setOnClickListener(v -> {
            Intent i = new Intent(TutorHome.this, ActivityPendingRequests.class);
            i.putExtra("user_id", userId);
            startActivity(i);
        });

        btnUpcoming.setOnClickListener(v -> {
            Intent i = new Intent(TutorHome.this, ActivityUpcomingSessions.class);
            i.putExtra("user_id", userId);
            startActivity(i);
        });

        btnPast.setOnClickListener(v -> {
            Intent i = new Intent(TutorHome.this, PastSessions.class);
            i.putExtra("user_id", userId);
            startActivity(i);
        });
    }
}
