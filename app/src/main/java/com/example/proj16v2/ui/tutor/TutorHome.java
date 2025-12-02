package com.example.proj16v2.ui.tutor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.proj16v2.Data.DatabaseHelper;
import android.widget.TextView;
import java.util.Locale;

import com.example.proj16v2.R;
import com.example.proj16v2.ui.tutor.availability.ActivityManageAvailability;
import com.example.proj16v2.ui.tutor.availability.RowAvailabilitySlot;
import com.example.proj16v2.ui.tutor.requests.ActivityPendingRequests;
import com.example.proj16v2.ui.tutor.sessions.ActivityUpcomingSessions;
import com.example.proj16v2.ui.tutor.sessions.PastSessions;

public class TutorHome extends AppCompatActivity {

    private int userId;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);

        // receive from LogIn.java
        final int userId = getIntent().getIntExtra("user_id", -1);
        db = new DatabaseHelper(this);

        TextView tvTutorRating = findViewById(R.id.tvTutorRating);

        float avg = db.getAverageRatingForTutor(userId);
        if (avg <= 0f) {
            tvTutorRating.setText("Average rating: no ratings yet");
        } else {
            tvTutorRating.setText(String.format(Locale.US,
                    "Average rating: %.1f / 5", avg));
        }

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
            Intent i = new Intent(TutorHome.this,
                    com.example.proj16v2.ui.tutor.availability.RowAvailabilitySlot.class);
            i.putExtra("user_id", userId);
            startActivity(i);
        });

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
