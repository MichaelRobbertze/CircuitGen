package com.example.circuitgen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CircuitDisplay extends AppCompatActivity {
    TextView txtCircuit;
    Button btnSave;
    String Save_Name = "";
    String Circuit = "";
    DBHelper myDB = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit_display);
        txtCircuit = findViewById(R.id.txtCircDisplay);
        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        Circuit = intent.getStringExtra("CustomCircuit");
        txtCircuit.setText(Circuit);
        txtCircuit.setMovementMethod(new ScrollingMovementMethod());
        SaveCircuit();
    }

    public void SaveCircuit()
    {
        btnSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String DefaultName = dtf.format(now);

                        AlertDialog.Builder builder = new AlertDialog.Builder(CircuitDisplay.this);
                        builder.setTitle("Name");

                        //input setup
                        final EditText txtSaveName = new EditText(CircuitDisplay.this);
                        //type of input expected
                        txtSaveName.setInputType(InputType.TYPE_CLASS_TEXT);
                        txtSaveName.setText(DefaultName);
                        builder.setView(txtSaveName);

                        //Button Setup
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDB.saveCircuit(txtSaveName.getText().toString(), Circuit);
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
}
