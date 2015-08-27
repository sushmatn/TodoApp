package com.sushmanayak.android.todoapp.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.sushmanayak.android.todoapp.R;
import com.sushmanayak.android.todoapp.model.TodoItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by SushmaNayak on 8/25/2015.
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoHolder> {

    ArrayList<TodoItem> mTodoItems;
    Context mContext;

    public interface TodoItemListeners {
        void onItemClick(int position);

        void onLongItemClick(int position);

        void onItemChecked(TodoItem item);
    }

    public TodoAdapter(ArrayList<TodoItem> items, Context context) {
        mTodoItems = items;
        mContext = context;
    }

    public void setToDoItems(ArrayList<TodoItem> items) {
        mTodoItems = items;
    }

    public TodoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item, viewGroup, false);
        return new TodoHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoHolder holder, int i) {
        holder.bindItem(mTodoItems.get(i));

    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    class TodoHolder extends RecyclerView.ViewHolder
            implements CompoundButton.OnCheckedChangeListener, View.OnLongClickListener, View.OnClickListener {

        TodoItem mItem;
        View viewPriority;
        TextView todoTitle;
        TextView todoDetails;
        TextView todoDueDate;
        CheckBox todoCompleted;
        View todoItemGroup;
        TodoItemListeners listener;

        public TodoHolder(View itemView) {
            super(itemView);

            InitViews(itemView);
            todoCompleted.setOnCheckedChangeListener(this);
            todoItemGroup.setOnClickListener(this);
            todoItemGroup.setOnLongClickListener(this);
        }

        private void InitViews(View itemView) {
            todoTitle = (TextView) itemView.findViewById(R.id.todoTitle);
            todoDetails = (TextView) itemView.findViewById(R.id.todoDetails);
            todoDueDate = (TextView) itemView.findViewById(R.id.todoDueDate);
            todoCompleted = (CheckBox) itemView.findViewById(R.id.todoCompleted);
            viewPriority = (View) itemView.findViewById(R.id.todoPriority);
            todoItemGroup = (View) itemView.findViewById(R.id.todoItemGroup);
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            CheckBox item = (CheckBox) buttonView;

            int paintFlags = item.getPaintFlags();
            if (isChecked)
                paintFlags = paintFlags | Paint.STRIKE_THRU_TEXT_FLAG;
            else
                paintFlags = paintFlags & ~Paint.STRIKE_THRU_TEXT_FLAG;

            todoTitle.setPaintFlags(paintFlags);
            todoDetails.setPaintFlags(paintFlags);
            todoDueDate.setPaintFlags(paintFlags);

            mItem.setCompleted(isChecked);
            listener = (TodoItemListeners) mContext;
            listener.onItemChecked(mItem);
        }

        public void onClick(View v) {
            listener = (TodoItemListeners) mContext;
            listener.onItemClick(getAdapterPosition());
        }

        public boolean onLongClick(View v) {
            listener = (TodoItemListeners) mContext;
            listener.onLongItemClick(getAdapterPosition());
            return true;
        }

        public void bindItem(TodoItem item) {
            mItem = item;
            todoTitle.setText(mItem.getTitle());
            todoTitle.setVisibility(mItem.getTitle().length() > 0 ? View.VISIBLE : View.GONE);

            todoDetails.setText(mItem.getDescription());
            todoDetails.setVisibility(mItem.getDescription().length() > 0 ? View.VISIBLE : View.GONE);

            if (mItem.getDate() != null) {
                todoDueDate.setText(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(mItem.getDate()));
                todoDueDate.setVisibility(View.VISIBLE);
            } else
                todoDueDate.setVisibility(View.GONE);

            todoCompleted.setChecked(mItem.isCompleted());

            int priority = mItem.getPriority();
            if (priority == 2)
                viewPriority.setBackgroundColor(mContext.getResources().getColor(R.color.highPriority));
            else if (priority == 1)
                viewPriority.setBackgroundColor(mContext.getResources().getColor(R.color.mediumPriority));
            else
                viewPriority.setBackgroundColor(mContext.getResources().getColor(R.color.lowPriority));
        }
    }
}
