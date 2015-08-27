package com.sushmanayak.android.todoapp;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sushmanayak.android.todoapp.Db.TodoList;
import com.sushmanayak.android.todoapp.model.TodoItem;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDetailsFragment extends DialogFragment {

    private TodoItem mItem;
    private int mIndex;
    EditText todoTitle;
    EditText todoDetails;
    RadioGroup priorityGroup;
    TextView todoDueDate;
    TextView todoDueTime;
    RadioButton lowPriority;
    RadioButton mediumPriority;
    RadioButton highPriority;
    Button cancelButton;
    Button doneButton;

    private static final String TASK_INDEX = "SimpleTodo.TaskIndex";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int NEW_TASK_INDEX = -1;

    enum Action {
        ADDNEW,
        EDIT
    }

    Action action = Action.ADDNEW;

    public static ItemDetailsFragment newInstance(int index) {
        ItemDetailsFragment taskDetails = new ItemDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(TASK_INDEX, index);
        taskDetails.setArguments(args);
        return taskDetails;
    }

    public interface ItemDetailsListener {
        void onAddItem(TodoItem item);
        void onUpdateItem(TodoItem item);
    }

    public ItemDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_item_details, container, false);
        todoTitle = (EditText) v.findViewById(R.id.todoTitle);
        todoDetails = (EditText) v.findViewById(R.id.todoDetails);
        todoDueDate = (TextView) v.findViewById(R.id.todoDueDate);
        todoDueTime = (TextView) v.findViewById(R.id.todoDueTime);
        priorityGroup = (RadioGroup) v.findViewById(R.id.priorityGroup);
        lowPriority = (RadioButton) v.findViewById(R.id.lowPriority);
        mediumPriority = (RadioButton) v.findViewById(R.id.mediumPriority);
        highPriority = (RadioButton) v.findViewById(R.id.highPriority);
        cancelButton = (Button) v.findViewById(R.id.cancelButton);
        doneButton = (Button) v.findViewById(R.id.doneButton);

        Dialog editDetails = getDialog();
        editDetails.setTitle(getResources().getString(R.string.enterTaskDetails));

        todoDueDate.setPaintFlags(todoDueDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        todoDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFragment dialog = DateFragment.newInstance(mItem.getDate());
                dialog.setTargetFragment(ItemDetailsFragment.this, REQUEST_DATE);
                dialog.show(getFragmentManager(), "DialogDate");
            }
        });

        todoDueTime.setPaintFlags(todoDueTime.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        todoDueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimeFragment dialog = TimeFragment.newInstance(mItem.getDate());
                dialog.setTargetFragment(ItemDetailsFragment.this, REQUEST_TIME);
                dialog.show(getFragmentManager(),"DialogTime");
            }
        });

        mIndex = getArguments().getInt(TASK_INDEX, NEW_TASK_INDEX);
        if (mIndex == NEW_TASK_INDEX)
            mItem = new TodoItem();
        else {
            mItem = TodoList.get(getActivity()).getTodoItems().get(mIndex);
            action = Action.EDIT;
            updateUI();
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        doneButton.setOnClickListener(doneButtonListener);
        return v;
    }

    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Verify that the title or notes have been added
            if(todoTitle.getText().length() > 0 || todoDetails.getText().length() > 0) {
                mItem.setTitle(todoTitle.getText().toString());
                mItem.setDescription(todoDetails.getText().toString());
                mItem.setPriority(getPriority());

                ItemDetailsListener listener = (ItemDetailsListener) getActivity();
                if (action == Action.ADDNEW) {
                    listener.onAddItem(mItem);
                } else {
                    listener.onUpdateItem(mItem);
                }
            }
            dismiss();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
            return;

        if(requestCode == REQUEST_DATE)
        {
            Date taskDate = (Date)data.getSerializableExtra(DateFragment.EXTRA_DATE);
            mItem.setDate(taskDate);
            todoDueDate.setText(String.format("%1$tY %1$tb %1$td", taskDate));
        }
        if(requestCode == REQUEST_TIME)
        {
            Date taskDate = (Date)data.getSerializableExtra(TimeFragment.EXTRA_TIME);
            mItem.setDate(taskDate);
            todoDueTime.setText(android.text.format.DateFormat.format("kk:mm:ss", taskDate));
        }
    }

    private void updateUI() {

        todoTitle.setText(mItem.getTitle());
        todoDetails.setText(mItem.getDescription());
        if(mItem.getDate() != null) {
            todoDueDate.setText(String.format("%1$tY %1$tb %1$td", mItem.getDate()));
            todoDueTime.setText(android.text.format.DateFormat.format("kk:mm:ss", mItem.getDate()));
        }

        if (mItem.getPriority() == 2)
            highPriority.setChecked(true);
        else if (mItem.getPriority() == 1)
            mediumPriority.setChecked(true);
        else
            lowPriority.setChecked(true);
    }

    private int getPriority()
    {
        int radioButtonID = priorityGroup.getCheckedRadioButtonId();
        View radioButton = priorityGroup.findViewById(radioButtonID);
        return priorityGroup.indexOfChild(radioButton);
    }
}
