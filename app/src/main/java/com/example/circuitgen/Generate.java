package com.example.circuitgen;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Generate extends AppCompatActivity {
    DBHelper myDb;
    EditText txtLength;
    CheckBox chkArms, chkBackAndShoulders, chkCore, chkLegs, chkOther;
    Button btnGo;
    RadioGroup radgrpEquip;
    RadioButton radEquipSkill, radEquipUnskill, radHome;
    Date now;
    String Day;
    boolean[] daysSchedule;
    private View.OnClickListener checkListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(chkCore.isChecked()||chkLegs.isChecked()||chkArms.isChecked()||chkBackAndShoulders.isChecked())
                chkOther.setChecked(false);
        }
    };

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
        btnGo = (Button) findViewById(R.id.btnGo);
        radEquipSkill = (RadioButton) findViewById(R.id.radEquipSkill);
        radEquipUnskill = (RadioButton) findViewById(R.id.radEquipUnskill);
        radHome = (RadioButton) findViewById(R.id.radHome);
        radgrpEquip = (RadioGroup) findViewById(R.id.radgrpEquip);

        now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        Day = simpleDateFormat.format(now);
        daysSchedule = myDb.getDaysSchedule(Day);
        if(daysSchedule[0])
            chkArms.setChecked(true);
        if(daysSchedule[1])
            chkBackAndShoulders.setChecked(true);
        if(daysSchedule[2])
            chkCore.setChecked(true);
        if(daysSchedule[3])
            chkLegs.setChecked(true);
        if(daysSchedule[4])
            chkOther.setChecked(true);

        btnGo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String length = txtLength.getText().toString();
                        if(length.equals("") || (!chkArms.isChecked() && !chkBackAndShoulders.isChecked()&&!chkCore.isChecked()&&!chkLegs.isChecked() && !chkOther.isChecked()))
                        {
                            Toast toast = Toast.makeText(Generate.this, "Ensure all necessary fields are selected and a circuit length is specified", Toast.LENGTH_LONG);
                            View view = toast.getView();
                            TextView text = view.findViewById(android.R.id.message);
                            text.setBackgroundResource(R.color.whitegrey);
                            text.setTextColor(Color.parseColor("#000000"));
                            toast.show();
                        }
                        else {
                            Intent intent = new Intent(getApplicationContext(), CircuitDisplay.class);
//                        intent.putExtra("SavedCircuit", false);
                            intent.putExtra("CustomCircuit", GenerateCirc());
                            startActivity(intent);
                        }
                    }
                }
        );
        controlChecks();
    }

    public void controlChecks()
    {
        chkOther.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(chkOther.isChecked())
                        {
                            chkLegs.setChecked(false);
                            chkArms.setChecked(false);
                            chkBackAndShoulders.setChecked(false);
                            chkCore.setChecked(false);
                        }
                    }
                }
        );
        chkArms.setOnClickListener(checkListen);
        chkBackAndShoulders.setOnClickListener(checkListen);
        chkCore.setOnClickListener(checkListen);
        chkLegs.setOnClickListener(checkListen);
    }



    public CircuitHolder GenerateCirc()
    {
        int length = Integer.parseInt(txtLength.getText().toString());
        boolean isArms = false;
        boolean isBackAndShoulders = false;
        boolean isCore = false;
        boolean isLegs = false;
        boolean isOther = false;
        boolean isNoob = false;
        boolean isEquipped = true;

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
        if(radEquipUnskill.isChecked())
            isNoob = true;
        else if(radHome.isChecked())
            isEquipped = false;


        if(!isArms&&!isBackAndShoulders&&!isCore&&!isLegs&&!isOther)
            isArms = isBackAndShoulders = isCore = isLegs = true;
        return myDb.newCircuit(isArms, isBackAndShoulders, isCore, isLegs, isOther, length, isNoob, isEquipped);
    }
}
