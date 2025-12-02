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

public class StudentRequestsAdapter extends RecyclerView.Adapter<StudentRequestsAdapter.VH> {

    public interface OnCancel {
        void run(long sessionId);
    }

    public static class Item {
        public long sessionId;
        public String course;
        public String status;
        public String date;
        public String start;
        public String end;
        public boolean canCancel;

        public Item(long sessionId, String course, String status,
                    String date, String start, String end, boolean canCancel) {
            this.sessionId = sessionId;
            this.course = course;
            this.status = status;
            this.date = date;
            this.start = start;
            this.end = end;
            this.canCancel = canCancel;
        }
    }

    private final List<Item> data = new ArrayList<>();
    private final OnCancel onCancel;

    public StudentRequestsAdapter(OnCancel onCancel) {
        this.onCancel = onCancel;
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
                .inflate(R.layout.row_student_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Item it = data.get(position);
        h.line1.setText(it.course + " • " + it.status.toUpperCase());
        h.line2.setText(it.date + "  " + it.start + " - " + it.end);

        if (it.canCancel) {
            h.btnCancel.setVisibility(View.VISIBLE);
            h.btnCancel.setOnClickListener(v -> onCancel.run(it.sessionId));
        } else {
            h.btnCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView line1, line2;
        Button btnCancel;

        VH(View v) {
            super(v);
            line1 = v.findViewById(R.id.tvLine1);
            line2 = v.findViewById(R.id.tvLine2);
            btnCancel = v.findViewById(R.id.btnCancel);
        }
    }
}
