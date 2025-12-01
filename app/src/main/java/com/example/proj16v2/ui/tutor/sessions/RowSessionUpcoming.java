package com.example.proj16v2.ui.tutor.sessions;

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

public class RowSessionUpcoming extends RecyclerView.Adapter<RowSessionUpcoming.VH> {

    // Callbacks to Activity
    public interface OnCancel   { void run(long sessionId); }
    public interface OnComplete{ void run(long sessionId); }

    public static class Item {
        public long id;
        public String student, course, date, start, end;
        public Item(long id, String student, String course,
                    String date, String start, String end) {
            this.id = id; this.student = student; this.course = course;
            this.date = date; this.start = start; this.end = end;
        }
    }

    private final List<Item> data = new ArrayList<>();
    private final OnCancel onCancel;
    private final OnComplete onComplete;

    public RowSessionUpcoming(OnCancel onCancel, OnComplete onComplete) {
        this.onCancel = onCancel;
        this.onComplete = onComplete;
    }

    public void setData(List<Item> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_row_session_upcoming, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Item it = data.get(position);
        h.line1.setText(it.student + " • " + it.course);
        h.line2.setText(it.date + "   " + it.start + " - " + it.end);

        // ↓↓↓ THIS is the "adapter bind" for the buttons ↓↓↓
        h.btnCancel.setOnClickListener(v -> onCancel.run(it.id));
        h.btnMarkCompleted.setOnClickListener(v -> onComplete.run(it.id));
        // ↑↑↑ you could add visibility logic here later if you only want
        //     the button visible after the end time.
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView line1, line2;
        Button btnCancel, btnMarkCompleted;

        VH(View v) {
            super(v);
            line1 = v.findViewById(R.id.tvLine1);
            line2 = v.findViewById(R.id.tvLine2);
            btnCancel = v.findViewById(R.id.btnCancel);
            btnMarkCompleted = v.findViewById(R.id.btnMarkCompleted);
        }
    }
}
