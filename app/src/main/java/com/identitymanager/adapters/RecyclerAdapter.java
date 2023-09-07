package com.identitymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.identitymanager.R;
import com.identitymanager.models.data.Account;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {

    static RecyclerViewClickListener listener;
    Context context;
    ArrayList<Account> list;

    public RecyclerAdapter(Context context, ArrayList<Account> list, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);

        return new RecyclerHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {

        Account account = list.get(position);

        holder.accountName.setText(account.getAccountName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView accountName;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            accountName = itemView.findViewById(R.id.account_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick (View v, int position);
    }

}
