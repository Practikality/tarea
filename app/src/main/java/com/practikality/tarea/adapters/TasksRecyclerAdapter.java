package com.practikality.tarea.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.practikality.tarea.R;
import com.practikality.tarea.models.Task;

import java.util.ArrayList;

public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.ViewHolder>  {
    private ArrayList<Task> mTasks = new ArrayList<>();
    private OnTaskListener mOnTaskListener;

    public TasksRecyclerAdapter(ArrayList<Task> tasks, OnTaskListener onTaskListener) {
        this.mTasks = tasks;
        this.mOnTaskListener = onTaskListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listitem_task,viewGroup,false);
        return new ViewHolder(view, mOnTaskListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText(mTasks.get(i).getTitle());
        viewHolder.assignedTo.setText(mTasks.get(i).getTaskTo());
        int color = Color.rgb(238,238,238);
        String priority = mTasks.get(i).getPriority();
        if(priority.equals("casual")){
            color = Color.rgb(3,169,244);
        }else if(priority.equals("important")){
            color = Color.rgb(255,183,77);
        }else if(priority.equals("urgent")){
            color = Color.rgb(255,138,101);
        }else if(priority.equals("rejected")){
            color = Color.rgb(217,206,197);
        }
        viewHolder.materialCardView.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title, assignedTo; //the views which are present in each item
        MaterialCardView materialCardView;
        OnTaskListener onTaskListener;
        public ViewHolder(@NonNull View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            title = itemView.findViewById(R.id.list_item_task_title);
            assignedTo = itemView.findViewById(R.id.list_item_task_assigned_to);
            materialCardView = itemView.findViewById(R.id.list_item_card_view);
            this.onTaskListener = onTaskListener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onTaskListener.onTaskClick(getAdapterPosition());
        }
    }

    public interface OnTaskListener{
        void onTaskClick(int position);
    }
}
