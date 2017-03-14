package com.example.pethoalpar.zxingexample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ViewNameListAdapter extends RecyclerView.Adapter<ViewNameListAdapter.MyViewHolder> {

    private List<ViewNameListModel> model;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView number, studentName, studentMatric;
        public MyViewHolder(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.number);
            studentName = (TextView) itemView.findViewById(R.id.studentname);
            studentMatric = (TextView) itemView.findViewById(R.id.matricno);
        }
    }

    public ViewNameListAdapter(Context context, List<ViewNameListModel> model){
        this.context = context;
        this.model = model;
    }

    @Override
    public ViewNameListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewNameListAdapter.MyViewHolder holder, int position) {
        ViewNameListModel models = model.get(position);
        holder.number.setText( position+1 + ". ");
        holder.studentName.setText(models.getStudent_name());
        holder.studentMatric.setText(models.getStudent_matric());
    }

    @Override
    public int getItemCount() {
        return model.size();
    }
}
