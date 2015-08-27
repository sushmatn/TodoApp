package com.sushmanayak.android.todoapp;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sushmanayak.android.todoapp.Db.TodoList;
import com.sushmanayak.android.todoapp.model.TodoItem;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    CheckBox setNotification;
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
        setNotification = (CheckBox) v.findViewById(R.id.setNotification);
        priorityGroup = (RadioGroup) v.findViewById(R.id.priorityGroup);
        lowPriority = (RadioButton) v.findViewById(R.id.lowPriority);
        mediumPriority = (RadioButton) v.findViewById(R.id.mediumPriority);
        highPriority = (RadioButton) v.findViewById(R.id.highPriority);
        cancelButton = (Button) v.findViewById(R.id.cancelButton);
        doneButton = (Button) v.findViewById(R.id.doneButton);

        mIndex = getArguments().getInt(TASK_INDEX, NEW_TASK_INDEX);
        if (mIndex == NEW_TASK_INDEX)
            mItem = new TodoItem();
        else {
            mItem = TodoList.get(getActivity()).getTodoItems().get(mIndex);
            action = Action.EDIT;
            updateUI();
        }

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
                dialog.show(getFragmentManager(), "DialogTime");
            }
        });

        setNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mItem.setNotify(isChecked);
                setAlarm(isChecked);
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        doneButton.setOnClickListener(doneButtonListener);
        return v;
    }

    private void setAlarm(boolean isChecked) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mItem.getDate());
        Intent alertIntent = new Intent(getActivity(), AlertReceiver.class);
        alertIntent.putExtra(Intent.EXTRA_TEXT, mItem.getTitle() + " " + mItem.getDescription());
        PendingIntent sender = PendingIntent.getBroadcast(getActivity(), (int) mItem.getId().getLeastSignificantBits(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        if (isChecked) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        } else
            alarmManager.cancel(sender);
    }

    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // Verify that the title or notes have been added
            if (todoTitle.getText().length() > 0 || todoDetails.getText().length() > 0) {
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
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_DATE) {
            Date taskDate = (Date) data.getSerializableExtra(DateFragment.EXTRA_DATE);
            mItem.setDate(taskDate);
            todoDueDate.setText(DateFormat.getDateInstance().format(taskDate));
        }
        if (requestCode == REQUEST_TIME) {
            Date taskDate = (Date) data.getSerializableExtra(TimeFragment.EXTRA_TIME);
            mItem.setDate(taskDate);
            todoDueTime.setText(DateFormat.getTimeInstance().format(taskDate));
        }
        if (mItem.getDate() != null) {
            setNotification.setVisibility(mItem.getDate() != null ? View.VISIBLE : View.GONE);
            // Set the alarm if the date/time changes and if setReminder is checked
            if (mItem.getNotify())
                setAlarm(true);
        }
    }

    private void updateUI() {

        todoTitle.setText(mItem.getTitle());
        todoDetails.setText(mItem.getDescription());
        if (mItem.getDate() != null) {
            todoDueDate.setText(DateFormat.getDateInstance().format(mItem.getDate()));
            todoDueTime.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(mItem.getDate()));
            setNotification.setVisibility(View.VISIBLE);
        }
        setNotification.setChecked(mItem.getNotify());
        if (mItem.getPriority() == 2)
            highPriority.setChecked(true);
        else if (mItem.getPriority() == 1)
            mediumPriority.setChecked(true);
        else
            lowPriority.setChecked(true);
    }

    private int getPriority() {
        int radioButtonID = priorityGroup.getCheckedRadioButtonId();
        View radioButton = priorityGroup.findViewById(radioButtonID);
        return priorityGroup.indexOfChild(radioButton);
    }
}
