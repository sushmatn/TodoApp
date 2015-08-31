package com.sushmanayak.android.todoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sushmanayak.android.todoapp.Db.TodoList;
import com.sushmanayak.android.todoapp.adapter.TodoAdapter;
import com.sushmanayak.android.todoapp.model.TodoItem;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements ItemDetailsFragment.ItemDetailsListener, TodoAdapter.TodoItemListeners {

    Toolbar mToolbar;
    TodoAdapter mTodoAdapter;
    RecyclerView mTodoRecyclerView;
    FloatingActionButton addItemButton;
    TodoList mTodoList;

    private static final int NEW_TASK_INDEX = -1;
    private static final String EDIT_TASK_DIALOG = "Edit task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTodoRecyclerView = (RecyclerView) findViewById(R.id.todoListView);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTodoRecyclerView.setHasFixedSize(true);

        mTouchHelper.attachToRecyclerView(mTodoRecyclerView);

        mTodoList = TodoList.get(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        addItemButton = (FloatingActionButton) findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditItem(NEW_TASK_INDEX);
            }
        });
        updateUI();
    }

    /**
     * Display the Edit task dialog
     */
    public void EditItem(int index) {
        ItemDetailsFragment taskDetails = ItemDetailsFragment.newInstance(index);
        taskDetails.show(getSupportFragmentManager(), EDIT_TASK_DIALOG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Locate MenuItem with ShareActionProvider
        /*MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setShareIntent();*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clearCompleted) {
            deleteAllCompletedTasks();
            return true;
        } else if (id == R.id.action_share) {
            setShareIntent();
        }
        return super.onOptionsItemSelected(item);
    }

    ItemTouchHelper mTouchHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                public boolean onMove(RecyclerView recyclerView,
                                      RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return true;
                }

                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // remove from adapter
                    mTodoList.removeItem(mTodoList.getTodoItems().get(viewHolder.getAdapterPosition()));
                    mTodoAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    refreshTaskList();
                    Toast snack = Toast.makeText(getApplication(),getResources().getString(R.string.toastTaskDeleted), Toast.LENGTH_SHORT);
                    snack.show();
                }
            });

    /**
     * Delete all checked tasks
     */
    private void deleteAllCompletedTasks() {
        ArrayList<TodoItem> todoItems = TodoList.get(this).getTodoItems();
        for (TodoItem item : todoItems) {
            if (item.isCompleted())
                TodoList.get(this).removeItem(item);
        }
        updateUI();
    }

    /**
     * Set the adapter
     */
    private void updateUI() {
        ArrayList<TodoItem> todoItems = mTodoList.getTodoItems();
        if (mTodoAdapter == null) {
            mTodoAdapter = new TodoAdapter(todoItems, this);
            mTodoRecyclerView.setAdapter(mTodoAdapter);
        } else {
            refreshTaskList();
            mTodoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Start an activity with ACTION_SEND
     */
    private void setShareIntent() {

        String taskList = "";
        for (TodoItem item : mTodoList.getTodoItems()) {
            taskList = taskList + "\n" + item.toString();
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, taskList);
        shareIntent.setType("text/plain");

        startActivity(shareIntent);

        /*if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }*/
    }

    public void refreshTaskList() {
        mTodoAdapter.setToDoItems(mTodoList.getTodoItems());
    }

    public void onAddItem(TodoItem item) {
        TodoList.get(this).addItem(item);
        mTodoAdapter.notifyItemInserted(mTodoList.getTodoItems().size());
        refreshTaskList();
    }

    @Override
    public void onItemClick(int position) {
        EditItem(position);
    }

    @Override
    public void onItemChecked(TodoItem item) {
        mTodoList.updateItem(item);
    }

    /**
     * Update the changes to the task details in the database
     * and update the UI
     */
    public void onUpdateItem(TodoItem item) {
        TodoList.get(this).updateItem(item);
        updateUI();
    }

    /**
     * Set or reset the alarm for notification
     */
    public void onUpdateNotification(TodoItem task) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.getDate());
        Intent alertIntent = new Intent(this, AlertReceiver.class);
        alertIntent.putExtra(Intent.EXTRA_TEXT, task.getTitle() + " " + task.getDescription());
        PendingIntent sender = PendingIntent.getBroadcast(this, (int) task.getId().getLeastSignificantBits(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        if (task.getNotify()) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        } else
            alarmManager.cancel(sender);
    }
}
