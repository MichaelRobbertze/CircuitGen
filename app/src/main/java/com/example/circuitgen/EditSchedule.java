package com.example.circuitgen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EditSchedule extends AppCompatActivity {
    Button btnEditSchedule;
    TextView txtSchedule;
    String Day, entireSchedule;
    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);
        myDb = new DBHelper(getApplicationContext());

        btnEditSchedule = (Button) findViewById(R.id.btnEditSchedule);
        txtSchedule = (TextView) findViewById(R.id.txtWeeklySchedule);
        beginScheduleReset();
        Day = "Monday";
        entireSchedule = myDb.getWeeksSchedule();
        txtSchedule.setText(entireSchedule);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
        startActivity(intent);
    }

    public void beginScheduleReset()
    {
        btnEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ScheduleOfTheDay.class);
                intent.putExtra("DayToEdit",Day);
                startActivity(intent);
            }
        });
    }
}
