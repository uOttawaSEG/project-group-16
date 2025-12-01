package com.example.proj16v2.ui.student;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proj16v2.R;
import com.example.proj16v2.Data.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RateTutorActivity extends AppCompatActivity {

    private long sessionId;
    private int studentId;
    private int tutorId;
    private String course;
    private String date, start, end;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_tutor);

        db = new DatabaseHelper(this);

        sessionId = getIntent().getLongExtra("session_id", -1);

        // load session info
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT student_id, tutor_id, course_code, date, start_time, end_time " +
                        "FROM Sessions WHERE session_id=?",
                new String[]{String.valueOf(sessionId)}
        );
        if (c.moveToFirst()) {
            studentId = c.getInt(0);
            tutorId   = c.getInt(1);
            course    = c.getString(2);
            date      = c.getString(3);
            start     = c.getString(4);
            end       = c.getString(5);
        }
        c.close();

        TextView tvHeader = findViewById(R.id.tvHeader);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        EditText etComment = findViewById(R.id.etComment);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        Button btnBack = findViewById(R.id.btnBack);

        String tutorName = db.getUserFullName(tutorId);
        if (tutorName == null) tutorName = "Tutor #" + tutorId;
        tvHeader.setText("Rate " + tutorName + "\n" +
                course + " • " + date + " " + start + "-" + end);

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {
            int stars = Math.round(ratingBar.getRating());
            if (stars < 1) stars = 1;   // enforce at least 1 star

            String comment = etComment.getText().toString().trim();
            String nowIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
                    .format(new Date());

            long rid = db.addOrUpdateRating(
                    sessionId,
                    studentId,
                    tutorId,
                    stars,
                    comment,
                    nowIso
            );
            if (rid == -1) {
                Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Thanks for your feedback!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
