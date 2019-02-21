package com.example.circuitgen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

public class CircuitEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spnExercise;
    RadioButton radReps1, radReps2;
    Button btnNext;
    CircuitHolder myCircuit, CircuitHead;
    DBHelper myDb;
    String[] ExOptions, repOptions;
    int num;
    String circName, formattedOriginal;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit_editor);

        //Layout Component Setup
        spnExercise = (Spinner) findViewById(R.id.spnExercise);
        radReps1 = (RadioButton) findViewById(R.id.radReps1);
        radReps2 = (RadioButton) findViewById(R.id.radReps2);
        btnNext = (Button) findViewById(R.id.btnNext);
        myDb = new DBHelper(getApplicationContext());
        Intent intent = getIntent();
        myCircuit = intent.getParcelableExtra("CircuitToEdit");
        myCircuit = myDb.getSavedCircuit(myCircuit.saveName);
        num = intent.getIntExtra("Number",0);
        while(i < num)
        {
            myCircuit = myCircuit.next;
            i++;
        }
        formattedOriginal = myCircuit.name;
        char[] charArr = myCircuit.name.toCharArray();
        circName = myDb.getExName(charArr);

        //Spinner Setup
        ExOptions = myDb.getExerciseOptionsList(circName);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CircuitEditor.this, android.R.layout.simple_spinner_item, ExOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnExercise.setAdapter(adapter);
        spnExercise.setOnItemSelectedListener(this);

        //Radio Button Setup
        repOptions = myDb.getRepOptions(circName);
        radReps1.setText(repOptions[0]);
        radReps2.setText(repOptions[1]);
        radReps1.setChecked(true);
        Next();
    }

    public void Next()
    {
        btnNext.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        String theName = spnExercise.getSelectedItem().toString();
                        myCircuit.name = theName;
                        if(radReps1.isChecked())
                            myDb.editSave(formattedOriginal, myCircuit.saveName, theName, repOptions[0], num);
                        else
                            myDb.editSave(formattedOriginal, myCircuit.saveName, theName, repOptions[1], num);
                        if(myCircuit.next == null)
                        {
                            Intent intent = new Intent(getApplicationContext(), CircuitDisplay.class);
                            CircuitHead = myDb.getSavedCircuit(myCircuit.saveName);
                            intent.putExtra("CustomCircuit",CircuitHead);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(getApplicationContext(), CircuitEditor.class);
                            intent.putExtra("CircuitToEdit", myCircuit.next);
                            intent.putExtra("Number", num + 1);
                            startActivity(intent);
                        }
                    }
                }
        );
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String newName = parent.getItemAtPosition(position).toString();
        repOptions = myDb.getRepOptions(newName);
        radReps1.setText(repOptions[0]);
        radReps2.setText(repOptions[1]);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
