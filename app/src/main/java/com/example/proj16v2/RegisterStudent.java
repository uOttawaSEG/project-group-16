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

public class RegisterStudent extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    //private ArrayList<String> userData;

    private Button submitStudentButton;
    private EditText firstName;
    private EditText lastName;
    private EditText emailAddress;
    private EditText phoneNumber;
    private EditText password;
    private EditText confirmPassword;
    private EditText programOfStudy;
    private String registrationStatus;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_student);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialize fields
        submitStudentButton = findViewById(R.id.submitStudentButton);
        firstName = findViewById(R.id.firstNameStudent);
        lastName = findViewById(R.id.lastNameStudent);
        emailAddress = findViewById(R.id.emailStudent);
        phoneNumber = findViewById(R.id.phoneNumberStudent);
        password = findViewById(R.id.passwordStudent);
        confirmPassword = findViewById(R.id.confirmPasswordStudent);
        programOfStudy = findViewById(R.id.programOfStudyStudent);

        dbHelper=new DatabaseHelper(this);

        submitStudentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                registerStudent();
            }
        });
    }

    public void registerStudent(){
        //Receive input values
        String firstNameString = firstName.getText().toString().trim();
        String lastNameString = lastName.getText().toString().trim();
        String emailAddressString = emailAddress.getText().toString().trim();
        String phoneNumberString = phoneNumber.getText().toString().trim();
        String programOfStudyString = programOfStudy.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        String confirmPasswordString = confirmPassword.getText().toString().trim();
        registrationStatus = "pending";

        //Field validation
        if (TextUtils.isEmpty(firstNameString) || firstNameString.length() < 2 || !firstNameString.matches("[a-zA-Z]+")) {
            firstName.setError("Enter a proper First Name");
            return;

        }

        if (TextUtils.isEmpty(lastNameString) || lastNameString.length() < 2 || !lastNameString.matches("[a-zA-Z ]+")) {
            lastName.setError("Enter a proper Last Name");
            return;

        }

        if (TextUtils.isEmpty(emailAddressString) || !Patterns.EMAIL_ADDRESS.matcher(emailAddressString).matches()) {
            emailAddress.setError("Enter a valid Email");
            return;

        }

        if (TextUtils.isEmpty(phoneNumberString)) {
            phoneNumber.setError("Phone Number is required");
            return;

        }
        if (phoneNumberString.length() !=10){
            phoneNumber.setError("Phone number must be 10 digits");
            return;

        }

        if (!Patterns.PHONE.matcher(phoneNumberString).matches()) {
            phoneNumber.setError("Enter a valid phone number");
            return;

        }

        if (TextUtils.isEmpty(programOfStudyString)) {
            programOfStudy.setError("Program of Study is required");
            return;

        }

        if (TextUtils.isEmpty(passwordString)) {
            password.setError("Password is required");
            return;

        }

        if (passwordString.length() < 5) {
            password.setError("Password must be at least 5 characters");
            return;

        }

        if (!passwordString.equals(confirmPasswordString)) {
            confirmPassword.setError("Passwords do not match");
            return;
        }

        if (dbHelper.emailExists(emailAddressString)){
            Toast.makeText(this, "This email already exists ! Please choose a different email.", Toast.LENGTH_SHORT).show();
        }

        if (dbHelper.phoneExists(phoneNumberString)){
            Toast.makeText(this, "This phone number already exists ! Please choose a different phone number.", Toast.LENGTH_SHORT).show();
        }

        // If all validations pass, proceed to register
         /* userData = new ArrayList<>(7);
        userData.add(firstNameString);
        userData.add(lastNameString);
        userData.add(emailAddressString);
        userData.add(phoneNumberString);
        userData.add(addressString);
        userData.add(passwordString);

          */

        long userId = dbHelper.addUser(
                firstNameString,
                lastNameString,
                emailAddressString,
                passwordString,
                phoneNumberString,
                programOfStudyString,
                "pending",
                null,
                null,
                "Student"
        );



        if (userId!= -1) {
            Toast.makeText(RegisterStudent.this, "Registration Successful", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RegisterStudent.this, MainActivity.class);
            intent.putExtra("UserType", "Student");
            intent.putExtra("Email", emailAddressString);
            intent.putExtra("passWord", passwordString);
            intent.putExtra("registrationStatus", registrationStatus);
            intent.putExtra("userId", userId);
            startActivity(intent);
        } else {
            // Show error message if there was an issue with registration


            Toast.makeText(RegisterStudent.this, "Registration Failed. Try Again.", Toast.LENGTH_LONG).show();
        }

    }
}