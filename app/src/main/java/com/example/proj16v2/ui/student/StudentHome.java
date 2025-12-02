package com.example.proj16v2.ui.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj16v2.R;

public class StudentHome extends AppCompatActivity {

    private int studentId;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_student_home);

        // get user_id from LogIn
        studentId = getIntent().getIntExtra("user_id", -1);

        Button btnSearch = findViewById(R.id.btnSearchByCourse);
        Button btnRequests = findViewById(R.id.btnMyRequests);
        Button btnPastRate = findViewById(R.id.btnPastRate);

        // Search by Course -> StudentFindSlots
        btnSearch.setOnClickListener(v -> {
            Intent i = new Intent(StudentHome.this, StudentFindSlots.class);
            i.putExtra("user_id", studentId);
            startActivity(i);
        });

        // My Requests / Sessions
        btnRequests.setOnClickListener(v -> {
            Intent i = new Intent(StudentHome.this, StudentRequests.class);
            i.putExtra("user_id", studentId);
            startActivity(i);
        });

        // Past Sessions & Rate Tutor
        btnPastRate.setOnClickListener(v -> {
            Intent i = new Intent(StudentHome.this, StudentPast.class);
            i.putExtra("user_id", studentId);
            startActivity(i);
        });
    }
}

