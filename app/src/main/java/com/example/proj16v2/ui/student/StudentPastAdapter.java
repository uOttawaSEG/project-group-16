package com.example.proj16v2.ui.student;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proj16v2.R;
import java.util.*;

public class StudentPastAdapter extends RecyclerView.Adapter<StudentPastAdapter.VH> {

    public static class Item {
        public long id; public String course, status, date, start, end;
        public Item(long id, String course, String status, String date, String start, String end){
            this.id=id; this.course=course; this.status=status; this.date=date; this.start=start; this.end=end;
        }
    }

    private final List<Item> data = new ArrayList<>();
    public void setData(List<Item> items){ data.clear(); data.addAll(items); notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View row = LayoutInflater.from(p.getContext()).inflate(R.layout.row_student_past, p, false);
        return new VH(row);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Item it = data.get(pos);
        h.line1.setText(it.course + " • " + it.status.toUpperCase());
        h.line2.setText(it.date + "  " + it.start + " - " + it.end);
    }

    @Override public int getItemCount(){ return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView line1, line2;
        VH(View v){ super(v); line1=v.findViewById(R.id.tvLine1); line2=v.findViewById(R.id.tvLine2); }
    }
}
