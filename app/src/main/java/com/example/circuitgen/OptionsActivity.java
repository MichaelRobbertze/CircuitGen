package com.example.circuitgen;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionsActivity extends AppCompatActivity {

    Button btnGenerate, btnView, btnSaved, btnNotes;
    DBHelper myDb;

    //Move btnGenerate listener into its own method, tidy it up.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        myDb = new DBHelper(this);

        btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnView = (Button) findViewById(R.id.btnViewEx);
        btnSaved = (Button) findViewById(R.id.btnSaved);
        btnNotes = (Button) findViewById(R.id.btnNotes);
        btnGenerate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCircGen();
                    }
                }
        );

        viewAll();
        savedCircuits();
        showNotes();
    }

    public void showNotes()
    {
        btnNotes.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String theNote;
                        //Add Circuit Generating Info
                        theNote = "Circuit Generating\n\n";
                        theNote += "\tRegular Days:\n";
                        theNote += "\t\t Recommended Length: 10 - 12\n";
                        theNote += "\t\t Do each exercise listen only once\n\n";
                        theNote += "\t HIIT Days:\n";
                        theNote += "\t\t Recommended Length: 8\n";
                        theNote += "\t\t Recommended Time: 40 on, 20 between\n";
                        theNote += "\t\t Perform circuit list at least twice, with a break in-between\n\n\n";

                        //Add Workout Structure
                        theNote += "Weekly Workout Structure\n\n";
                        theNote += "\tMonday: Arms, Back & Shoulders\n";
                        theNote += "\tTuesday: Legs\n";
                        theNote += "\tWednesday: Core\n";
                        theNote += "\tThursday: Arms, Back & Shoulders\n";
                        theNote += "\tFriday: HIIT\n";
                        theNote += "\tSaturday: Legs & Core\n\n\n";

                        //Note Section about the musclegroups
                        theNote += "Available MuscleGroup Selections\n\n";
                        theNote += "\tArms: Exercises that involve bending your arms, pushing and pulling.\n\n";
                        theNote += "\tBack & Shoulders: Upper Body Exercises that involve keeping your arms straight, mostly planche related stuff\n\n";
                        theNote += "\tCore: Exercises which place focus on your abs and core. Body tightening stuff.\n\n";
                        theNote += "\tLegs: Exercises for the legs. Mixture between regular and plyometric leg exercises\n\n";
                        theNote += "\tOther: Exercises used to generate HIIT circuits, involves full range of muscle groups. These exercises do not have set rep amounts as they are performed for a certain time limit";

                        showMessage("Info", theNote);
                    }
                }
        );
    }

    public void savedCircuits()
    {
        btnSaved.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OptionsActivity.this, ListOfSaved.class);
                        startActivity(intent);
                    }
                }
        );
    }

    public void showCircGen()
    {
        Intent intent = new Intent(this, Generate.class);
        startActivity(intent);
    }

    public void viewAll()
    {
        btnView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllExercises();
                        if(res.getCount() == 0)
                        {
                            showMessage("Error", "No Data Found");
                            return;
                        }
                        StringBuffer buff = new StringBuffer();
                        while(res.moveToNext())
                        {
                            buff.append("ID: " + res.getString(0) + "\n");
                            buff.append("Name: " + res.getString(1) + "\n");
                            buff.append("Opt 1: " + res.getString(2) + "\n");
                            buff.append("Opt 2: " + res.getString(3) + "\n");
                            buff.append("Category: " + res.getString(4) + "\n");
                            buff.append("Is Easy: " + res.getString(5) + "\n\n");
                        }
                        showMessage("Exercises",buff.toString());
                    }
                }
        );
    }

    public void showMessage(String Title, String Message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(Title);
        builder.setMessage(Message);
        builder.show();
    }


}
