package com.androgeekapps.covid_19status.adapter;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androgeekapps.covid_19status.R;
import com.androgeekapps.covid_19status.StateDetails;
import com.androgeekapps.covid_19status.model.DistrictData;
import com.androgeekapps.covid_19status.model.StateData;
import com.androgeekapps.covid_19status.service.FirebaseDataService;

import org.jdeferred2.DoneCallback;

import java.util.HashMap;
import java.util.List;

public class DistrictDataAdapter extends RecyclerView.Adapter<DistrictDataAdapter.MyViewHolder>{
    HashMap<String, DistrictData> mDataset;

    @NonNull
    @Override
    public DistrictDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.district_data_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DistrictDataAdapter.MyViewHolder holder, int position) {
        final MyViewHolder rowViewHolder = holder;
        holder.setIsRecyclable(false);
        final int rowPos = rowViewHolder.getAdapterPosition();

        if (rowPos == 0) {
            rowViewHolder.name.setText("DISTRICT");
            rowViewHolder.confirmed.setTypeface(Typeface.DEFAULT_BOLD);
            rowViewHolder.confirmed.setText(R.string.confirmed);
            rowViewHolder.imgIncrease.setVisibility(View.GONE);
        } else {
            String index = mDataset.keySet().toArray()[rowPos - 1].toString();
            final DistrictData modal = mDataset.get(index);
            rowViewHolder.name.setText(modal.name);
            rowViewHolder.confirmed.setText(modal.confirmed.toString());
            if (modal.increase == 0) {
                rowViewHolder.imgIncrease.setVisibility(View.GONE);
            } else {
                rowViewHolder.increase.setText(modal.increase.toString());
            }
        }
    }

    public DistrictDataAdapter(HashMap<String, DistrictData> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public int getItemCount() {
        return mDataset.size() + 1;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView confirmed;
        public TextView increase;
        public ImageView imgIncrease;
        public MyViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.txt_state_name);
            confirmed = v.findViewById(R.id.txt_confirmed);
            increase = v.findViewById(R.id.txt_increase);
            imgIncrease = v.findViewById(R.id.img_increase);
        }
    }
}
