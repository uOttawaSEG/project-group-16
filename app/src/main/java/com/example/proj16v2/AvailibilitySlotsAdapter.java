package com.example.proj16v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AvailibilitySlotsAdapter extends RecyclerView.Adapter<AvailibilitySlotsAdapter.VH> {

    public interface OnDeleteClick { void onDelete(long slotId); }

    public static class Item {
        public long slotId; public String date; public String start; public String end;
        public Item(long id, String d, String s, String e) { slotId=id; date=d; start=s; end=e; }
    }

    private final List<Item> data = new ArrayList<>();
    private final OnDeleteClick onDelete;

    public AvailibilitySlotsAdapter(OnDeleteClick onDelete) { this.onDelete = onDelete; }

    public void setData(List<Item> items) { data.clear(); data.addAll(items); notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                // If your row item XML has a different name, change the layout below:
                .inflate(R.layout.activity_row_availibility_slot, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Item s = data.get(pos);
        h.tvDate.setText(s.date);
        h.tvRange.setText(s.start + " - " + s.end);
        h.btnDelete.setOnClickListener(v -> onDelete.onDelete(s.slotId));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvRange; Button btnDelete;
        VH(View v) {
            super(v);
            tvDate   = v.findViewById(R.id.tvSlotDate);
            tvRange  = v.findViewById(R.id.tvSlotTimeRange);
            btnDelete = v.findViewById(R.id.btnDeleteSlot);
        }
    }
}
