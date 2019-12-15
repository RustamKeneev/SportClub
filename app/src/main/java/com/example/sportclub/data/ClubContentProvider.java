package com.example.sportclub.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.example.sportclub.data.ClubContract.*;

public class ClubContentProvider extends ContentProvider  {

    ClubDatabaseHelper clubDatabaseHelper;
    private static final int MEMBERS = 111;
    private static final int MEMBER_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ClubContract.AUTHORITY,ClubContract.PATH_MEMBERS,MEMBERS);
        uriMatcher.addURI(ClubContract.AUTHORITY,ClubContract.PATH_MEMBERS + "/#",MEMBER_ID);
    }

    @Override
    public boolean onCreate() {
        clubDatabaseHelper = new ClubDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings,  String s, String[] strings1,  String s1) {
        SQLiteDatabase database = clubDatabaseHelper.getReadableDatabase();
        Cursor cursor;

        int match = uriMatcher.match(uri);
        switch (match){
            case MEMBERS:
                cursor = database.query(MemberEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                break;
            case MEMBER_ID:
                s = MemberEntry._ID + "=?";
                strings1 = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MemberEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                 break;
                 default:
                     Toast.makeText(getContext(),"Incorrect URI",Toast.LENGTH_LONG).show();
                     throw new IllegalArgumentException("Can't query incorrect URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri );
        return cursor;
    }


    @Nullable
    @Override
    public Uri insert( Uri uri,  ContentValues contentValues) {
        String firstName = contentValues.getAsString(MemberEntry.COLUMN_FIRST_NAME);
        if (firstName == null){
            throw new IllegalArgumentException("You have to input first-name" );
        }
        String lastName = contentValues.getAsString(MemberEntry.COLUMN_LAST_NAME );
        if (lastName == null) {
            throw new IllegalArgumentException("You have to input last-name");
        }
        Integer gender = contentValues.getAsInteger(MemberEntry.COLUMN_GENDER);
        if (gender == null || !(gender == MemberEntry.GENDER_UNKNOWN || gender == MemberEntry.GENDER_FEMALE ||
                gender == MemberEntry.GENDER_FEMALE)){
             throw  new IllegalArgumentException("You have to input correct gender");
        }
        String sport = contentValues.getAsString(MemberEntry.COLUMN_SPORT );
        if (sport == null) {
            throw new IllegalArgumentException("You have to input last-name");
        }
            SQLiteDatabase database = clubDatabaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match){
            case MEMBERS:
               long id =  database.insert(MemberEntry.TABLE_NAME,null,contentValues);
               if (id == -1){
                    Toast.makeText(getContext(),"Insert method failed" +uri,Toast.LENGTH_LONG).show();
                    return null;
               }
               getContext().getContentResolver().notifyChange(uri,null);
               return  ContentUris.withAppendedId(uri,id);

            default:
                Toast.makeText(getContext(),"Incorrect URI",Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't insert incorrect URI " + uri);
        }
    }

    @Override
    public int delete( Uri uri,  String s,  String[] strings) {

        SQLiteDatabase database = clubDatabaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch (match){
            case MEMBERS:
                rowsDeleted =  database.delete(MemberEntry.TABLE_NAME ,s,strings);
                break;
             case MEMBER_ID:
                s = MemberEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted =  database.delete(MemberEntry.TABLE_NAME ,s,strings);
                break;
            default:
                Toast.makeText(getContext(),"Incorrect URI",Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't delete this URI " + uri);
        }
        if (rowsDeleted !=0){
             getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update( Uri uri,  ContentValues contentValues,  String s,  String[] strings) {
        if (contentValues.containsKey(MemberEntry.COLUMN_FIRST_NAME)){
            String firstName = contentValues.getAsString(MemberEntry.COLUMN_FIRST_NAME);
            if (firstName == null){
                throw new IllegalArgumentException("You have to input first-name" );
            }
        }
        if (contentValues.containsKey(MemberEntry.COLUMN_LAST_NAME)){
            String lastName = contentValues.getAsString(MemberEntry.COLUMN_LAST_NAME );
            if (lastName == null) {
                throw new IllegalArgumentException("You have to input last-name");
            }
        }

        if (contentValues.containsKey(MemberEntry.COLUMN_GENDER)){
            Integer gender = contentValues.getAsInteger(MemberEntry.COLUMN_GENDER);
            if (gender == null || !(gender == MemberEntry.GENDER_UNKNOWN || gender == MemberEntry.GENDER_FEMALE ||
                    gender == MemberEntry.GENDER_FEMALE)){
                throw  new IllegalArgumentException("You have to input correct gender");
            }
        }
        if (contentValues.containsKey(MemberEntry.COLUMN_SPORT)){
            String sport = contentValues.getAsString(MemberEntry.COLUMN_SPORT );
            if (sport == null) {
                throw new IllegalArgumentException("You have to input last-name");
            }
        }

        SQLiteDatabase database = clubDatabaseHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsUpdates;
        switch (match){
            case MEMBERS:
                 rowsUpdates = database.update(MemberEntry.TABLE_NAME,contentValues,s,strings);
                break;
            case MEMBER_ID:
                s = MemberEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdates = database.update(MemberEntry.TABLE_NAME,contentValues,s,strings);
                    break;
                default:
                Toast.makeText(getContext(),"Incorrect URI",Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Can't update  URI " + uri);
        }
        if (rowsUpdates !=0 ){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdates;
    }

    @Nullable
    @Override
    public String getType( Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match){
            case MEMBERS:
                return MemberEntry.CONTENT_MULTIPLE_ITEMS;
            case MEMBER_ID:
                return MemberEntry.CONTENT_SINGLE_ITEM;
            default:
                Toast.makeText(getContext(),"Unknown URI",Toast.LENGTH_LONG).show();
                throw new IllegalArgumentException("Unknown  URI:" + uri);
        }
    }
}
