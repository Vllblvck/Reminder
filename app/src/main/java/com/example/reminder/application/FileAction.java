package com.example.reminder.application;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileAction {
    private static final String REMINDERS_FILE = "reminders";
    private static final String ARCHIVE_FILE = "archive.txt";

    public static List<Reminder> loadReminders(Context context) {

        ArrayList<Reminder> reminders = new ArrayList<>();

        try (FileInputStream fileInput = context.openFileInput(REMINDERS_FILE);
             ObjectInputStream inputStream = new ObjectInputStream(fileInput)) {

            reminders = (ArrayList<Reminder>) inputStream.readObject();
            return reminders;

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return reminders;
        }
    }

    public static void saveReminders(Context context, List<Reminder> reminders) {
        try (FileOutputStream fileOutput = context.openFileOutput(REMINDERS_FILE, Context.MODE_PRIVATE);
             ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {

            objectOutput.writeObject(reminders);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveToArchive(Context context, String title, String content, String date) {
        try (OutputStreamWriter outputStream =
                     new OutputStreamWriter(context.openFileOutput(ARCHIVE_FILE, Context.MODE_APPEND))) {

            outputStream.write(title);
            outputStream.write("\n");
            outputStream.write(content);
            outputStream.write("\n");
            outputStream.write(date);
            outputStream.write("\n");
            outputStream.write("------------------------------------------------------------");
            outputStream.write("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromArchive(Context context) {
        StringBuilder stringBuilder = new StringBuilder();

        try (InputStream inputStream = context.openFileInput(ARCHIVE_FILE);
             InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(streamReader)) {

            String receiveString;

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString().trim();
    }
}
