package com.example.circuitgen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class ChooseUnwanted extends AppCompatActivity {
    ArrayList<CircuitHolder> AllExercises;
    DBHelper myDb;
    Cursor res;
    RecyclerView reView;
    LinearLayoutManager myLayoutManager;
    UnwantedExercisesAdapter unwantedAdapter;
    CircuitHolder circ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_unwanted);
        AllExercises = new ArrayList<>();

        myDb = new DBHelper(getApplicationContext());
        res = myDb.getAllExercises();
        if(res.getCount() == 0)
        {
            OptionsActivity.showMessage("Error", "No Data Found", getApplicationContext());
            return;
        }
        while(res.moveToNext())
        {
            circ = new CircuitHolder("("+res.getString(1)+")","");
            AllExercises.add(circ);
        }
        for(int i=0; i<AllExercises.size() - 1; i++)
        {
            AllExercises.get(i).next = AllExercises.get(i+1);
        }

        reView = (RecyclerView) findViewById(R.id.lstUnwantedSelectionList);
        reView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(this);
        unwantedAdapter = new UnwantedExercisesAdapter(AllExercises, getApplicationContext());
        reView.setLayoutManager(myLayoutManager);
        reView.setAdapter(unwantedAdapter);
    }
}
