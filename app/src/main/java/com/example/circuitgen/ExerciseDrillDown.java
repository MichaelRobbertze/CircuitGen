package com.example.circuitgen;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;

public class ExerciseDrillDown extends AppCompatActivity {
    DBHelper myDb;
    TextView txtTitle, txtDescription;
    ImageView imgGif;
    String exName, exDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_drill_down);

        myDb = new DBHelper(this);
        txtTitle = (TextView)findViewById(R.id.txtExTitle);
        txtDescription = (TextView)findViewById(R.id.txtDescription);
        imgGif = (GifImageView) findViewById(R.id.imgExExample);

        Intent intent = getIntent();
        String ExName = intent.getStringExtra("DetailExercise");

        CircuitHolder Circuit = myDb.getExercise(ExName);

        exName = Circuit.name;
        exDesc = Circuit.description;

        txtTitle.setText(exName);
        txtDescription.setText(exDesc);
        getAppropriateGif(exName, getApplicationContext());
    }

    public void getAppropriateGif(String Name, Context context)
    {
        String filename = Name.toLowerCase().replaceAll("\\s","").replaceAll("-", "");
//        filename = filename + ".gif";
        int id = context.getResources().getIdentifier(filename, "drawable", context.getPackageName());
        imgGif.setImageResource(id);
    }
}
