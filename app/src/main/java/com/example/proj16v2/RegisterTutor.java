package com.example.proj16v2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterTutor extends AppCompatActivity {

    private DatabaseHelper dbHelper;
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

        firstNameTutor = findViewById(R.id.firstNameTutor);
        lastNameTutor = findViewById(R.id.lastNameTutor);
        emailTutor = findViewById(R.id.emailTutor);
        phoneNumberTutor = findViewById(R.id.phoneNumberTutor);
        highestDegreeTutor = findViewById(R.id.highestDegreeTutor);
        coursesOfferedTutor = findViewById(R.id.coursesOfferedTutor);
        passwordTutor = findViewById(R.id.passwordTutor);
        confirmPasswordTutor = findViewById(R.id.confirmPasswordTutor);
        submitTutorButton = findViewById(R.id.submitTutorButton);

        dbHelper = new DatabaseHelper(this);

        submitTutorButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                registerTutor();
            }
        });
    }

    public void registerTutor(){

        //Receive input values

        String firstNameTutorString = firstNameTutor.getText().toString().trim();
        String lastNameTutorString = lastNameTutor.getText().toString().trim();
        String emailTutorString = emailTutor.getText().toString().trim();
        String phoneNumberTutorString = phoneNumberTutor.getText().toString().trim();
        String passwordTutorString = passwordTutor.getText().toString().trim();
        String confirmPasswordTutorString = confirmPasswordTutor.getText().toString().trim();
        String highestDegreeTutorString = highestDegreeTutor.getText().toString().trim();
        String coursesOfferedTutorString = coursesOfferedTutor.getText().toString().trim();
        registrationStatus = "pending";

        // Field Validation
        if (TextUtils.isEmpty(firstNameTutorString) || firstNameTutorString.length() < 2 || !firstNameTutorString.matches("[a-zA-Z]+")) {
            firstNameTutor.setError("Enter a proper First Name");
            return;

        }

        if (TextUtils.isEmpty(lastNameTutorString) || lastNameTutorString.length() < 2 || !lastNameTutorString.matches("[a-zA-Z ]+")) {
            lastNameTutor.setError("Enter a proper Last Name");
            return;

        }

        if (TextUtils.isEmpty(emailTutorString) || !Patterns.EMAIL_ADDRESS.matcher(emailTutorString).matches()) {
            emailTutor.setError("Enter a valid email");
            return;

        }

        if (TextUtils.isEmpty(phoneNumberTutorString)) {
            phoneNumberTutor.setError("Phone Number is required");
            return;

        }
        if (phoneNumberTutorString.length() !=10){
            phoneNumberTutor.setError("Phone number must be 10 digits");
            return;

        }

        if (!Patterns.PHONE.matcher(phoneNumberTutorString).matches()) {
            phoneNumberTutor.setError("Enter a valid phone number");
            return;

        }

        if (TextUtils.isEmpty(highestDegreeTutorString)) {
            highestDegreeTutor.setError("Highest degree is required");
            return;

        }

        if (TextUtils.isEmpty(passwordTutorString)) {
            passwordTutor.setError("Password is required");
            return;

        }

        if (passwordTutorString.length() < 5) {
            passwordTutor.setError("Password must be at least 5 characters");
            return;

        }

        if (!passwordTutorString.equals(confirmPasswordTutorString)) {
            confirmPasswordTutor.setError("Passwords do not match");
            return;
        }

        if (TextUtils.isEmpty(coursesOfferedTutorString)) {
            coursesOfferedTutor.setError("Please enter at least one course");
            return;
        }


        long userId= dbHelper.addUser(
                firstNameTutorString,
                lastNameTutorString,
                emailTutorString,
                passwordTutorString,
                phoneNumberTutorString,
                null,
                registrationStatus,
                highestDegreeTutorString,
                coursesOfferedTutorString,
                "Tutor"
        );

        // Proceed after successful validation
        if (userId != -1) {
            Toast.makeText(RegisterTutor.this, "Registration Successful", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterTutor.this, MainActivity.class);
            intent.putExtra("UserType", "Tutor");
            intent.putExtra("Email", emailTutorString);
            intent.putExtra("passWord", passwordTutorString);
            intent.putExtra("userId", userId);
            intent.putExtra("registrationStatus", registrationStatus);
            startActivity(intent);
        } else {
            // Show error message if there was an issue with registration
            Toast.makeText(RegisterTutor.this, "Registration Failed. Try Again.", Toast.LENGTH_LONG).show();
        }



    }
}