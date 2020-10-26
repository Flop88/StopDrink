package ru.mvlikhachev.stopdrink.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.mvlikhachev.stopdrink.model.User;
import ru.mvlikhachev.stopdrink.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder>{

    private ArrayList<User> resultArrayList;

    public CardAdapter(ArrayList<User> resultArrayList) {
        this.resultArrayList = resultArrayList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_days, parent, false);

        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.cardDaysWithoutAlcoholeTextView.setText(resultArrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return resultArrayList.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        TextView cardDaysWithoutAlcoholeTextView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDaysWithoutAlcoholeTextView = itemView.findViewById(R.id.cardDaysWithoutAlcoholeTextView);


        }
    }
}
