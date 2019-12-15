package com.example.sportclub.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sportclub.data.ClubContract.MemberEntry;

public class ClubDatabaseHelper extends SQLiteOpenHelper {
    public ClubDatabaseHelper(Context context) {
        super(context, ClubContract.DATABASE_NAME, null, ClubContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_MEMBERS_TABLE = "CREATE TABLE " + MemberEntry.TABLE_NAME + "(" +
        MemberEntry._ID + " INTEGER PRIMARY KEY," +
        MemberEntry.COLUMN_FIRST_NAME + " TEXT," +
        MemberEntry.COLUMN_LAST_NAME + " TEXT," +
        MemberEntry.COLUMN_GENDER + " INTEGER NOT NULL," +
        MemberEntry.COLUMN_SPORT + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_MEMBERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF  EXISTS " + ClubContract.DATABASE_NAME);
        onCreate(sqLiteDatabase);
    }
}
