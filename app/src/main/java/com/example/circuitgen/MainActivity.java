package com.example.circuitgen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnBegin;
    DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBegin = (Button) findViewById(R.id.btnBegin);
        btnBegin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showOptions();
            }
        });

        myDb = new DBHelper(this);
    }

    public void showOptions()
    {
        myDb.populate();
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
}
