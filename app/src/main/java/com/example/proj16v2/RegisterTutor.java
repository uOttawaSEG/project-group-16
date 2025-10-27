package com.example.proj16v2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterTutor extends AppCompatActivity {

    //private DatabaseHelper dbHelper;
    // private ArrayList<String> userData;
    private Button submitTutorButton;
    private EditText firstNameTutor, lastNameTutor, emailTutor, phoneNumberTutor, passwordTutor, confirmPasswordTutor,
            highestDegreeTutor, coursesOfferedTutor;
    private String registrationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_tutor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
}