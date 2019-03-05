package com.example.circuitgen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class UnwantedViewAdapter extends RecyclerView.Adapter<UnwantedViewAdapter.unwantViewHolder>{
    private ArrayList<CircuitHolder> myDataSet;
    public DBHelper myDb;
    public String remName;
    public char[] remNameArr;

    public static class unwantViewHolder extends RecyclerView.ViewHolder{
        public TextView txtExerciseName;
        public Button btnReplace;
        public unwantViewHolder(View itemView)
        {
            super(itemView);
            txtExerciseName = itemView.findViewById(R.id.txtUnwantedName);
            btnReplace = itemView.findViewById(R.id.btnChangeUnwanted);
        }
    }

    public UnwantedViewAdapter(ArrayList<CircuitHolder> exList, Context context)
    {
        myDataSet = exList;
        myDb = new DBHelper(context);
    }

    @NonNull
    @Override
    public unwantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.unwanted_picker, viewGroup, false);
        unwantViewHolder mvh = new unwantViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull unwantViewHolder unwantedViewHolder, int i) {
        final CircuitHolder curr = myDataSet.get(i);
        final unwantViewHolder holder1 = unwantedViewHolder;
        holder1.btnReplace.setText("Replace");

        holder1.btnReplace.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Add entry into unwanted table
                        remName = holder1.txtExerciseName.getText().toString();
                        remNameArr = remName.toCharArray();
                        remName = DBHelper.getExName(remNameArr);
                        myDb.unRemove(remName);
                        holder1.btnReplace.setText("Reentered");
                        holder1.btnReplace.setEnabled(false);
                    }
                }
        );
        holder1.txtExerciseName.setText(curr.name);
    }

    @Override
    public int getItemCount() {
        return myDataSet.size();
    }

}

