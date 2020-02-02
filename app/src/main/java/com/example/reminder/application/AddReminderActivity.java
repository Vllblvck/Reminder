package com.example.reminder.application;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.reminder.R;

import static com.example.reminder.application.MainActivity.EXTRA_REMINDER;

public class AddReminderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private EditText editTextTitle;
    private EditText editTextContent;
    private TextView textViewTimePickerValue;
    private TextView textViewDatePickerValue;
    private Calendar pickedDate = Calendar.getInstance();
    private Reminder reminder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        buildToolbar();
        initViews();
        checkRequest();
    }

    private void buildToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        editTextTitle = findViewById(R.id.edit_text_reminder_title);
        editTextContent = findViewById(R.id.edit_text_reminder_content);
        textViewDatePickerValue = findViewById(R.id.text_view_date_picker_value);
        textViewTimePickerValue = findViewById(R.id.text_view_time_picker_value);

        ImageButton datePicker = findViewById(R.id.button_reminder_date);
        datePicker.setOnClickListener(view -> {
            ReminderDatePicker datePickerDialog = new ReminderDatePicker();
            datePickerDialog.show(getSupportFragmentManager(), "datePicker");

        });

        ImageButton timePicker = findViewById(R.id.button_reminder_time);
        timePicker.setOnClickListener(view -> {
            ReminderTimePicker timePickerDialog = new ReminderTimePicker();
            timePickerDialog.show(getSupportFragmentManager(), "timePicker");
        });
    }

    private void checkRequest() {
        Intent intent = getIntent();
        int request = intent.getIntExtra(MainActivity.EXTRA_REQUEST, 0);

        if (request == MainActivity.EDIT_REMINDER_REQUEST) {
            reminder = (Reminder) intent.getSerializableExtra(MainActivity.EXTRA_REMINDER);
            editTextTitle.setText(reminder.getTitle());
            editTextContent.setText(reminder.getContent());
            textViewDatePickerValue.setText(reminder.getDateAsString());
            textViewTimePickerValue.setText(reminder.getTimeAsString());
        } else {
            int dayOfMonth = pickedDate.get(Calendar.DAY_OF_MONTH);
            int month = pickedDate.get(Calendar.MONTH);
            int year = pickedDate.get(Calendar.YEAR);
            int hourOfDay = pickedDate.get(Calendar.HOUR_OF_DAY);
            int minute = pickedDate.get(Calendar.MINUTE);
            String date = dayOfMonth + "/" + month + "/" + year;
            String time = hourOfDay + ":" + minute;

            textViewTimePickerValue.setText(time);
            textViewDatePickerValue.setText(date);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_reminder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.save_reminder) {
            String title = editTextTitle.getText().toString();
            String content = editTextContent.getText().toString();

            if (reminder == null) {
                reminder = new Reminder(title, content, pickedDate);
            } else {
                reminder.setTitle(title);
                reminder.setContent(content);
                reminder.setDate(pickedDate);
            }

            Intent saveIntent = new Intent();
            saveIntent.putExtra(EXTRA_REMINDER, reminder);
            setResult(Activity.RESULT_OK, saveIntent);
            finish();
            return true;
        }

        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
            return true;
        }

        return false;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        textViewTimePickerValue.setText(time);
        pickedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        pickedDate.set(Calendar.MINUTE, minute);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + month + "/" + year;
        textViewDatePickerValue.setText(date);
        pickedDate.set(Calendar.YEAR, year);
        pickedDate.set(Calendar.MONTH, month);
        pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }
}
