package com.example.circuitgen;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class Generate extends AppCompatActivity {
    DBHelper myDb;
    EditText txtLength;
    CheckBox chkArms, chkBackAndShoulders, chkCore, chkLegs, chkOther, chkEasy, chkHard;
    Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        myDb = new DBHelper(this);

        txtLength = (EditText) findViewById(R.id.txtLength);
        chkArms = (CheckBox) findViewById(R.id.chkArms);
        chkBackAndShoulders = (CheckBox) findViewById(R.id.chkBackAndShoulders);
        chkCore = (CheckBox) findViewById(R.id.chkCore);
        chkLegs = (CheckBox) findViewById(R.id.chkLegs);
        chkOther = (CheckBox) findViewById(R.id.chkOther);
        chkEasy = (CheckBox) findViewById(R.id.chkMakeEasy);
        chkHard = (CheckBox) findViewById(R.id.chkMakeHard);
        btnGo = (Button) findViewById(R.id.btnGo);

        btnGo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), CircuitDisplay.class);
//                        intent.putExtra("SavedCircuit", false);
                        intent.putExtra("CustomCircuit", GenerateCirc());
                        startActivity(intent);
                    }
                }
        );
    }

    public CircuitHolder GenerateCirc()
    {
        int length = Integer.parseInt(txtLength.getText().toString());
        boolean isArms = false;
        boolean isBackAndShoulders = false;
        boolean isCore = false;
        boolean isLegs = false;
        boolean isOther = false;
        boolean isEasy = false;
        boolean isHard = false;

        if(chkArms.isChecked())
            isArms = true;
        if(chkBackAndShoulders.isChecked())
            isBackAndShoulders = true;
        if(chkCore.isChecked())
            isCore = true;
        if(chkLegs.isChecked())
            isLegs = true;
        if(chkOther.isChecked())
            isOther = true;
        if(chkEasy.isChecked())
            isEasy = true;
        if(chkHard.isChecked())
            isHard = true;

        return myDb.newCircuit(isArms, isBackAndShoulders, isCore, isLegs, isOther, length, isEasy, isHard);
    }
}
