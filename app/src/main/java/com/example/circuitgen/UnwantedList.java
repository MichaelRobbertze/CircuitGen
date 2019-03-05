package com.example.circuitgen;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UnwantedList extends AppCompatActivity {
    ArrayList<CircuitHolder> AllExercises;
    DBHelper myDb;
    Cursor res;
    RecyclerView reView;
    LinearLayoutManager myLayoutManager;
    UnwantedViewAdapter unwantedAdapter;
    CircuitHolder circ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unwanted_list);

        AllExercises = new ArrayList<>();

        myDb = new DBHelper(getApplicationContext());
        res = myDb.getAllUnwantedExercises();
        if(res.getCount() == 0)
        {
            Toast toast = Toast.makeText(UnwantedList.this, "Empty List, no Exercises Removed", Toast.LENGTH_LONG);
            View view = toast.getView();
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setBackgroundResource(R.color.whitegrey);
            text.setTextColor(Color.parseColor("#000000"));
            toast.show();
        }
        else
        {
            while(res.moveToNext())
            {
                circ = new CircuitHolder("("+res.getString(1)+")","");
                AllExercises.add(circ);
            }
            for(int i=0; i<AllExercises.size() - 1; i++)
            {
                AllExercises.get(i).next = AllExercises.get(i+1);
            }
        }


        reView = (RecyclerView) findViewById(R.id.lstFixUnwanted);
        reView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(this);
        unwantedAdapter = new UnwantedViewAdapter(AllExercises, getApplicationContext());
        reView.setLayoutManager(myLayoutManager);
        reView.setAdapter(unwantedAdapter);

    }
}
