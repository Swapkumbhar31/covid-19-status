package com.androgeekapps.covid_19status.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androgeekapps.covid_19status.R;
import com.androgeekapps.covid_19status.adapter.StateDataAdapter;
import com.androgeekapps.covid_19status.model.StateData;
import com.androgeekapps.covid_19status.service.FirebaseDataService;

import org.jdeferred2.DoneCallback;

import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView txtActive = root.findViewById(R.id.txt_active);
        final TextView txtConfirmed = root.findViewById(R.id.txt_confirmed);
        final TextView txtDeceased = root.findViewById(R.id.txt_deceased);
        final TextView txtRecovered = root.findViewById(R.id.txt_recovered);
        final TextView txtConfirmedIncrease = root.findViewById(R.id.txt_confirmed_increase);
        final TextView txtRecoveredIncrease = root.findViewById(R.id.txt_recovered_increase);
        final TextView txtDeceasedIncrease = root.findViewById(R.id.txt_deceased_increase);
        recyclerView = root.findViewById(R.id.recyclerViewStateList);

        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FirebaseDataService.getStateData("total").done(new DoneCallback<StateData>() {
            @Override
            public void onDone(StateData result) {
                txtActive.setText(result.active);
                txtConfirmed.setText(result.confirmed);
                txtDeceased.setText(result.deceased);
                txtRecovered.setText(result.recovered);
                if (result.increase_confirmed != 0) {
                    txtConfirmedIncrease.setText("[+" + result.increase_confirmed + "]");
                }
                if (result.increase_deceased != 0) {
                    txtDeceasedIncrease.setText("[+" + result.increase_deceased + "]");
                }
                if (result.increase_recovered != 0) {
                    txtRecoveredIncrease.setText("[+" + result.increase_recovered + "]");
                }
            }
        });
        FirebaseDataService.fetchStateData(false).done(new DoneCallback<HashMap<String, StateData>>() {
            @Override
            public void onDone(HashMap<String, StateData> result) {
                mAdapter = new StateDataAdapter(result);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }
}