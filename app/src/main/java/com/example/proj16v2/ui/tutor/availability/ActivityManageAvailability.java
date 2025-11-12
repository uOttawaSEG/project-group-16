package com.example.proj16v2.ui.tutor.availability;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.Data.DatabaseHelper;
import com.example.proj16v2.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityManageAvailability extends AppCompatActivity {

    private DatabaseHelper db;

    private EditText etDate, etStartTime, etEndTime;
    private CheckBox cbManual;
    private RecyclerView rv;
    private SlotsAdapter adapter;

    private int tutorId;

    // API-24 friendly formatters
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat TF = new SimpleDateFormat("HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_availability);

        db = new DatabaseHelper(this);
        tutorId = getIntent().getIntExtra("user_id", -1);

        // bind views (match XML IDs)
        etDate      = findViewById(R.id.etDate);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime   = findViewById(R.id.etEndTime);
        cbManual    = findViewById(R.id.cbManual);

        Button btnPickDate  = findViewById(R.id.btnPickDate);
        Button btnPickStart = findViewById(R.id.btnPickStart);
        Button btnPickEnd   = findViewById(R.id.btnPickEnd);
        Button btnCreate    = findViewById(R.id.btnCreateSlot);
        Button btnBack = findViewById(R.id.btnBack);

        rv = findViewById(R.id.rvSlots);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SlotsAdapter(this::deleteSlot);
        rv.setAdapter(adapter);

        // default date -> today
        etDate.setText(DF.format(new Date()));

        // pickers
        btnPickDate.setOnClickListener(v -> pickDate());
        btnPickStart.setOnClickListener(v -> pickTime(etStartTime));
        btnPickEnd.setOnClickListener(v -> pickTime(etEndTime));
        btnBack.setOnClickListener(v -> finish());

        // create
        btnCreate.setOnClickListener(v -> saveSlot());

        // load today's slots
        reload();
    }

    private void pickDate() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog d = new DatePickerDialog(
                this,
                (view, y, m, day) -> etDate.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, day)),
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        d.show();
    }

    private void pickTime(EditText target) {
        final Calendar c = Calendar.getInstance();
        TimePickerDialog t = new TimePickerDialog(
                this,
                (view, h, m) -> target.setText(String.format(Locale.US, "%02d:%02d", h, m)),
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
        );
        t.show();
    }

    private boolean isThirtyMinute(Date time) {
        // time is a Date whose hours/minutes we care about; check minutes are 0 or 30
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int minutes = cal.get(Calendar.MINUTE);
        return minutes == 0 || minutes == 30;
    }

    private boolean isToday(Date dateOnly) {
        Calendar a = Calendar.getInstance();
        a.setTime(dateOnly);
        Calendar b = Calendar.getInstance();
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    private Date parseDate(String s) throws ParseException {
        // strip time; return date at midnight
        Date d = DF.parse(s);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private Date parseTimeOnDate(String dateStr, String timeStr) throws ParseException {
        // combine to a single Date (yyyy-MM-dd HH:mm)
        Date date = DF.parse(dateStr);
        Date time = TF.parse(timeStr);

        Calendar cd = Calendar.getInstance();
        cd.setTime(date);

        Calendar ct = Calendar.getInstance();
        ct.setTime(time);

        cd.set(Calendar.HOUR_OF_DAY, ct.get(Calendar.HOUR_OF_DAY));
        cd.set(Calendar.MINUTE, ct.get(Calendar.MINUTE));
        cd.set(Calendar.SECOND, 0);
        cd.set(Calendar.MILLISECOND, 0);
        return cd.getTime();
    }

    private void saveSlot() {
        String d = etDate.getText().toString().trim();
        String s = etStartTime.getText().toString().trim();
        String e = etEndTime.getText().toString().trim();
        boolean manual = cbManual.isChecked();

        if (TextUtils.isEmpty(d) || TextUtils.isEmpty(s) || TextUtils.isEmpty(e)) {
            Toast.makeText(this, "Pick date/start/end", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse inputs
            Date dateOnly  = parseDate(d);
            Date startDate = parseTimeOnDate(d, s);
            Date endDate   = parseTimeOnDate(d, e);

            // 1) Date must not be in the past
            Calendar todayZero = Calendar.getInstance();
            todayZero.set(Calendar.HOUR_OF_DAY, 0);
            todayZero.set(Calendar.MINUTE, 0);
            todayZero.set(Calendar.SECOND, 0);
            todayZero.set(Calendar.MILLISECOND, 0);
            if (dateOnly.before(todayZero.getTime())) {
                Toast.makeText(this, "Date cannot be in the past.", Toast.LENGTH_LONG).show();
                return;
            }

            // 2) If today, start time must not be in the past
            if (isToday(dateOnly) && startDate.before(new Date())) {
                Toast.makeText(this, "Start time has already passed.", Toast.LENGTH_LONG).show();
                return;
            }

            // 3) 30-minute increments
            if (!isThirtyMinute(startDate) || !isThirtyMinute(endDate)) {
                Toast.makeText(this, "Times must be on 30-minute increments (:00 or :30).", Toast.LENGTH_LONG).show();
                return;
            }

            // 4) End after start
            if (!endDate.after(startDate)) {
                Toast.makeText(this, "End must be after start.", Toast.LENGTH_LONG).show();
                return;
            }

            // 5) Overlap check in DB
            String sStr = TF.format(startDate);
            String eStr = TF.format(endDate);
            String dStr = DF.format(dateOnly);

            if (db.slotOverlaps(tutorId, dStr, sStr, eStr)) {
                Toast.makeText(this, "Overlaps an existing slot.", Toast.LENGTH_LONG).show();
                return;
            }

            long rowId = db.addSlot(tutorId, dStr, sStr, eStr, manual);
            if (rowId == -1) {
                Toast.makeText(this, "Failed to save slot.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Slot saved.", Toast.LENGTH_SHORT).show();
                reload(); // refresh list for selected date
            }

        } catch (ParseException ex) {
            Toast.makeText(this, "Use formats YYYY-MM-DD and HH:MM (24h).", Toast.LENGTH_LONG).show();
        }
    }

    private void deleteSlot(long slotId) {
        int rows = db.deleteSlot(slotId);
        if (rows > 0) {
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
            reload();
        } else {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void reload() {
        String d = etDate.getText().toString().trim();
        List<SlotsAdapter.SlotItem> items = new ArrayList<>();

        // Query today’s (or selected date’s) slots for this tutor
        final String[] cols = new String[]{"slot_id", "date", "start_time", "end_time"};
        final String   sel  = "tutor_id=? AND date=?";
        final String[] args = new String[]{ String.valueOf(tutorId), d };
        final String   ord  = "start_time ASC";

        android.database.Cursor c = db.getReadableDatabase().query(
                "AvailabilitySlots", cols, sel, args, null, null, ord);

        while (c.moveToNext()) {
            items.add(new SlotsAdapter.SlotItem(
                    c.getLong(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3)
            ));
        }
        c.close();

        adapter.setData(items);
    }
}
