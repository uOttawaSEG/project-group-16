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
import java.util.function.LongConsumer;

public class SlotsAdapter extends RecyclerView.Adapter<SlotsAdapter.VH> {

    public static class SlotItem {
        public long slotId;
        public String date;
        public String start;
        public String end;
        public SlotItem(long id, String d, String s, String e) {
            slotId = id; date = d; start = s; end = e;
        }
    }

    private final List<SlotItem> data = new ArrayList<>();
    private final LongConsumer onDelete;

    public SlotsAdapter(LongConsumer onDelete) {
        this.onDelete = onDelete;
    }

    public void setData(List<SlotItem> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_availability_slot, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        SlotItem s = data.get(pos);
        h.tvDate.setText(s.date);
        h.tvRange.setText(s.start + " - " + s.end);
        h.btnDelete.setOnClickListener(v -> onDelete.accept(s.slotId));
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvRange; Button btnDelete;
        VH(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvSlotDate);
            tvRange = v.findViewById(R.id.tvSlotTimeRange);
            btnDelete = v.findViewById(R.id.btnDeleteSlot);
        }
    }
}

