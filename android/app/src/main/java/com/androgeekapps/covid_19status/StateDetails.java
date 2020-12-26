package com.androgeekapps.covid_19status;

import android.os.Bundle;

import com.androgeekapps.covid_19status.adapter.DistrictDataAdapter;
import com.androgeekapps.covid_19status.model.StateData;
import com.androgeekapps.covid_19status.service.FirebaseDataService;
import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import org.jdeferred2.DoneCallback;

public class StateDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_details);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        String stateName = getIntent().getStringExtra("state_name");
        final TextView txtActive = findViewById(R.id.txt_active);
        final TextView txtDeceased = findViewById(R.id.txt_deceased);
        final TextView txtRecovered = findViewById(R.id.txt_recovered);
        final TextView txtConfirmed = findViewById(R.id.txt_confirmed);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, stateName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        recyclerView = findViewById(R.id.recyclerViewStateList);

        recyclerView.setHasFixedSize(false);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FirebaseDataService.getStateData(stateName).done(new DoneCallback<StateData>() {
            @Override
            public void onDone(StateData result) {
                toolbar.setTitle(result.name);
                txtDeceased.setText(result.deceased);
                txtRecovered.setText(result.recovered);
                txtActive.setText(result.active);
                txtConfirmed.setText(result.confirmed);
                mAdapter = new DistrictDataAdapter(result.data);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
