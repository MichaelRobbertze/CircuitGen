package com.example.circuitgen;

import android.app.AlertDialog;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ListOfSaved extends AppCompatActivity {
    DBHelper myDb;
    private RecyclerView reView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    String[] testString = {""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myDb = new DBHelper(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_saved);

        reView = (RecyclerView) findViewById(R.id.lstSaved);
        myLayoutManager = new LinearLayoutManager(this);
        reView.setLayoutManager(myLayoutManager);
        myAdapter = new SavedCircuitAdapter(SavedData());
        reView.setAdapter(myAdapter);
    }

    public void onNameClick(View v)
    {
        TextView txtView  = (TextView) v;
        String Name = txtView.getText().toString();
        Cursor CircRecord = myDb.CircuitData(Name);
        CircRecord.moveToFirst();
        String CircuitResult = CircRecord.getString(0);
        showMessage(Name, CircuitResult);
    }

    public String[] SavedData()
    {
        Cursor res = myDb.SavedCircuitNames();
        if(res.getCount() == 0)
        {
            showMessage("Lazy!", "No Circuits Saved Yet");
            return testString;
        }
        String[] resultString = new String[0];
        while(res.moveToNext())
        {
            resultString = myDb.addOn(resultString, res.getString(0));
        }
        return resultString;
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
