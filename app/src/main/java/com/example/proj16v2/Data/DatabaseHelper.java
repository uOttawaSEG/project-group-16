package com.example.proj16v2.Data;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "OTAMS.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // ---------------- USERS ----------------
        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "first_name TEXT NOT NULL, "
                + "last_name TEXT NOT NULL, "
                + "email TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "phone_number TEXT NOT NULL, "
                + "programOfStudy TEXT, "
                + "registration_status TEXT, "
                + "coursesOffered TEXT, "
                + "highestDegree TEXT, "
                + "user_role TEXT CHECK(user_role IN ('Student', 'Tutor', 'Administrator')) NOT NULL"
                + ");";
        db.execSQL(createUsersTable);

        // ------------- TUTOR COURSES -------------
        String createTutorCoursesTable =
                "CREATE TABLE IF NOT EXISTS TutorCourses ("
                        + "tutor_id INTEGER NOT NULL, "
                        + "course_name TEXT NOT NULL, "
                        + "PRIMARY KEY (tutor_id, course_name), "
                        + "FOREIGN KEY (tutor_id) REFERENCES Users(user_id) ON DELETE CASCADE"
                        + ");";
        db.execSQL(createTutorCoursesTable);

        // --------- AVAILABILITY SLOTS (30-min blocks) ---------
        String createAvailabilitySlots = "CREATE TABLE IF NOT EXISTS AvailabilitySlots ("
                + "slot_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "tutor_id INTEGER NOT NULL, "
                + "date TEXT NOT NULL, "           // YYYY-MM-DD
                + "start_time TEXT NOT NULL, "      // HH:MM
                + "end_time TEXT NOT NULL, "        // HH:MM
                + "is_manual_approval INTEGER NOT NULL DEFAULT 1, "
                + "UNIQUE (tutor_id, date, start_time), "
                + "FOREIGN KEY (tutor_id) REFERENCES Users(user_id) ON DELETE CASCADE"
                + ");";
        db.execSQL(createAvailabilitySlots);

        // ------------------ SESSIONS ------------------
        String createSessions = "CREATE TABLE IF NOT EXISTS Sessions ("
                + "session_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "student_id INTEGER NOT NULL, "
                + "tutor_id INTEGER NOT NULL, "
                + "course_code TEXT NOT NULL, "
                + "date TEXT NOT NULL, "            // YYYY-MM-DD
                + "start_time TEXT NOT NULL, "      // HH:MM
                + "end_time TEXT NOT NULL, "        // HH:MM
                + "status TEXT NOT NULL, "          // requested, approved, rejected, cancelled, completed
                + "created_at TEXT NOT NULL, "      // ISO timestamp string
                + "cancelled_by TEXT, "
                + "cancelled_at TEXT, "
                + "UNIQUE (tutor_id, date, start_time), "
                + "FOREIGN KEY (student_id) REFERENCES Users(user_id), "
                + "FOREIGN KEY (tutor_id) REFERENCES Users(user_id)"
                + ");";
        db.execSQL(createSessions);

        // ------------------ RATINGS ------------------
        String createRatings = "CREATE TABLE IF NOT EXISTS Ratings ("
                + "rating_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "session_id INTEGER NOT NULL UNIQUE, "
                + "student_id INTEGER NOT NULL, "
                + "tutor_id INTEGER NOT NULL, "
                + "stars INTEGER NOT NULL CHECK (stars BETWEEN 1 AND 5), "
                + "comment TEXT, "
                + "created_at TEXT NOT NULL, "
                + "FOREIGN KEY (session_id) REFERENCES Sessions(session_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (student_id) REFERENCES Users(user_id), "
                + "FOREIGN KEY (tutor_id) REFERENCES Users(user_id)"
                + ");";
        db.execSQL(createRatings);

        // ------------------ INDEXES ------------------
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_slots_tutor_date ON AvailabilitySlots(tutor_id, date);");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_sessions_tutor_date ON Sessions(tutor_id, date);");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_sessions_student ON Sessions(student_id);");
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ratings_tutor ON Ratings(tutor_id);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Guarded, idempotent upgrades. Keep try/catch so re-running is safe.
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE Users ADD COLUMN registration_status TEXT");
            } catch (Exception ignored) {
            }
        }
        if (oldVersion < 3) {
            String createAvailabilitySlots = "CREATE TABLE IF NOT EXISTS AvailabilitySlots ("
                    + "slot_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "tutor_id INTEGER NOT NULL, "
                    + "date TEXT NOT NULL, "
                    + "start_time TEXT NOT NULL, "
                    + "end_time TEXT NOT NULL, "
                    + "is_manual_approval INTEGER NOT NULL DEFAULT 1, "
                    + "UNIQUE (tutor_id, date, start_time), "
                    + "FOREIGN KEY (tutor_id) REFERENCES Users(user_id) ON DELETE CASCADE"
                    + ");";
            db.execSQL(createAvailabilitySlots);
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_slots_tutor_date ON AvailabilitySlots(tutor_id, date);");
        }
        if (oldVersion < 4) {
            String createSessions = "CREATE TABLE IF NOT EXISTS Sessions ("
                    + "session_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "student_id INTEGER NOT NULL, "
                    + "tutor_id INTEGER NOT NULL, "
                    + "course_code TEXT NOT NULL, "
                    + "date TEXT NOT NULL, "
                    + "start_time TEXT NOT NULL, "
                    + "end_time TEXT NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "created_at TEXT NOT NULL, "
                    + "cancelled_by TEXT, "
                    + "cancelled_at TEXT, "
                    + "UNIQUE (tutor_id, date, start_time), "
                    + "FOREIGN KEY (student_id) REFERENCES Users(user_id), "
                    + "FOREIGN KEY (tutor_id) REFERENCES Users(user_id)"
                    + ");";
            db.execSQL(createSessions);
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_sessions_tutor_date ON Sessions(tutor_id, date);");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_sessions_student ON Sessions(student_id);");
        }
        if (oldVersion < 5) {
            String createRatings = "CREATE TABLE IF NOT EXISTS Ratings ("
                    + "rating_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "session_id INTEGER NOT NULL UNIQUE, "
                    + "student_id INTEGER NOT NULL, "
                    + "tutor_id INTEGER NOT NULL, "
                    + "stars INTEGER NOT NULL CHECK (stars BETWEEN 1 AND 5), "
                    + "comment TEXT, "
                    + "created_at TEXT NOT NULL, "
                    + "FOREIGN KEY (session_id) REFERENCES Sessions(session_id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (student_id) REFERENCES Users(user_id), "
                    + "FOREIGN KEY (tutor_id) REFERENCES Users(user_id)"
                    + ");";
            db.execSQL(createRatings);
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_ratings_tutor ON Ratings(tutor_id);");
        }
    }

    /* ===================== USER API (matches your usage) ===================== */

    public long addUser(String firstName, String lastName, String email, String password,
                        String phoneNumber, String programOfStudy, String registrationStatus,
                        String highestDegree, String coursesOffered, String userRole) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("email", email);
        values.put("password", password);
        values.put("phone_number", phoneNumber);
        values.put("programOfStudy", programOfStudy);
        values.put("registration_status", registrationStatus == null ? "pending" : registrationStatus);
        values.put("highestDegree", highestDegree);
        values.put("coursesOffered", coursesOffered);
        values.put("user_role", userRole);

        long userId = db.insert("Users", null, values);

        if (userId == -1) {
            Log.e("DB", "Failed to add user: " + email);
        } else {
            Log.d("DB", "User added with ID: " + userId);
        }
        return userId;
    }

    public String checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                "Users",
                new String[]{"user_role"},
                "email=? AND password=?",
                new String[]{email, password},
                null, null, null);

        String role = null;
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            String userRole = cursor.getString(cursor.getColumnIndex("user_role"));
            role = userRole;
        }
        if (cursor != null) cursor.close();
        return role;
    }

    public boolean emailExists(String email) {
        Cursor cursor = getReadableDatabase().query(
                "Users",
                new String[]{"email"},
                "email = ?",
                new String[]{email},
                null, null, null
        );
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public boolean phoneExists(String phoneNumber) {
        Cursor cursor = getReadableDatabase().query(
                "Users",
                new String[]{"phone_number"},
                "phone_number = ?",
                new String[]{phoneNumber},
                null, null, null
        );
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public String getRegistrationStatus(String email) {
        Cursor cursor = getReadableDatabase().query(
                "Users",
                new String[]{"registration_status"},
                "email = ?",
                new String[]{email},
                null, null, null
        );
        String status = null;
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            String s = cursor.getString(cursor.getColumnIndex("registration_status"));
            status = s;
        }
        if (cursor != null) cursor.close();
        return status;
    }

    public int getUserId(String email) {
        Cursor cursor = getReadableDatabase().query(
                "Users",
                new String[]{"user_id"},
                "email = ?",
                new String[]{email},
                null, null, null
        );
        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
        }
        if (cursor != null) cursor.close();
        return userId;
    }

    public Cursor getUsers(String status, @Nullable String role) {
        String where = role == null ? "registration_status=?" : "registration_status=? AND user_role=?";
        String[] args = role == null ? new String[]{status} : new String[]{status, role};

        return getReadableDatabase().query(
                "Users",
                new String[]{"user_id", "first_name", "last_name", "email", "user_role", "registration_status"},
                where,
                args,
                null, null,
                "last_name ASC"
        );
    }

    public int setUserStatus(long userId, String newStatus) {
        ContentValues v = new ContentValues();
        v.put("registration_status", newStatus);
        return getWritableDatabase().update(
                "Users",
                v,
                "user_id=?",
                new String[]{String.valueOf(userId)}
        );
    }

    /* ===================== TUTOR AVAILABILITY API ===================== */

    /**
     * Detect overlap: NOT( newStart >= existingEnd OR newEnd <= existingStart )
     */
    public boolean slotOverlaps(long tutorId, String date, String start, String end) {
        String sql =
                "SELECT 1 FROM AvailabilitySlots "
                        + "WHERE tutor_id=? AND date=? "
                        + "AND NOT( ? >= end_time OR ? <= start_time ) "
                        + "LIMIT 1";
        Cursor c = getReadableDatabase().rawQuery(sql,
                new String[]{String.valueOf(tutorId), date, start, end});
        boolean conflict = (c != null && c.moveToFirst());
        if (c != null) c.close();
        return conflict;
    }

    public long addSlot(long tutorId, String date, String start, String end, boolean manualApproval) {
        ContentValues v = new ContentValues();
        v.put("tutor_id", tutorId);
        v.put("date", date);
        v.put("start_time", start);
        v.put("end_time", end);
        v.put("is_manual_approval", manualApproval ? 1 : 0);
        return getWritableDatabase().insert("AvailabilitySlots", null, v);
    }

    public Cursor getSlotsForDate(long tutorId, String date) {
        return getReadableDatabase().query(
                "AvailabilitySlots",
                new String[]{"slot_id", "tutor_id", "date", "start_time", "end_time", "is_manual_approval"},
                "tutor_id=? AND date=?",
                new String[]{String.valueOf(tutorId), date},
                null, null,
                "start_time ASC"
        );
    }

    public Cursor getAllSlots(long tutorId) {
        return getReadableDatabase().query(
                "AvailabilitySlots",
                new String[]{"slot_id", "tutor_id", "date", "start_time", "end_time", "is_manual_approval"},
                "tutor_id=?",
                new String[]{String.valueOf(tutorId)},
                null, null,
                "date ASC, start_time ASC"
        );
    }

    public int deleteSlot(long slotId) {
        return getWritableDatabase().delete("AvailabilitySlots", "slot_id=?", new String[]{String.valueOf(slotId)});
    }

    /* ===================== SESSIONS API ===================== */

    public long createSession(long studentId, long tutorId, String courseCode,
                              String date, String startTime, String endTime,
                              String status, String createdAt) {
        ContentValues v = new ContentValues();
        v.put("student_id", studentId);
        v.put("tutor_id", tutorId);
        v.put("course_code", courseCode);
        v.put("date", date);
        v.put("start_time", startTime);
        v.put("end_time", endTime);
        v.put("status", status);        // e.g., "requested"
        v.put("created_at", createdAt); // ISO string
        return getWritableDatabase().insert("Sessions", null, v);
    }

    public int updateSessionStatus(long sessionId, String status,
                                   @Nullable String cancelledBy, @Nullable String cancelledAt) {
        ContentValues v = new ContentValues();
        v.put("status", status);
        if (cancelledBy != null) v.put("cancelled_by", cancelledBy);
        if (cancelledAt != null) v.put("cancelled_at", cancelledAt);
        return getWritableDatabase().update("Sessions", v, "session_id=?", new String[]{String.valueOf(sessionId)});
    }

    public Cursor getSessionsForStudent(long studentId) {
        return getReadableDatabase().query(
                "Sessions",
                new String[]{"session_id", "student_id", "tutor_id", "course_code", "date", "start_time", "end_time", "status", "created_at", "cancelled_by", "cancelled_at"},
                "student_id=?",
                new String[]{String.valueOf(studentId)},
                null, null,
                "date DESC, start_time DESC"
        );
    }

    public Cursor getSessionsForTutor(long tutorId) {
        return getReadableDatabase().query(
                "Sessions",
                new String[]{"session_id", "student_id", "tutor_id", "course_code", "date", "start_time", "end_time", "status", "created_at", "cancelled_by", "cancelled_at"},
                "tutor_id=?",
                new String[]{String.valueOf(tutorId)},
                null, null,
                "date DESC, start_time DESC"
        );
    }

    /* ===================== RATINGS API ===================== */

    public long addOrUpdateRating(long sessionId, long studentId, long tutorId,
                                  int stars, @Nullable String comment, String createdAt) {
        // try update first (session_id is UNIQUE)
        ContentValues v = new ContentValues();
        v.put("session_id", sessionId);
        v.put("student_id", studentId);
        v.put("tutor_id", tutorId);
        v.put("stars", stars);
        v.put("comment", comment);
        v.put("created_at", createdAt);

        // replace (upsert) pattern using CONFLICT REPLACE requires table to be created with ON CONFLICT, so use manual approach:
        // Try insert; if fails due to UNIQUE, do update.
        long id = getWritableDatabase().insert("Ratings", null, v);
        if (id == -1) {
            // conflict: update
            int rows = getWritableDatabase().update("Ratings", v, "session_id=?", new String[]{String.valueOf(sessionId)});
            if (rows > 0) {
                // fetch rating_id for return
                Cursor c = getReadableDatabase().query("Ratings", new String[]{"rating_id"}, "session_id=?", new String[]{String.valueOf(sessionId)}, null, null, null);
                long rid = -1;
                if (c != null && c.moveToFirst()) rid = c.getLong(0);
                if (c != null) c.close();
                return rid;
            }
        }
        return id;
    }

    public Cursor getRatingsForTutor(long tutorId) {
        return getReadableDatabase().query(
                "Ratings",
                new String[]{"rating_id", "session_id", "student_id", "tutor_id", "stars", "comment", "created_at"},
                "tutor_id=?",
                new String[]{String.valueOf(tutorId)},
                null, null,
                "created_at DESC"
        );
    }

    public float getAverageRatingForTutor(long tutorId) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT AVG(stars) FROM Ratings WHERE tutor_id=?",
                new String[]{String.valueOf(tutorId)}
        );
        float avg = 0f;
        if (c != null && c.moveToFirst()) {
            avg = c.isNull(0) ? 0f : (float) c.getDouble(0);
        }
        if (c != null) c.close();
        return avg;
    }

    /* ===================== D3: PENDING REQUESTS HELPERS ===================== */

    /** List all pending requests for a tutor, joined with student names. */
    public Cursor getPendingRequestsForTutor(long tutorId) {
        String sql =
                "SELECT s.session_id, s.student_id, s.tutor_id, s.course_code, " +
                        "       s.date, s.start_time, s.end_time, " +
                        "       u.first_name, u.last_name " +
                        "FROM Sessions s " +
                        "JOIN Users u ON u.user_id = s.student_id " +
                        "WHERE s.tutor_id = ? AND s.status = 'requested' " +
                        "ORDER BY s.date ASC, s.start_time ASC";
        return getReadableDatabase().rawQuery(sql, new String[]{ String.valueOf(tutorId) });
    }

    /** Optional: show rejected ones for the tutor (nice to have for your “Rejected” list). */
    public Cursor getRejectedRequestsForTutor(long tutorId) {
        String sql =
                "SELECT s.session_id, s.student_id, s.tutor_id, s.course_code, " +
                        "       s.date, s.start_time, s.end_time, " +
                        "       u.first_name, u.last_name " +
                        "FROM Sessions s " +
                        "JOIN Users u ON u.user_id = s.student_id " +
                        "WHERE s.tutor_id = ? AND s.status = 'rejected' " +
                        "ORDER BY s.date DESC, s.start_time DESC";
        return getReadableDatabase().rawQuery(sql, new String[]{ String.valueOf(tutorId) });
    }

    /**
     * Prevent double-booking on approval: returns true if the proposed time overlaps an
     * already approved session for this tutor on that date.
     * Overlap condition: NOT( newStart >= existingEnd OR newEnd <= existingStart )
     */
    public boolean tutorHasApprovedOverlap(long tutorId, String date, String start, String end) {
        String sql =
                "SELECT 1 FROM Sessions " +
                        "WHERE tutor_id=? AND date=? " +
                        "  AND status IN ('approved') " +
                        "  AND NOT( ? >= end_time OR ? <= start_time ) " +
                        "LIMIT 1";
        Cursor c = getReadableDatabase().rawQuery(sql,
                new String[]{ String.valueOf(tutorId), date, start, end });
        boolean conflict = (c != null && c.moveToFirst());
        if (c != null) c.close();
        return conflict;
    }

    /** Convenience wrappers if you want explicit names in your UI layer. */
    public int approveRequest(long sessionId) {
        return updateSessionStatus(sessionId, "approved", null, null);
    }
    public int rejectRequest(long sessionId) {
        return updateSessionStatus(sessionId, "rejected", null, null);
    }

    // Upcoming = approved sessions that are later today or future dates
    public Cursor getUpcomingSessionsForTutor(long tutorId, String todayDate, String nowTime) {
        String sql =
                "SELECT s.session_id, s.student_id, s.course_code, s.date, s.start_time, s.end_time, " +
                        "       u.first_name, u.last_name " +
                        "FROM Sessions s " +
                        "JOIN Users u ON u.user_id = s.student_id " +
                        "WHERE s.tutor_id=? AND s.status='approved' AND " +
                        "      (s.date > ? OR (s.date = ? AND s.start_time >= ?)) " +
                        "ORDER BY s.date ASC, s.start_time ASC";
        return getReadableDatabase().rawQuery(sql,
                new String[]{ String.valueOf(tutorId), todayDate, todayDate, nowTime });
    }

    // Past = completed OR (approved sessions that already ended)
    public Cursor getPastSessionsForTutor(long tutorId, String todayDate, String nowTime) {
        String sql =
                "SELECT s.session_id, s.student_id, s.course_code, s.date, s.start_time, s.end_time, " +
                        "       s.status, u.first_name, u.last_name " +
                        "FROM Sessions s " +
                        "JOIN Users u ON u.user_id = s.student_id " +
                        "WHERE s.tutor_id=? AND (" +
                        "      s.status='completed' " +
                        "   OR (s.status='approved' AND (s.date < ? OR (s.date = ? AND s.end_time < ?)))" +
                        ") ORDER BY s.date DESC, s.start_time DESC";
        return getReadableDatabase().rawQuery(sql,
                new String[]{ String.valueOf(tutorId), todayDate, todayDate, nowTime });
    }

    public Cursor getOpenSlots() {
        // A slot is "open" if there is NO session with same tutor/date/start in requested/approved
        String sql =
                "SELECT a.slot_id, a.tutor_id, a.date, a.start_time, a.end_time, a.is_manual_approval " +
                        "FROM AvailabilitySlots a " +
                        "LEFT JOIN Sessions s " +
                        "  ON s.tutor_id = a.tutor_id AND s.date = a.date AND s.start_time = a.start_time " +
                        "  AND s.status IN ('requested','approved') " +
                        "WHERE s.session_id IS NULL " +
                        "ORDER BY a.date ASC, a.start_time ASC";
        return getReadableDatabase().rawQuery(sql, null);
    }


}
