package com.example.circuitgen;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleOfTheDay extends AppCompatActivity {
    String Day;
    Button btnAccept;
    TextView txtTheTitle;
    CheckBox chkArms, chkBAS, chkCore, chkLegs, chkHIIT, chkRest;
    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_of_the_day);

        btnAccept = (Button) findViewById(R.id.btnAcceptToday);

        txtTheTitle = (TextView) findViewById(R.id.txtDailyTitle);

        Intent intent = getIntent();
        Day = intent.getStringExtra("DayToEdit");
        String Title = "Exercises for " + Day;
        txtTheTitle.setText(Title);
        btnAccept.setText("Accept Settings for " + Day);

        myDb = new DBHelper(ScheduleOfTheDay.this);

        chkArms = (CheckBox) findViewById(R.id.chkArmsDay);
        chkBAS = (CheckBox) findViewById(R.id.chkBackAndShouldersDay);
        chkCore = (CheckBox) findViewById(R.id.chkCoreDay);
        chkLegs = (CheckBox) findViewById(R.id.chkLegsDay);
        chkHIIT = (CheckBox) findViewById(R.id.chkHIITDay);
        chkRest = (CheckBox) findViewById(R.id.chkRestDay);

        AcceptDaysSettings();
        controlChecks();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        Intent intent = new Intent(getApplicationContext(), EditSchedule.class);
        startActivity(intent);
    }

    public void controlChecks()
    {
        chkArms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkArms.isChecked()) {
                    chkHIIT.setChecked(false);
                    chkRest.setChecked(false);
                }
                if(!chkArms.isChecked() && !chkBAS.isChecked() && !chkCore.isChecked() && !chkLegs.isChecked() && !chkHIIT.isChecked())
                    chkRest.setChecked(true);
            }
        });

        chkBAS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkBAS.isChecked()) {
                    chkHIIT.setChecked(false);
                    chkRest.setChecked(false);
                }
                if(!chkArms.isChecked() && !chkBAS.isChecked() && !chkCore.isChecked() && !chkLegs.isChecked() && !chkHIIT.isChecked())
                    chkRest.setChecked(true);
            }
        });
        chkCore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkCore.isChecked()) {
                    chkHIIT.setChecked(false);
                    chkRest.setChecked(false);
                }
                if(!chkArms.isChecked() && !chkBAS.isChecked() && !chkCore.isChecked() && !chkLegs.isChecked() && !chkHIIT.isChecked())
                    chkRest.setChecked(true);
            }
        });
        chkLegs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkLegs.isChecked()) {
                    chkHIIT.setChecked(false);
                    chkRest.setChecked(false);
                }
                if(!chkArms.isChecked() && !chkBAS.isChecked() && !chkCore.isChecked() && !chkLegs.isChecked() && !chkHIIT.isChecked())
                    chkRest.setChecked(true);
            }
        });
        chkHIIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkHIIT.isChecked()) {
                    chkArms.setChecked(false);
                    chkBAS.setChecked(false);
                    chkCore.setChecked(false);
                    chkLegs.setChecked(false);
                    chkRest.setChecked(false);
                }
            }
        });
        chkRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chkRest.isChecked()) {
                    chkArms.setChecked(false);
                    chkBAS.setChecked(false);
                    chkCore.setChecked(false);
                    chkLegs.setChecked(false);
                    chkHIIT.setChecked(false);
                }
            }
        });

    }

    public void AcceptDaysSettings()
    {
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleOfTheDay.this, ScheduleOfTheDay.class);
                boolean isArms = chkArms.isChecked();
                boolean isBAS = chkBAS.isChecked();
                boolean isCore = chkCore.isChecked();
                boolean isLegs = chkLegs.isChecked();
                boolean isHIIT = chkHIIT.isChecked();
                boolean isRest = chkRest.isChecked();

                if(!isArms && !isBAS && !isCore && !isLegs && !isHIIT && !isRest)
                {
                    Toast toast = Toast.makeText(ScheduleOfTheDay.this, "Selection is empty! Select \"Rest\" if you want to do nothing on a "+ Day, Toast.LENGTH_LONG);
                    View view = toast.getView();
                    TextView text = (TextView) view.findViewById(android.R.id.message);
                    text.setBackgroundResource(R.color.whitegrey);
                    text.setTextColor(Color.parseColor("#000000"));
                    toast.show();
                }
                else
                {
                    myDb.setDaysSchedule(Day,isArms,isBAS,isCore,isLegs,isHIIT);
                    Day = advanceDay(Day);
                    if(Day.equalsIgnoreCase("Monday"))
                    {
                        Intent Endintent = new Intent(getApplicationContext(),OptionsActivity.class);
                        startActivity(Endintent);
                    }
                    else{
                        intent.putExtra("DayToEdit",Day);
                        startActivity(intent);
                    }

                }
            }
        });
    }

    public String advanceDay(String Day)
    {
        String[] TheWeek = new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        int nextDay = 21;
        int arrPoint = 0;
        while(nextDay == 21)
        {
            if(!Day.equalsIgnoreCase(TheWeek[arrPoint]))
            {
                arrPoint = (arrPoint+1)%7;
            }
            else
            {
                nextDay = (arrPoint+1)%7;
            }
        }
        return TheWeek[nextDay];
    }
}
