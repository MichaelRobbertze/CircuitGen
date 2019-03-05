package com.example.circuitgen;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CircuitEditor extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spnExercise;
    RadioButton radReps1, radReps2, radRepsCust;
    EditText txtCustReps;
    TextView txtRepsOrTime, txtExId;
    Button btnNext;
    CircuitHolder myCircuit, CircuitHead;
    DBHelper myDb;
    String[] ExOptions, repOptions;
    int num;
    String circName, formattedOriginal;
    int i = 0;
    RadioGroup radgrp;
    String customRep;
    int length;
    String prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit_editor);


        //Layout Component Setup
        txtExId = (TextView) findViewById(R.id.txtExIdentifier);
        spnExercise = (Spinner) findViewById(R.id.spnExercise);
        radReps1 = (RadioButton) findViewById(R.id.radReps1);
        radReps2 = (RadioButton) findViewById(R.id.radReps2);
        radRepsCust = (RadioButton) findViewById(R.id.radCustomReps);
        txtCustReps = (EditText) findViewById(R.id.txtCustomReps);
        txtRepsOrTime = (TextView) findViewById(R.id.txtRepsOrTime);
        txtRepsOrTime.setVisibility(View.GONE);
        txtCustReps.setVisibility(View.GONE);
        btnNext = (Button) findViewById(R.id.btnNext);
        myDb = new DBHelper(getApplicationContext());
        Intent intent = getIntent();
        myCircuit = intent.getParcelableExtra("CircuitToEdit");
        myCircuit = myDb.getSavedCircuit(myCircuit.saveName);
        num = intent.getIntExtra("Number",0);
        length = intent.getIntExtra("Length", 0);
        radgrp = (RadioGroup) findViewById(R.id.radgrpReps);
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
        allowCustomReps();
        prog = (num+1) + "/" + length;
        txtExId.setText(prog);
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
                        else if(radReps2.isChecked())
                            myDb.editSave(formattedOriginal, myCircuit.saveName, theName, repOptions[1], num);
                        else{
                            customRep = txtCustReps.getText().toString();
                            if(customRep.equalsIgnoreCase(""))
                            {
                                Toast toast = Toast.makeText(CircuitEditor.this, "Reps shouldn't be empty", Toast.LENGTH_LONG);
                                View view = toast.getView();
                                TextView text = (TextView) view.findViewById(android.R.id.message);
                                text.setBackgroundResource(R.color.whitegrey);
                                text.setTextColor(Color.parseColor("#000000"));
                                toast.show();
                            }
                            else
                                myDb.editSave(formattedOriginal, myCircuit.saveName, theName, txtCustReps.getText().toString(), num);
                        }
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
                            intent.putExtra("Length", length);
                            startActivity(intent);
                        }
                    }
                }
        );
    }

    public void allowCustomReps()
    {
        radgrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(radRepsCust.isChecked())
                {
                    txtCustReps.setVisibility(View.VISIBLE);
                    txtRepsOrTime.setVisibility(View.VISIBLE);
                }
                else{
                    txtCustReps.setVisibility(View.GONE);
                    txtRepsOrTime.setVisibility(View.GONE);
                }
            }
        });
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

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), CircuitDisplay.class);
        CircuitHead = myDb.getSavedCircuit(myCircuit.saveName);
        intent.putExtra("CustomCircuit",CircuitHead);
        startActivity(intent);
    }
}
