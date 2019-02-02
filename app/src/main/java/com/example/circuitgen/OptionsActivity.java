package com.example.circuitgen;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionsActivity extends AppCompatActivity {

    Button btnGenerate, btnView, btnSaved;
    DBHelper myDb;

    //Move btnGenerate listener into its own method, tidy it up.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        myDb = new DBHelper(this);

        btnGenerate = (Button) findViewById(R.id.btnGenerate);
        btnView = (Button) findViewById(R.id.btnViewEx);
        btnSaved = (Button) findViewById(R.id.btnSaved);
        btnGenerate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCircGen();
                    }
                }
        );

        viewAll();
        savedCircuits();
    }

    public void savedCircuits()
    {
        btnSaved.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OptionsActivity.this, ListOfSaved.class);
                        startActivity(intent);
                    }
                }
        );
    }

    public void showCircGen()
    {
        Intent intent = new Intent(this, Generate.class);
        startActivity(intent);
    }

    public void viewAll()
    {
        btnView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllExercises();
                        if(res.getCount() == 0)
                        {
                            showMessage("Error", "No Data Found");
                            return;
                        }
                        StringBuffer buff = new StringBuffer();
                        while(res.moveToNext())
                        {
                            buff.append("ID: " + res.getString(0) + "\n");
                            buff.append("Name: " + res.getString(1) + "\n");
                            buff.append("Opt 1: " + res.getString(2) + "\n");
                            buff.append("Opt 2: " + res.getString(3) + "\n");
                            buff.append("Category: " + res.getString(4) + "\n\n");
                        }
                        showMessage("Exercises",buff.toString());
                    }
                }
        );
    }

    public void showMessage(String Title, String Message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(Title);
        builder.setMessage(Message);
        builder.show();
    }


}
