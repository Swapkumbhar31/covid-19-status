package com.androgeekapps.covid_19status.adapter;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.androgeekapps.covid_19status.R;
import com.androgeekapps.covid_19status.StateDetails;
import com.androgeekapps.covid_19status.model.StateData;
import com.androgeekapps.covid_19status.service.FirebaseDataService;

import org.jdeferred2.DoneCallback;

import java.util.HashMap;
import java.util.List;

public class StateDataAdapter extends RecyclerView.Adapter<StateDataAdapter.MyViewHolder>{
    HashMap<String, StateData> mDataset;

    @NonNull
    @Override
    public StateDataAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.state_data_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StateDataAdapter.MyViewHolder holder, int position) {
        final MyViewHolder rowViewHolder = holder;
        holder.setIsRecyclable(false);
        final int rowPos = rowViewHolder.getAdapterPosition();

        if (rowPos == 0) {
            rowViewHolder.stateName.setText("STATE/UT");
            rowViewHolder.active.setText(R.string.active);
            rowViewHolder.deceased.setTypeface(Typeface.DEFAULT_BOLD);
            rowViewHolder.recovered.setTypeface(Typeface.DEFAULT_BOLD);
            rowViewHolder.active.setTypeface(Typeface.DEFAULT_BOLD);
            rowViewHolder.confirmed.setTypeface(Typeface.DEFAULT_BOLD);
            rowViewHolder.active.setAllCaps(true);
            rowViewHolder.confirmed.setAllCaps(true);
            rowViewHolder.recovered.setAllCaps(true);
            rowViewHolder.deceased.setAllCaps(true);
            rowViewHolder.confirmed.setText(R.string.confirmed);
            rowViewHolder.recovered.setText(R.string.recovered);
            rowViewHolder.deceased.setText(R.string.deceased);
            rowViewHolder.imgIncrease.setVisibility(View.GONE);
        } else {
            FirebaseDataService.getStates().done(new DoneCallback<List<String>>() {
                @Override
                public void onDone(final List<String> result) {
                    final StateData modal = mDataset.get(result.get(rowPos - 1));
                    rowViewHolder.stateName.setText(modal.name);
                    rowViewHolder.active.setText(modal.active);
                    rowViewHolder.confirmed.setText(modal.confirmed);
                    rowViewHolder.recovered.setText(modal.recovered);
                    rowViewHolder.deceased.setText(modal.deceased);
                    if (modal.increase_confirmed == 0) {
                        rowViewHolder.imgIncrease.setVisibility(View.GONE);
                    } else {
                        rowViewHolder.increase.setText(modal.increase_confirmed.toString());
                    }
                    if (modal.name.equalsIgnoreCase("total")) {
                        rowViewHolder.deceased.setTypeface(Typeface.DEFAULT_BOLD);
                        rowViewHolder.recovered.setTypeface(Typeface.DEFAULT_BOLD);
                        rowViewHolder.active.setTypeface(Typeface.DEFAULT_BOLD);
                        rowViewHolder.confirmed.setTypeface(Typeface.DEFAULT_BOLD);
                        rowViewHolder.increase.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        rowViewHolder.stateName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(view.getContext(), StateDetails.class);
                                intent.putExtra("state_name", result.get(rowPos - 1));
                                view.getContext().startActivity(intent);
                            }
                        });
                    }
                }
            });
        }
    }

    public StateDataAdapter(HashMap<String, StateData> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public int getItemCount() {
        return mDataset.size() + 1;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView stateName;
        public TextView active;
        public TextView confirmed;
        public TextView recovered;
        public TextView deceased;
        public TextView increase;
        public ImageView imgIncrease;
        public MyViewHolder(View v) {
            super(v);
            stateName = v.findViewById(R.id.txt_state_name);
            active = v.findViewById(R.id.txt_active);
            confirmed = v.findViewById(R.id.txt_confirmed);
            recovered = v.findViewById(R.id.txt_recovered);
            deceased = v.findViewById(R.id.txt_deceased);
            increase = v.findViewById(R.id.txt_increase);
            imgIncrease = v.findViewById(R.id.img_increase);
        }
    }
}
