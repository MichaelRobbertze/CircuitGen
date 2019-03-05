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

public class UnwantedExercisesAdapter extends RecyclerView.Adapter<UnwantedExercisesAdapter.unwantedViewHolder>{
    private ArrayList<CircuitHolder> myDataSet;
    public DBHelper myDb;
    String remName;
    char[] remNameArr;

    public static class unwantedViewHolder extends RecyclerView.ViewHolder{
        public TextView txtExerciseName;
        public Button btnRemove;
        public unwantedViewHolder(View itemView)
        {
            super(itemView);
            txtExerciseName = itemView.findViewById(R.id.txtUnwantedName);
            btnRemove = itemView.findViewById(R.id.btnChangeUnwanted);
        }
    }

    public UnwantedExercisesAdapter(ArrayList<CircuitHolder> exList, Context context)
    {
        myDataSet = exList;
        myDb = new DBHelper(context);
    }

    @NonNull
    @Override
    public unwantedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.unwanted_picker, viewGroup, false);
        unwantedViewHolder mvh = new unwantedViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull unwantedViewHolder unwantedViewHolder, int i) {
        final CircuitHolder curr = myDataSet.get(i);
        final unwantedViewHolder holder1 = unwantedViewHolder;

        holder1.btnRemove.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Add entry into unwanted table
                        remName = holder1.txtExerciseName.getText().toString();
                        remNameArr = remName.toCharArray();
                        remName = DBHelper.getExName(remNameArr);
                        myDb.setRemoved(remName);
                        holder1.btnRemove.setText("Removed");
                        holder1.btnRemove.setEnabled(false);
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
