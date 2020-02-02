package com.example.reminder.application;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminder.R;
import com.example.reminder.notification.AlarmReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ReminderAdapter.ReminderListener {
    public static final String EXTRA_REMINDER = "com.example.reminder.application.EXTRA_REMINDER";
    public static final String EXTRA_REQUEST = "com.exmaple.reminder.application.EXTRA_REQUEST";
    public static final String EXTRA_TITLE = "com.example.reminder.application.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "com.example.reminder.application.EXTRA_CONTENT";
    public static final String EXTRA_ID = "com.example.reminder.application.EXTRA_ID";
    public static final String EXTRA_DATE = "com.example.reminder.application.EXTRA_DATE";
    public static final int ADD_REMINDER_REQUEST = 1;
    public static final int EDIT_REMINDER_REQUEST = 2;
    public static final int DELETE_REMINDER_REQUEST = 3;
    private List<Reminder> allReminders = new ArrayList<>();
    private ReminderAdapter reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildToolbar();
        buildFab();
        buildRecyclerView();
    }

    private void buildToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void buildFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent addIntent = new Intent(this, AddReminderActivity.class);
            addIntent.putExtra(EXTRA_REQUEST, ADD_REMINDER_REQUEST);
            startActivityForResult(addIntent, ADD_REMINDER_REQUEST);
        });
    }

    private void buildRecyclerView() {
        allReminders = FileAction.loadReminders(this);
        reminderAdapter = new ReminderAdapter(allReminders, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        dividerItemDecoration.setDrawable(getDrawable(R.drawable.separator_shape));
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(reminderAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.archive) {
            Intent intent = new Intent(this, ArchiveActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Reminder reminder = (Reminder) data.getSerializableExtra(EXTRA_REMINDER);

            switch (requestCode) {
                case ADD_REMINDER_REQUEST:
                    allReminders.add(reminder);
                    saveData();
                    setNotification(reminder);
                    break;

                case EDIT_REMINDER_REQUEST:
                    for (int i = 0; i < allReminders.size(); i++) {
                        if (allReminders.get(i).getId() == reminder.getId()) {
                            allReminders.set(i, reminder);
                            saveData();
                            setNotification(reminder);
                        }
                    }
                    break;

                case DELETE_REMINDER_REQUEST:
            }
        }
    }

    @Override
    public void onReminderClick(Reminder reminder) {
        Intent editIntent = new Intent(this, AddReminderActivity.class);
        editIntent.putExtra(EXTRA_REMINDER, reminder);
        editIntent.putExtra(EXTRA_REQUEST, EDIT_REMINDER_REQUEST);
        startActivityForResult(editIntent, EDIT_REMINDER_REQUEST);
    }

    @Override
    public void onReminderDelete(List<Reminder> selectedReminders) {
        Iterator<Reminder> reminderIter = allReminders.iterator();

        while (reminderIter.hasNext()) {
            Reminder reminder = reminderIter.next();

            for (Reminder item : selectedReminders) {
                if (reminder.getId() == item.getId()) {
                    reminderIter.remove();
                }
            }
        }

        saveData();
    }

    private void saveData() {
        FileAction.saveReminders(this, allReminders);
        reminderAdapter.setReminders(allReminders);
        reminderAdapter.notifyDataSetChanged();
    }

    private void setNotification(Reminder reminder) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(EXTRA_TITLE, reminder.getTitle());
        intent.putExtra(EXTRA_CONTENT, reminder.getContent());
        intent.putExtra(EXTRA_ID, reminder.getId());
        intent.putExtra(EXTRA_DATE, reminder.getDateAsString() + " " + reminder.getTimeAsString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.getDate().getTimeInMillis(), pendingIntent);
    }
}
