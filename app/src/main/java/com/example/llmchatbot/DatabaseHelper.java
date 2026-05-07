package com.example.llmchatbot;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chat_database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_SENDER = "sender";
    private static final String COLUMN_TIME = "time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_SENDER + " TEXT, " +
                COLUMN_TIME + " TEXT)";
        database.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public void addMessage(String username, String message, String sender, String time) {
        SQLiteDatabase database = this.getWritableDatabase();

        String insertQuery = "INSERT INTO " + TABLE_NAME +
                " (" + COLUMN_USERNAME + ", " + COLUMN_MESSAGE + ", " + COLUMN_SENDER + ", " + COLUMN_TIME + ") VALUES (?, ?, ?, ?)";

        database.execSQL(insertQuery, new Object[]{username, message, sender, time});
        database.close();
    }

    public ArrayList<Message> getMessages(String username) {
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                String messageText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                String sender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDER));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));

                messages.add(new Message(messageText, sender, time));

            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return messages;
    }
}