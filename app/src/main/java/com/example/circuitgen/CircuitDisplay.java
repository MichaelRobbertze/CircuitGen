package com.example.circuitgen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;

public class CircuitDisplay extends AppCompatActivity {
    Button btnSave;
    DBHelper myDb;
    CircuitHolder Circuit;
    String saveName;
    private RecyclerView reView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit_display);
        btnSave = findViewById(R.id.btnSave);
        Intent intent = getIntent();
        Circuit = intent.getParcelableExtra("CustomCircuit");
//        boolean isSaved = intent.getBooleanExtra("SavedCircuit",false);
        myDb = new DBHelper(this);
        if(Circuit.isSaved == 1)
            btnSave.setVisibility(View.GONE);


        reView = (RecyclerView) findViewById(R.id.lstCircuitResult);
        myLayoutManager = new LinearLayoutManager(this);
        reView.setLayoutManager(myLayoutManager);
        myAdapter = new CircuitDisplayAdapter(CircuitStringDisplay(Circuit));
        reView.setAdapter(myAdapter);
        SaveCircuit();
    }

    public void SaveCircuit()
    {
        btnSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        SharedPreferences.Editor prefsEdit = mPrefs.edit();
//                        Gson gson = new Gson();
//                        String json = gson.toJson(Circuit);
//                        prefsEdit.putString("MyCircuit",json);
//                        prefsEdit.commit();

//                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm");
                        LocalDateTime now = LocalDateTime.now();
                        String DefaultName = dtf.format(now);

                        AlertDialog.Builder builder = new AlertDialog.Builder(CircuitDisplay.this);
                        builder.setTitle("Save-Name");

//                        input setup
                        final EditText txtSaveName = new EditText(CircuitDisplay.this);
                        //type of input expected
                        txtSaveName.setInputType(InputType.TYPE_CLASS_TEXT);
                        txtSaveName.setText(DefaultName);
                        builder.setView(txtSaveName);

                        //Button Setup
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                create a method in dbhelper class that takes a circuitHolder as input and saves it to the db
                                saveName = txtSaveName.getText().toString();
                                myDb.saveCircuit(Circuit,"Yes",1,saveName);
//                                myDb.saveCircuit(Circuit,"Yes",1,txtSaveName.getText().toString());
                                Toast.makeText(CircuitDisplay.this, "Circuit Saved", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                }
        );
    }

    public String[] CircuitStringDisplay(CircuitHolder myCirc)
    {
        DBHelper myDb = new DBHelper(this);
        int num = 1;
        String formattedExercise;
        String[] myCircArr = new String[0];
        if(myCirc == null)
            return myCircArr;

        do {
            if(myCirc.repCount == null)
                formattedExercise = myCirc.name;
            else
                formattedExercise = num + ": " + myCirc.repCount + " (" + myCirc.name + ")";
            myCircArr = DBHelper.addOn(myCircArr, formattedExercise);
            myCirc = myCirc.next;
            num++;
        }while(myCirc != null);

        return myCircArr;
    }

    public void DetailExercise(View v)
    {
        TextView txtEx = (TextView) v;
        Stack<Character> bracketStack = new Stack<>();
        char[] charArr = txtEx.getText().toString().toCharArray();
        String ExName = "";
        for (Character currChar: charArr) {
            if(currChar == '(' && bracketStack.isEmpty())
            {
                bracketStack.push(currChar);
            }
            else if(currChar == '(' && !bracketStack.isEmpty())
            {
                ExName += currChar;
                bracketStack.push(currChar);
            }
            else if(currChar == ')')
            {
                bracketStack.pop();
                if(!bracketStack.isEmpty())
                    ExName += currChar;
            }
            else
            {
                if(!bracketStack.isEmpty())
                {
                    ExName += currChar;
                }
            }
        }
//        CircuitHolder myExercise = myDb.getExercise(ExName);
        Intent intent = new Intent(this, ExerciseDrillDown.class);
        intent.putExtra("DetailExercise",ExName);
        startActivity(intent);
    }
}
