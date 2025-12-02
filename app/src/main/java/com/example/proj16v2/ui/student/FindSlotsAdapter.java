package com.example.proj16v2.ui.student;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FindSlotsAdapter extends RecyclerView.Adapter<FindSlotsAdapter.VH> {

    // Callback to StudentFindSlots when user presses Request
    public interface OnRequest {
        void run(Item slot);
    }

    public static class Item {
        public long slotId;
        public long tutorId;
        public String courseCode;
        public String date;
        public String start;
        public String end;
        public boolean isManual;
        public String tutorName;
        public float avgRating;

        public Item(long slotId,
                    long tutorId,
                    String courseCode,
                    String date,
                    String start,
                    String end,
                    boolean isManual,
                    String tutorName,
                    float avgRating) {
            this.slotId = slotId;
            this.tutorId = tutorId;
            this.courseCode = courseCode;
            this.date = date;
            this.start = start;
            this.end = end;
            this.isManual = isManual;
            this.tutorName = tutorName;
            this.avgRating = avgRating;
        }
    }

    private final List<Item> data = new ArrayList<>();
    private final OnRequest onRequest;

    public FindSlotsAdapter(OnRequest onRequest) {
        this.onRequest = onRequest;
    }

    public void setData(List<Item> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_find_slot, parent, false);
        return new VH(row);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Item it = data.get(pos);

        String ratingText;
        if (it.avgRating <= 0f) {
            ratingText = "no ratings yet";
        } else {
            ratingText = String.format(Locale.US, "%.1f★", it.avgRating);
        }

        // line1: Tutor Name (+ rating) • Auto/Manual • COURSE
        h.line1.setText(String.format(
                Locale.US,
                "%s (%s) • %s • %s",
                it.tutorName == null ? ("Tutor #" + it.tutorId) : it.tutorName,
                ratingText,
                it.isManual ? "Manual" : "Auto",
                it.courseCode
        ));

        // line2: date + time range
        h.line2.setText(it.date + "  " + it.start + " - " + it.end);

        // Request button callback
        h.btnRequest.setOnClickListener(v -> onRequest.run(it));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView line1, line2;
        Button btnRequest;

        VH(View v) {
            super(v);
            line1 = v.findViewById(R.id.tvLine1);
            line2 = v.findViewById(R.id.tvLine2);
            btnRequest = v.findViewById(R.id.btnRequest);
        }
    }
}
