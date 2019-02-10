package com.example.circuitgen;

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
        getAppropriateGif(exName);
    }

    public void getAppropriateGif(String Name)
    {
        switch(Name)
        {
            //Arms
            case"Dips":
                imgGif.setImageResource(R.drawable.dips);
                break;
            case"Reverse Dips":
                imgGif.setImageResource(R.drawable.reversedips);
                break;
            case"Swings to Handstand":
                imgGif.setImageResource(R.drawable.swingstohandstand);
                break;
            case"Dip Swings":
                imgGif.setImageResource(R.drawable.dipswings);
                break;
            case"Dip Swings to Planche":
                imgGif.setImageResource(R.drawable.dipswingstoplanche);
                break;
            case"Dip Swings to Handstand":
                imgGif.setImageResource(R.drawable.dipswingstohandstand);
                break;
            case"Chicken Wing Dips":
                imgGif.setImageResource(R.drawable.chickenwingdips);
                break;
            case"Overgrip Pull-ups":
                imgGif.setImageResource(R.drawable.overgrippullups);
                break;
            case"Undergrip Pull-ups":
                imgGif.setImageResource(R.drawable.undergrippullups);
                break;
            case"Wide arm Pull-ups":
                imgGif.setImageResource(R.drawable.widearmpullups);
                break;
            case"Diamond Push-ups":
                imgGif.setImageResource(R.drawable.diamondpushups);
                break;
            case"Push-ups":
                imgGif.setImageResource(R.drawable.pushups);
                break;
            case"Handstand Push-ups":
                imgGif.setImageResource(R.drawable.handstandpushups);
                break;
            case"Inverted Rows":
                imgGif.setImageResource(R.drawable.invertedrows);
                break;
            case"Rings Push-ups":
                imgGif.setImageResource(R.drawable.ringspushups);
                break;
            case"Reverse push-ups on Rings":
                imgGif.setImageResource(R.drawable.reversepushupsonrings);
                break;
            case"Archer Push-ups":
                imgGif.setImageResource(R.drawable.archerpushups);
                break;
            case"Archer Push-ups on Rings":
                imgGif.setImageResource(R.drawable.archerpushupsonrings);
                break;
            case"Dips on Rings":
                imgGif.setImageResource(R.drawable.dipsonrings);
                break;
            case"Rings Support Hold":
                imgGif.setImageResource(R.drawable.ringssupporthold);
                break;
            case"Clapping Push-ups":
                imgGif.setImageResource(R.drawable.clappingpushups);
                break;
            case"Japanese Handstand Push-ups":
                imgGif.setImageResource(R.drawable.japanesehandstandpushups);
                break;
            case"Bent-arm Straight Body Presses":
                imgGif.setImageResource(R.drawable.bentarmstraightbodypresses);
                break;
            case"Ice Cream Makers (Rings)":
                imgGif.setImageResource(R.drawable.icecreammakersrings);
                break;
            case"Ice Cream Makers (Bar)":
                imgGif.setImageResource(R.drawable.icecreammakersbar);
                break;
            case"Side-to-Side Pull-ups":
                imgGif.setImageResource(R.drawable.sidetosidepullups);
                break;
            case"Muscle-ups on Bar":
                imgGif.setImageResource(R.drawable.muscleupsonbar);
                break;
            case"Muscle-ups on Rings":
                imgGif.setImageResource(R.drawable.muscleupsonrings);
                break;
            case"Overgrip Pull-up Hold":
                imgGif.setImageResource(R.drawable.overgrippulluphold);
                break;
            case"Undergrip Pull-up Hold":
                imgGif.setImageResource(R.drawable.undergrippulluphold);
                break;
            case"Rope Climb":
                imgGif.setImageResource(R.drawable.ropeclimb);
                break;
            case"Chest Roll to Handstand":
                imgGif.setImageResource(R.drawable.chestrolltohandstand);
                break;
            case"Wrist Rollers":
                imgGif.setImageResource(R.drawable.wristrollers);
                break;
            case"Pull-ups on Rope with Vertical Grip":
                imgGif.setImageResource(R.drawable.pullupsonropewithverticalgrip);
                break;
            default:
                imgGif.setImageResource(R.drawable.thisisagif);
                break;
        }
    }
}
