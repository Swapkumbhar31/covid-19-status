package com.androgeekapps.covid_19status.service;

import android.content.Context;
import android.util.Log;


import com.androgeekapps.covid_19status.model.StateData;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jdeferred2.Deferred;
import org.jdeferred2.DoneCallback;
import org.jdeferred2.Promise;
import org.jdeferred2.impl.DeferredObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseDataService {
    private static FirebaseDatabase database;
    private static final String TAG = "FirebaseDataService";
    private static List<String> states = new ArrayList<>();
    private static HashMap<String, StateData> stateData = new HashMap<>();

    public static void initialise(Context context) {
        if (database == null) {
            FirebaseApp.initializeApp(context);
            database = FirebaseDatabase.getInstance();
        }
    }

    public static Promise fetchStates() {
        final Deferred deferred = new DeferredObject();
        DatabaseReference rf = FirebaseDataService.database.getReference("state");
        rf.keepSynced(true);
        rf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDataService.states = new ArrayList<>();
                for (DataSnapshot state: dataSnapshot.getChildren()) {
                    String value = state.getValue(String.class);
                    states.add(value);
                }
                deferred.resolve(FirebaseDataService.states);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
        return deferred.promise();
    }

    public static Promise fetchStateData(boolean force) {
        final Deferred deferred = new DeferredObject();
        DatabaseReference rf = FirebaseDataService.database.getReference("data");
        rf.keepSynced(true);
        rf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDataService.stateData = new HashMap<>();
                for (DataSnapshot state: dataSnapshot.getChildren()) {
                    StateData value = state.getValue(StateData.class);
                    stateData.put(state.getKey(), value);
                }
                deferred.resolve(FirebaseDataService.stateData);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
        return deferred.promise();
    }

    public static Promise getStates() {
        Deferred deferred = new DeferredObject();
        if (FirebaseDataService.states.size() == 0) {
            return FirebaseDataService.fetchStates();
        } else {
            deferred.resolve(FirebaseDataService.states);
        }
        return deferred.promise();
    }

    public static Promise getStateData(final String stateName) {
        final Deferred deferred = new DeferredObject();
        FirebaseDataService.getStates().done(new DoneCallback<List<String>>() {
            @Override
            public void onDone(List<String> result) {
                FirebaseDataService.fetchStateData(false).done(new DoneCallback<HashMap<String, StateData>>() {
                    @Override
                    public void onDone(HashMap<String, StateData> result) {
                        deferred.resolve(result.get(stateName));
                    }
                });
            }
        });

        return deferred.promise();
    }

}
