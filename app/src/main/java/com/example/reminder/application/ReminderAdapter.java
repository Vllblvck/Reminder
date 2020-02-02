package com.example.reminder.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminder.R;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminders;
    private List<Reminder> selectedReminders = new ArrayList<>();
    private List<CheckBox> checkBoxes = new ArrayList<>();
    private boolean multiselect;
    private ActionMode actionMode;
    private ReminderListener listener;


    public ReminderAdapter(List<Reminder> reminders, ReminderListener listener) {
        this.reminders = reminders;
        this.listener = listener;
        multiselect = false;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.reminder_view_holder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder currReminder = reminders.get(position);
        String date = currReminder.getDateAsString() + " " + currReminder.getTimeAsString();
        holder.date.setText(date);
        holder.title.setText(currReminder.getTitle());
        checkBoxes.add(holder.checkBox);
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiselect = true;
            mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
            showCheckBoxes();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.select_all:
                    if (selectedReminders.size() == reminders.size()) {
                        selectedReminders.clear();

                        for (CheckBox checkBox : checkBoxes) {
                            checkBox.setChecked(false);
                        }

                        return true;
                    }

                    selectedReminders.clear();
                    selectedReminders.addAll(reminders);

                    for (CheckBox checkBox : checkBoxes) {
                        checkBox.setChecked(true);
                    }

                    return true;

                case R.id.delete:
                    listener.onReminderDelete(selectedReminders);
                    mode.finish();
                    return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            multiselect = false;
            hideCheckBoxes();
        }
    };

    private void selectItem(int position, CheckBox checkBox) {
        Reminder item = reminders.get(position);

        if (!selectedReminders.contains(item)) {
            selectedReminders.add(item);
            checkBox.setChecked(true);
        } else {
            selectedReminders.remove(item);
            checkBox.setChecked(false);
        }
    }

    private void showCheckBoxes() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
            checkBox.setVisibility(View.VISIBLE);
        }
    }

    private void hideCheckBoxes() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    public interface ReminderListener {
        void onReminderClick(Reminder reminder);

        void onReminderDelete(List<Reminder> selectedReminders);
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView date;
        public CheckBox checkBox;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reminder_title);
            date = itemView.findViewById(R.id.reminder_date);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(view -> {
                if (multiselect) {
                    selectItem(getAdapterPosition(), checkBox);
                } else {
                    listener.onReminderClick(reminders.get(getAdapterPosition()));
                }
            });

            itemView.setOnLongClickListener(view -> {
                if (actionMode == null) {
                    MainActivity mainContext = (MainActivity) itemView.getContext();
                    actionMode = mainContext.startSupportActionMode(actionModeCallback);
                    selectItem(getAdapterPosition(), checkBox);
                    return true;
                }

                return false;
            });
        }
    }
}
