package com.example.circuitgen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class CircuitDisplay extends AppCompatActivity {
    TextView txtCircuit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circuit_display);
        txtCircuit = findViewById(R.id.txtCircDisplay);

        Intent intent = getIntent();
        String Circuit = intent.getStringExtra("CustomCircuit");
        txtCircuit.setText(Circuit);
        txtCircuit.setMovementMethod(new ScrollingMovementMethod());
    }
}
