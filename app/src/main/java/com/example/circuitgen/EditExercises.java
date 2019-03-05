package com.example.circuitgen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EditExercises extends AppCompatActivity {

    Button btnsetUnwanted, btnUnsetUnwanted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercises);

        btnsetUnwanted = (Button) findViewById(R.id.btnSetUnwanted);
        btnUnsetUnwanted = (Button) findViewById(R.id.btnUnRemove);
        chooseUnwanted();
        viewUnwanted();
    }

    public void chooseUnwanted()
    {
        btnsetUnwanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChooseUnwanted.class);
                startActivity(intent);
            }
        });
    }

    public void viewUnwanted()
    {
        btnUnsetUnwanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UnwantedList.class);
                startActivity(intent);
            }
        });
    }
}
