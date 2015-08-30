package com.sushmanayak.android.todoapp;


import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sushmanayak.android.todoapp.Db.TodoList;
import com.sushmanayak.android.todoapp.model.TodoItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
    ImageView micEnterTitle;
    ImageView micEnterNotes;
    private boolean mNotifyOptionChanged = false;

    private static final String TASK_INDEX = "SimpleTodo.TaskIndex";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQ_CODE_SPEECH_TITLE = 2;
    private static final int REQ_CODE_SPEECH_NOTES = 3;
    private static final int NEW_TASK_INDEX = -1;

    enum Action {
        ADDNEW,
        EDIT
    }

    Action action = Action.ADDNEW;

    public ItemDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance with the item index as the argument
     */
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

        void onUpdateNotification(TodoItem item);
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
        micEnterTitle = (ImageView) v.findViewById(R.id.micEnterTitle);
        micEnterNotes = (ImageView) v.findViewById(R.id.micEnterNotes);

        mIndex = getArguments().getInt(TASK_INDEX, NEW_TASK_INDEX);
        if (mIndex == NEW_TASK_INDEX)
            mItem = new TodoItem();
        else {
            mItem = TodoList.get(getActivity()).getTodoItems().get(mIndex);
            action = Action.EDIT;
            InitUI();
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
                mNotifyOptionChanged = true;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        doneButton.setOnClickListener(doneButtonListener);

        micEnterTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(REQ_CODE_SPEECH_TITLE);
            }
        });
        micEnterNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(REQ_CODE_SPEECH_NOTES);
            }
        });
        return v;
    }

    /**
     * Set the width and height of the dialog
     */
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        super.onResume();
    }

    /**
     * Event handler for the 'done' button
     */
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
                // Update the alarm if the user sets/resets reminder
                if(mNotifyOptionChanged)
                    listener.onUpdateNotification(mItem);
            }
            dismiss();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQ_CODE_SPEECH_TITLE && resultCode == Activity.RESULT_OK && null != data) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            todoTitle.setText(result.get(0));
            return;

        } else if (requestCode == REQ_CODE_SPEECH_NOTES && resultCode == Activity.RESULT_OK && null != data) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            todoDetails.setText(result.get(0));
            return;

        } else if (requestCode == REQUEST_DATE) {
            Date taskDate = (Date) data.getSerializableExtra(DateFragment.EXTRA_DATE);
            mItem.setDate(taskDate);
            todoDueDate.setText(DateFormat.getDateInstance().format(taskDate));

        } else if (requestCode == REQUEST_TIME) {
            Date taskDate = (Date) data.getSerializableExtra(TimeFragment.EXTRA_TIME);
            mItem.setDate(taskDate);
            todoDueTime.setText(DateFormat.getTimeInstance().format(taskDate));

        }
        if (mItem.getDate() != null) {
            setNotification.setVisibility(mItem.getDate() != null ? View.VISIBLE : View.GONE);
            // Set the alarm if the date/time changes and if setReminder is checked
            if (mItem.getNotify())
                mNotifyOptionChanged = true;
        }
    }

    /**
     * Initialize the Edit task dialog
     */
    private void InitUI() {

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

    /**
     * Get the index for the radio button of the priority selected
     */
    private int getPriority() {
        int radioButtonID = priorityGroup.getCheckedRadioButtonId();
        View radioButton = priorityGroup.findViewById(radioButtonID);
        return priorityGroup.indexOfChild(radioButton);
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput(int requestCode) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
