package com.example.proj16v2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "OTAMS.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_APPOINTMENTS = "appointments";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TUTOR_EMAIL = "tutor_email";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable = "CREATE TABLE Users ("
                + "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "first_name TEXT NOT NULL, "
                + "last_name TEXT NOT NULL, "
                + "email TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "phone_number TEXT NOT NULL, "
                + "registration_status TEXT, "
                + "organization_name TEXT, "
                + "user_role TEXT CHECK(user_role IN ('Student', 'Tutor', 'Administrator')) NOT NULL"
                + ");";


        db.execSQL(createUsersTable);

        String createTutorCoursesTable = "CREATE TABLE TutorCourses ("
                + "tutor_id INTEGER NOT NULL,"
                + "course_code TEXT NOT NULL,"
                + "PRIMARY KEY (tutor_id, course_code),"
                + "FOREIGN KEY (tutor_id) REFERENCES users(id) ON DELETE CASCADE"
                + ");";

        db.execSQL(createTutorCoursesTable);

        String createEventStudentsTable = "CREATE TABLE EventStudents ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "event_id INTEGER NOT NULL, "
                + "student_id INTEGER NOT NULL, "
                + "registration_status TEXT DEFAULT 'pending', "
                + "FOREIGN KEY (appointment_id) REFERENCES Events(event_id), "
                + "FOREIGN KEY (student_id) REFERENCES Users(user_id)"
                + ");";
        db.execSQL(createEventStudentsTable);

        Cursor cursor = db.rawQuery("SELECT * FROM EventStudents", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Log.d("Database Debug",
                        "Row - Student ID: " + cursor.getInt(cursor.getColumnIndexOrThrow("student_id")) +
                                ", Event ID: " + cursor.getInt(cursor.getColumnIndexOrThrow("event_id")) +
                                ", Status: " + cursor.getString(cursor.getColumnIndexOrThrow("registration_status")));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.e("Database Debug", "No rows found in EventStudents table.");
        }
        Log.d("Schema Debug", "Starting PRAGMA table_info query...");
        Cursor cursor1 = db.rawQuery("PRAGMA table_info(EventStudents);", null);
        if (cursor1 != null && cursor1.moveToFirst()) {
            do {
                Log.d("Schema Debug", "Column: " + cursor1.getString(cursor1.getColumnIndexOrThrow("name")) +
                        ", Type: " + cursor1.getString(cursor1.getColumnIndexOrThrow("type")));
            } while (cursor1.moveToNext());
            cursor1.close();
        } else {
            Log.e("Schema Debug", "No schema info found for EventStudents table.");
        }
        Log.d("Schema Debug", "Finished PRAGMA table_info query...");

        String createRegistrationsTable = "CREATE TABLE Registrations ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "event_id INTEGER NOT NULL, "
                + "student_id INTEGER NOT NULL, "
                + "FOREIGN KEY(event_id) REFERENCES Events(event_id), "
                + "FOREIGN KEY(student_id) REFERENCES Users(user_id)"
                + ");";
        db.execSQL(createRegistrationsTable);


    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE Users ADD COLUMN registration_status TEXT");
        }

        if (oldVersion < 3) {
            String createEventsTable = "CREATE TABLE Events ("
                    + "event_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "title TEXT NOT NULL, "
                    + "description TEXT NOT NULL, "
                    + "date TEXT NOT NULL ,"
                    + "start_time TEXT NOT NULL ,"
                    + "end_time TEXT NOT NULL, "
                    + "event_address TEXT NOT NULL ,"
                    + "eventState TEXT NOT NULL, "
                    + "organizer_id INTEGER NOT NULL, "
                    + "isManualApproval INTEGER DEFAULT 0, "
                    + "FOREIGN KEY (organizer_id) REFERENCES Users(user_id) "
                    + ");";
            db.execSQL(createEventsTable);
        }

        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE Events ADD COLUMN eventState TEXT");
        }
        if (oldVersion < 5) {
            String createEventStudentsTable = "CREATE TABLE EventStudents ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "event_id INTEGER NOT NULL, "
                    + "student_id INTEGER NOT NULL, "
                    + "registration_status TEXT DEFAULT 'pending', "
                    + "FOREIGN KEY (event_id) REFERENCES Events(event_id), "
                    + "FOREIGN KEY (student_id) REFERENCES Users(user_id)"
                    + ");";
            db.execSQL(createEventStudentsTable);
        }

        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE Events ADD COLUMN isManualApproval INTEGER");
        }
        if (oldVersion < 7) {
            String createRegistrationsTable = "CREATE TABLE Registrations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "event_id INTEGER NOT NULL, " +
                    "student_id INTEGER NOT NULL, " +
                    "FOREIGN KEY(event_id) REFERENCES Events(event_id), " +
                    "FOREIGN KEY(student_id) REFERENCES Users(user_id)" +
                    ");";
            db.execSQL(createRegistrationsTable);
        }

    }


    public long addUser(String firstName, String lastName, String email, String password,
                        String phoneNumber, String programOfStudy, String registrationStatus, String highestDegree, String coursesOffered, String userRole) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("email", email);
        values.put("password", password);
        values.put("phone_number", phoneNumber);
        values.put("program_of_study_string", programOfStudy);
        values.put("registration_status", "pending");
        values.put("Highest Degree", highestDegree);
        values.put("Courses Offered", coursesOffered);
        values.put("user_role", userRole);

        long userId = db.insert("Users", null, values);

        // for logcat

        if (userId == -1) {
            Log.e("Debug", "Failed to add user: " + email);
        } else {
            Log.d("Debug", "User added with ID: " + userId);
        }

        return userId;
    }

    public String checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Users", new String[]{"user_role"}, "email=? AND password=?", new String[]{email, password}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String userRole = cursor.getString(cursor.getColumnIndex("user_role"));

            cursor.close();
            return userRole;
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }
    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "Users",
                new String[]{"email"},
                "email = ?",
                new String[]{email},
                null, null, null
        );

        boolean exists = cursor.getCount() > 0;
        return exists;
    }
    public boolean phoneExists(String phoneNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "Users",
                new String[]{"phone_number"},
                "phone_number = ?",
                new String[]{phoneNumber},
                null, null, null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    public String getRegistrationStatus(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Users", new String[]{"registration_status"}, "email = ?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("registration_status"));
            cursor.close();
            return status;
        }
        return null;
    }

    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d("Debug", "Fetching User ID for email: " + email);

        Cursor cursor = db.query(
                "Users",
                new String[]{"user_id"},
                "email = ?",
                new String[]{email},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            Log.d("Debug", "User ID found: " + userId); // Affiche l'ID trouvé
            cursor.close();
            return userId;
        }

        Log.e("Debug", "No User ID found for email: " + email);

        if (cursor != null) {
            cursor.close();
        }
        return -1;
    }
}