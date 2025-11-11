package com.example.proj16v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RowSessionPast extends RecyclerView.Adapter<RowSessionPast.VH> {

    public static class Item {
        public long id;
        public String studentName;
        public String course;
        public String date;
        public String start;
        public String end;
        public Item(long id, String studentName, String course, String date, String start, String end) {
            this.id = id; this.studentName = studentName; this.course = course;
            this.date = date; this.start = start; this.end = end;
        }
    }

    private final List<Item> data = new ArrayList<>();
    public void setData(List<Item> items) { data.clear(); data.addAll(items); notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_row_session_past, parent, false);
        return new VH(row);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int position) {
        Item it = data.get(position);
        h.line1.setText(it.studentName + " • " + it.course);
        h.line2.setText(it.date + "   " + it.start + " - " + it.end);
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView line1, line2;
        VH(View v) { super(v); line1 = v.findViewById(R.id.tvLine1); line2 = v.findViewById(R.id.tvLine2); }
    }
}
