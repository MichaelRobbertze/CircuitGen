package com.example.circuitgen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Stack;

public class CircuitDisplay extends AppCompatActivity {
    Button btnSave, btnEdit;
    DBHelper myDb;
    CircuitHolder Circuit, circuitCurrent;
    String saveName;
    private RecyclerView reView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    private ArrayList<CircuitHolder> myCircList;
    int length = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit_display);
        btnSave = findViewById(R.id.btnSave);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        Intent intent = getIntent();
        Circuit = intent.getParcelableExtra("CustomCircuit");
//        boolean isSaved = intent.getBooleanExtra("SavedCircuit",false);
        myDb = new DBHelper(this);
        if (Circuit.isSaved != 1) {
            btnEdit.setVisibility(View.GONE);
        }
        if (Circuit.isSaved == 1)
            DeleteCircuit();
        else
            SaveCircuit();
        if(Circuit.hideTheButtons == 1)
        {
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
        }
        circuitCurrent = Circuit;
        while(circuitCurrent != null)
        {
            length++;
            circuitCurrent = circuitCurrent.next;
        }


        reView = (RecyclerView) findViewById(R.id.lstCircuitResult);
        myLayoutManager = new LinearLayoutManager(this);
        reView.setLayoutManager(myLayoutManager);

        createList();
        buildRecyclerView();
        editCircuit();
    }

    @Override
    public void onBackPressed() {
        if(Circuit.isSaved == 1)
        {
            Intent intent = new Intent(getApplicationContext(), ListOfSaved.class);
            startActivity(intent);
        }
        else
            super.onBackPressed();
    }

    public void createList(){
        myCircList = new ArrayList<>();
        circuitCurrent = Circuit;
        while (circuitCurrent != null) {
            myCircList.add(circuitCurrent);
            circuitCurrent = circuitCurrent.next;
        }
    }
    public void buildRecyclerView(){
        reView = findViewById(R.id.lstCircuitResult);
        reView.setHasFixedSize(true);
        myLayoutManager = new LinearLayoutManager(this);
        myAdapter = new CircuitDisplayAdapter(myCircList, getApplicationContext());
        reView.setLayoutManager(myLayoutManager);
        reView.setAdapter(myAdapter);
    }


//    @Override
//    public void onRestart()
//    {
//        super.onRestart();
//        Intent intent = new Intent(getApplicationContext(),OptionsActivity.class);
//        startActivity(intent);
//    }
//
//        @Override
//        public void onBackPressed ()
//        {
//            super.onBackPressed();
//            Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
//            startActivity(intent);
//        }

        public void DeleteCircuit ()
        {
            btnSave.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CircuitDisplay.this);
                            builder.setTitle("Are you sure?");
                            builder.setPositiveButton("Yea", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    myDb.DeleteSavedCircuit(Circuit.saveName);
                                    Intent intent = new Intent(getApplicationContext(), ListOfSaved.class);
//                                Toast.makeText(CircuitDisplay.this, "Circuit Deleted", Toast.LENGTH_LONG).show();

                                    Toast toast = Toast.makeText(CircuitDisplay.this, "Circuit Deleted", Toast.LENGTH_LONG);
                                    View view = toast.getView();
                                    TextView text = (TextView) view.findViewById(android.R.id.message);
                                    text.setBackgroundResource(R.color.whitegrey);
                                    text.setTextColor(Color.parseColor("#000000"));
                                    toast.show();

                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    }
            );
            btnSave.setText("Delete Circuit");
        }

        public void SaveCircuit ()
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
                                    myDb.saveCircuit(Circuit, "Yes", 1, saveName);
//                                myDb.saveCircuit(Circuit,"Yes",1,txtSaveName.getText().toString());
                                    Toast toast = Toast.makeText(CircuitDisplay.this, "Circuit Saved", Toast.LENGTH_LONG);
                                    View view = toast.getView();
                                    TextView text = (TextView) view.findViewById(android.R.id.message);
                                    text.setBackgroundResource(R.color.whitegrey);
                                    text.setTextColor(Color.parseColor("#000000"));
                                    toast.show();
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
            btnSave.setText("Save Circuit");
        }

        public void editCircuit ()
        {
            btnEdit.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), CircuitEditor.class);
                            intent.putExtra("CircuitToEdit", Circuit);
                            intent.putExtra("Number", 0);
                            intent.putExtra("Length", length);
                            startActivity(intent);
                        }
                    }
            );
        }

        public String[] CircuitStringDisplay (CircuitHolder myCirc)
        {
            DBHelper myDb = new DBHelper(this);
            int num = 1;
            String formattedExercise;
            String[] myCircArr = new String[0];
            if (myCirc == null)
                return myCircArr;

            do {
                if (myCirc.repCount == null)
                    formattedExercise = myCirc.name;
                else
                    formattedExercise = num + ": " + myCirc.repCount + " (" + myCirc.name + ")";
                myCircArr = DBHelper.addOn(myCircArr, formattedExercise);
                myCirc = myCirc.next;
                num++;
            } while (myCirc != null);

            return myCircArr;
        }

        public void DetailExercise (View v)
        {
            TextView txtEx = (TextView) v;
            char[] charArr = txtEx.getText().toString().toCharArray();
            String ExName = myDb.getExName(charArr);
//        CircuitHolder myExercise = myDb.getExercise(ExName);
            Intent intent = new Intent(this, ExerciseDrillDown.class);
            intent.putExtra("DetailExercise", ExName);
            startActivity(intent);
        }


    }

