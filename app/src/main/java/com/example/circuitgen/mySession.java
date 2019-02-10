package com.example.circuitgen;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;

public class mySession {

    private SharedPreferences prefs;

    public mySession(Context cntx)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setSessionCircuit(CircuitHolder myCircuit)
    {
        ArrayList<CircuitHolder> myEntireCircuit = new ArrayList<CircuitHolder>();
        while(myCircuit!=null)
        {
            myEntireCircuit.add(myCircuit);
            myCircuit = myCircuit.next;
        }
        Gson gson = new Gson();
        String CircuitString = gson.toJson(myEntireCircuit);

    }
}
