package com.example.proj16v2.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proj16v2.R;
import com.example.proj16v2.Data.DatabaseHelper;


public class StudentHome extends AppCompatActivity {

    private int userId;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_student_home);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Missing user_id", Toast.LENGTH_LONG).show();
        }

        Button btnSearch = findViewById(R.id.btnSearchByCourse);
        Button btnRequests = findViewById(R.id.btnMyRequests);
        Button btnPast = findViewById(R.id.btnPastRate);

        btnSearch.setOnClickListener(v ->
                startActivity(new Intent(this, StudentFindSlots.class)
                        .putExtra("user_id", userId))
        );

        // “My Requests / Sessions” → show Requested + Upcoming in one list
        btnRequests.setOnClickListener(v ->
                startActivity(new Intent(this, StudentRequests.class)
                        .putExtra("user_id", userId))
        );

        // “Past Sessions & Rate Tutor”
        btnPast.setOnClickListener(v ->
                startActivity(new Intent(this, StudentPast.class)
                        .putExtra("user_id", userId))
        );
    }
}
