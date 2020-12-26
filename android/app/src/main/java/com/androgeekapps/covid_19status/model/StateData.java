package com.androgeekapps.covid_19status.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class StateData {
    public String active;
    public String confirmed;
    public String deceased;
    public String recovered;
    public Long increase_confirmed;
    public Long increase_recovered;
    public Long increase_deceased;
    public Long increase_active;
    public String name;
    public HashMap<String, DistrictData> data;
}
