package com.example.circuitgen;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CircuitDisplayAdapter extends RecyclerView.Adapter<CircuitDisplayAdapter.MyViewHolder> {
    private ArrayList<CircuitHolder> myDataSet;
    public DBHelper myDb;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtText;
        public Button btnRegenerate;

        public MyViewHolder(View itemView){
            super(itemView);
            txtText = itemView.findViewById(R.id.txtExerciseName);
            btnRegenerate = itemView.findViewById(R.id.btnRegenerate);
        }
    }

    public CircuitDisplayAdapter(ArrayList<CircuitHolder> circList, Context context)
    {
        myDataSet = circList;
        myDb = new DBHelper(context);
    }

    @Override
    public CircuitDisplayAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.textandbutton, parent, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final CircuitHolder curr = myDataSet.get(position);
        final int pos = position;

        int num = position + 1;
        String text;
        final MyViewHolder holder1 = holder;
        if(curr.isSaved == 1 || curr.hideTheButtons == 1)
        {
            text = curr.name;
            holder.btnRegenerate.setVisibility(View.GONE);
        }
        else{
            text = num + ": " + curr.repCount + " (" + curr.name + ")";
        }
        holder.btnRegenerate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentExercise = holder1.txtText.getText().toString();
                        char[] charArr = currentExercise.toCharArray();
                        if(charArr[4] == '(')
                            currentExercise = myDb.replaceExercise(currentExercise,true);
                        else
                            currentExercise = myDb.replaceExercise(currentExercise, false);
                        holder1.txtText.setText(currentExercise);
                        myDataSet.get(pos).name = currentExercise;
                        myDataSet.get(pos).isEdited = 1;
                    }
                }
        );
        holder.txtText.setText(text);
    }
    @Override
    public int getItemCount() {
        return myDataSet.size();
    }

}
