package com.example.circuitgen;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class SavedCircuitAdapter extends RecyclerView.Adapter<SavedCircuitAdapter.MyViewHolder> {
    private String[] myDataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtText;
        public MyViewHolder(TextView v){
            super(v);
            txtText = v;
        }
    }

    public SavedCircuitAdapter(String[] MyData)
    {
        myDataSet = MyData;
    }

    @Override
    public SavedCircuitAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        TextView v =(TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.txtText.setText(myDataSet[position]);
    }
    @Override
    public int getItemCount() {
        return myDataSet.length;
    }

}
