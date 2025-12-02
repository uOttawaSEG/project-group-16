package com.example.proj16v2.ui.student;

import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proj16v2.R;
import java.util.*;
import java.util.Locale;


public class FindSlotsAdapter extends RecyclerView.Adapter<FindSlotsAdapter.VH> {

    public static class Item {
        public long slotId;
        public long tutorId;
        public String date;
        public String start;
        public String end;
        public boolean isManual;
        public String tutorName;
        public float avgRating;
        public String course;   // course filter that matched, if any

        public Item(long slotId,
                    long tutorId,
                    String date,
                    String start,
                    String end,
                    boolean isManual,
                    String tutorName,
                    float avgRating,
                    String course) {
            this.slotId = slotId;
            this.tutorId = tutorId;
            this.date = date;
            this.start = start;
            this.end = end;
            this.isManual = isManual;
            this.tutorName = tutorName;
            this.avgRating = avgRating;
            this.course = course;
        }
    }




    public interface OnRequest { void run(Item it); }

    private final List<Item> data = new ArrayList<>();
    private final OnRequest onRequest;

    public FindSlotsAdapter(OnRequest onRequest) { this.onRequest = onRequest; }

    public void setData(List<Item> items) { data.clear(); data.addAll(items); notifyDataSetChanged(); }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View row = LayoutInflater.from(p.getContext()).inflate(R.layout.row_find_slot, p, false);
        return new VH(row);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Item it = data.get(pos);

        String ratingText;
        if (it.avgRating <= 0f) {
            ratingText = "no ratings yet";
        } else {
            ratingText = String.format(java.util.Locale.US, "%.1f★", it.avgRating);
        }

        String coursePart = (it.course == null || it.course.isEmpty())
                ? ""
                : " • " + it.course;

        h.line1.setText(
                it.tutorName + " (" + ratingText + ")" +
                        coursePart +
                        " • " + (it.isManual ? "Manual" : "Auto")
        );

        h.line2.setText(it.date + "   " + it.start + " - " + it.end);

        h.btnRequest.setOnClickListener(v -> onRequest.run(it));

    }



    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView line1, line2; Button btnRequest;
        VH(View v){ super(v);
            line1=v.findViewById(R.id.tvLine1); line2=v.findViewById(R.id.tvLine2); btnRequest=v.findViewById(R.id.btnRequest);
        }
    }


}
