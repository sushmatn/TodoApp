package com.sushmanayak.android.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sushmanayak.android.todoapp.Db.TodoList;
import com.sushmanayak.android.todoapp.adapter.TodoAdapter;
import com.sushmanayak.android.todoapp.model.TodoItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ItemDetailsFragment.ItemDetailsListener, TodoAdapter.TodoItemListeners {

    Toolbar mToolbar;
    TodoAdapter mTodoAdapter;
    RecyclerView mTodoRecyclerView;
    FloatingActionButton addItemButton;
    TodoList mTodoList;

    private static final int NEW_TASK_INDEX = -1;
    private static final String EDIT_TASK_DIALOG = "Edit task";
    private static final int ANIM_DURATION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTodoRecyclerView = (RecyclerView) findViewById(R.id.todoListView);
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTodoRecyclerView.setHasFixedSize(true);

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(ANIM_DURATION);
        animator.setRemoveDuration(ANIM_DURATION);
        mTodoRecyclerView.setItemAnimator(animator);

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

    private void deleteAllCompletedTasks() {
        ArrayList<TodoItem> todoItems = TodoList.get(this).getTodoItems();
        for (TodoItem item : todoItems) {
            if (item.isCompleted())
                TodoList.get(this).removeItem(item);
        }
        updateUI();
    }

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

    // Call to update the share intent
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

    @Override
    public void onLongItemClick(int position) {
        mTodoList.removeItem(mTodoList.getTodoItems().get(position));
        mTodoAdapter.notifyItemRemoved(position);
        refreshTaskList();
    }

    public void onUpdateItem(TodoItem item) {
        TodoList.get(this).updateItem(item);
        updateUI();
    }
}
