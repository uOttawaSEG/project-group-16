package com.example.proj16v2.ui.student;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.R;

import java.util.*;

public class StudentPastAdapter extends RecyclerView.Adapter<StudentPastAdapter.VH> {

    public static class Item {
        public long sessionId;
        public String course, status, date, start, end;
        public boolean canRate;

        public Item(long sessionId,
                    String course,
                    String status,
                    String date,
                    String start,
                    String end,
                    boolean canRate) {
            this.sessionId = sessionId;
            this.course = course;
            this.status = status;
            this.date = date;
            this.start = start;
            this.end = end;
            this.canRate = canRate;
        }
    }

    private final List<Item> data = new ArrayList<>();
    private final Context context;

    public StudentPastAdapter(Context context) {
        this.context = context;
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
                .inflate(R.layout.row_student_past, parent, false);
        return new VH(row);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Item it = data.get(position);
        h.line1.setText(it.course + " • " + it.status.toUpperCase());
        h.line2.setText(it.date + "  " + it.start + " - " + it.end);

        if (it.canRate) {
            h.btnRate.setVisibility(View.VISIBLE);
            h.btnRate.setOnClickListener(v -> {
                Intent i = new Intent(context, RateTutorActivity.class);
                i.putExtra("session_id", it.sessionId);
                context.startActivity(i);
            });
        } else {
            h.btnRate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView line1, line2;
        Button btnRate;

        VH(View v) {
            super(v);
            line1 = v.findViewById(R.id.tvLine1);
            line2 = v.findViewById(R.id.tvLine2);
            btnRate = v.findViewById(R.id.btnRate);
        }
    }
}
