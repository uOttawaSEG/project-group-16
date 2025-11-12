package com.example.proj16v2.ui.tutor.requests;

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

public class RowSessionRequest extends RecyclerView.Adapter<RowSessionRequest.VH> {

    public interface OnAction { void run(long sessionId); }

    public static class Item {
        public long sessionId;
        public String studentName, course, date, start, end;
        public Item(long id, String studentName, String course, String date, String start, String end) {
            this.sessionId = id; this.studentName = studentName; this.course = course;
            this.date = date; this.start = start; this.end = end;
        }
    }

    private final List<Item> data = new ArrayList<>();
    private final OnAction onApprove;
    private final OnAction onReject;

    public RowSessionRequest(OnAction onApprove, OnAction onReject) {
        this.onApprove = onApprove; this.onReject = onReject;
    }

    public void setData(List<Item> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_row_session_request, parent, false);
        return new VH(row);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Item it = data.get(pos);
        h.line1.setText(it.studentName + " • " + it.course);
        h.line2.setText(it.date + "   " + it.start + " - " + it.end);

        h.btnApprove.setOnClickListener(v -> onApprove.run(it.sessionId));
        h.btnReject.setOnClickListener(v -> onReject.run(it.sessionId));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView line1, line2;
        Button btnApprove, btnReject;
        VH(View v) {
            super(v);
            line1 = v.findViewById(R.id.tvLine1);
            line2 = v.findViewById(R.id.tvLine2);
            btnApprove = v.findViewById(R.id.btnApprove);
            btnReject  = v.findViewById(R.id.btnReject);
        }
    }
}
