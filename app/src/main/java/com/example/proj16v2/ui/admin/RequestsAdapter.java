package com.example.proj16v2.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj16v2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private List<AdminRequestsList.UserRow> data = new ArrayList<>();
    private final Consumer<Long> onApprove;
    private final Consumer<Long> onReject;

    public RequestsAdapter(Consumer<Long> onApprove, Consumer<Long> onReject) {
        this.onApprove = onApprove;
        this.onReject = onReject;
    }

    public void submitList(List<AdminRequestsList.UserRow> newList) {
        data = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminRequestsList.UserRow u = data.get(position);
        holder.name.setText(u.first + " " + u.last);
        holder.email.setText(u.email);
        holder.role.setText(u.role);
        holder.status.setText(u.status);

        holder.approve.setOnClickListener(v -> onApprove.accept(u.id));
        holder.reject.setOnClickListener(v -> onReject.accept(u.id));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, role, status;
        Button approve, reject;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.txtName);
            email = v.findViewById(R.id.txtEmail);
            role = v.findViewById(R.id.chipRole);
            status = v.findViewById(R.id.txtStatus);
            approve = v.findViewById(R.id.btnApprove);
            reject = v.findViewById(R.id.btnReject);
        }
    }
}

