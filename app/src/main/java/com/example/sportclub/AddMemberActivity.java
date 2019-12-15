package com.example.sportclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sportclub.data.ClubContract.MemberEntry;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText edit_first_name;
    private EditText edit_last_name;
    private Spinner spinner_gender;
    private EditText edit_sport;
    private int gender = 0;
    private ArrayAdapter arrayAdapterSpinner;
    private Uri currentMemberUri;
    private static final int EDIT_MEMBER_LOADER = 145;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Intent intent = getIntent();
        currentMemberUri = intent.getData();
        if (currentMemberUri == null){
            setTitle("Add a Member");
            invalidateOptionsMenu();
        }else {
            setTitle("Edit the Member");
            getSupportLoaderManager().initLoader(EDIT_MEMBER_LOADER,null,this);
        }
        initViews();
        setupSpiner();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentMemberUri == null){
            MenuItem menuItem = menu.findItem(R.id.delete_member);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void setupSpiner() {
        arrayAdapterSpinner = ArrayAdapter.createFromResource(
                this,R.array.array_gender,android.R.layout.simple_spinner_item);
        arrayAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gender.setAdapter(arrayAdapterSpinner);
        spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedGender = (String) adapterView.getItemAtPosition(i);
                if (!TextUtils.isEmpty(selectedGender)){
                    if (selectedGender.equals("Male")){
                        gender = MemberEntry.GENDER_MALE;
                    }else if (selectedGender.equals("Female")){
                        gender = MemberEntry.GENDER_FEMALE;
                    }else {
                        gender = MemberEntry.GENDER_UNKNOWN;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                gender = 0;
            }
        });
     }

    private void initViews() {
        edit_first_name = findViewById(R.id.edit_first_name);
        edit_last_name = findViewById(R.id.edit_last_name);
        spinner_gender = findViewById(R.id.spinner_gender);
        edit_sport = findViewById(R.id.edit_group);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_member_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_member:
                saveMember();
                return true;
            case R.id.delete_member:
                showDeleteMemberDialog();
                 return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want delete the member?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteMember();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface !=null){
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMember(){
        if (currentMemberUri !=null){
            int rowsDeleted =  getContentResolver().delete(currentMemberUri,null,null);
            if (rowsDeleted == 0 ){
                Toast.makeText(this,"Deleting of data in the table failed",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this,"Member is deleted ",Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    private void saveMember(){
        String firstName = edit_first_name.getText().toString().trim();
        String lastName = edit_last_name.getText().toString().trim();
        String sport = edit_sport.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)){
            Toast.makeText(this,"Input the first name",Toast.LENGTH_LONG).show();
            return;
        }else if (TextUtils.isEmpty(lastName)){
            Toast.makeText(this,"Input the last name",Toast.LENGTH_LONG).show();
            return;
        }else if (TextUtils.isEmpty(sport)){
            Toast.makeText(this,"Input the sport",Toast.LENGTH_LONG).show();
            return;
        }else if (gender == MemberEntry.GENDER_UNKNOWN){
            Toast.makeText(this,"Choose the gender",Toast.LENGTH_LONG).show();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MemberEntry.COLUMN_FIRST_NAME,firstName);
        contentValues.put(MemberEntry.COLUMN_LAST_NAME,lastName);
        contentValues.put(MemberEntry.COLUMN_SPORT,sport);
        contentValues.put(MemberEntry.COLUMN_GENDER,gender);

        if (currentMemberUri == null){
            ContentResolver contentResolver = getContentResolver();
            Uri uri = contentResolver.insert(MemberEntry.CONTENT_URI, contentValues);

            if (uri == null){
                Toast.makeText(this,"Insert data  failed",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this,"data saved", Toast.LENGTH_LONG).show();
            }
        }else {
            int rowsChanged = getContentResolver().update(currentMemberUri,contentValues,null,null);
            if (rowsChanged == 0){
                Toast.makeText(this,"Saving data  failed",Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this," member data update", Toast.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                MemberEntry._ID,MemberEntry.COLUMN_FIRST_NAME, MemberEntry.COLUMN_LAST_NAME,
                MemberEntry.COLUMN_GENDER,MemberEntry.COLUMN_SPORT
        };
        return new CursorLoader(this,currentMemberUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.moveToNext()){
            int firstNameColumnIndex = data.getColumnIndex(MemberEntry.COLUMN_FIRST_NAME);
            int lastNameColumnIndex = data.getColumnIndex(MemberEntry.COLUMN_LAST_NAME);
            int genderColumnIndex = data.getColumnIndex(MemberEntry.COLUMN_GENDER);
            int sportColumnIndex = data.getColumnIndex(MemberEntry.COLUMN_SPORT);
            String firstName = data.getString(firstNameColumnIndex);
            String lastName = data.getString(lastNameColumnIndex);
            int gender = data.getInt(genderColumnIndex);
            String sport = data.getString(sportColumnIndex);

            edit_first_name.setText(firstName);
            edit_last_name.setText(lastName);
            edit_sport.setText(sport);

            switch (gender){
                case MemberEntry.GENDER_MALE:
                    spinner_gender.setSelection(1);
                    break;
                    case MemberEntry.GENDER_FEMALE:
                        spinner_gender.setSelection(2);
                        break;
                 case MemberEntry.GENDER_UNKNOWN:
                     spinner_gender.setSelection(0);
                     break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


}
