package com.example.weclass.calendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.weclass.DatePickerFragment;
import com.example.weclass.ExtendedRecyclerView;
import com.example.weclass.R;
import com.example.weclass.Settings;
import com.example.weclass.SharedPref;
import com.example.weclass.archive.Archive;
import com.example.weclass.database.DataBaseHelper;
import com.example.weclass.subject.Subject;
import com.example.weclass.taskGrade.TaskGrade;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarEvents extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, CalendarAdapter.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    SharedPref sharedPref;
    TextView currentDateTextView, noTextView;
    View noView;
    MaterialButton buttonCalendar;
    ExtendedRecyclerView recyclerView;
    DataBaseHelper dataBaseHelper;
    CalendarAdapter calendarAdapter;
    ArrayList<CalendarItems> calendarItems;
    String date;
    String currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //This will set the theme depends on save state of switch button
        sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState()){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(CalendarEvents.this, R.color.titleBar));
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_events);

        initialize();
        displayDate();
        navigationOpen();
        setDate();
        setData();
        initializeAdapter();

    }

    @Override
    protected void onResume() {
        super.onResume();

        initialize();
        setDate();
        setData();
        initializeAdapter();

    }

    public void initialize(){
        toolbar = findViewById(R.id.toolbarCalendar);
        navigationView = findViewById(R.id.navViewCalendar);
        drawerLayout = findViewById(R.id.drawerCalendar);
        currentDateTextView = findViewById(R.id.dateCalendar);
        buttonCalendar = findViewById(R.id.buttonCalendar);
        recyclerView = findViewById(R.id.recViewCalendar);
        noTextView = findViewById(R.id.noStudentTextViewCalendar);
        noView = findViewById(R.id.noStudentViewCalendar);
    }

    public void initializeAdapter(){
        calendarAdapter = new CalendarAdapter(this, calendarItems, this);
        recyclerView.setAdapter(calendarAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setEmptyView(noView,noTextView);
    }

    // DATE PICKER
    public void setDate() {
        buttonCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
    }

    public void setData(){
        calendarItems=new ArrayList<>();
        dataBaseHelper = new DataBaseHelper(this);
        calendarItems = displayData();

    }

    private ArrayList<CalendarItems> displayData(){

        SQLiteDatabase sql = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sql.rawQuery("SELECT * FROM "
                + DataBaseHelper.TABLE_MY_SUBJECTS + " LEFT JOIN "
                + DataBaseHelper.TABLE_MY_TASKS + " ON "
                + DataBaseHelper.TABLE_MY_SUBJECTS + "."
                + DataBaseHelper.COLUMN_ID + " = "
                + DataBaseHelper.TABLE_MY_TASKS + "."
                + DataBaseHelper.COLUMN_PARENT_ID_SUBJECT + " WHERE "
                + DataBaseHelper.TABLE_MY_TASKS + "."
                + DataBaseHelper.COLUMN_DUE_DATE + " = '"
                + currentDate + "'", null);

        ArrayList<CalendarItems> calendarItems = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                calendarItems.add(new CalendarItems(
                        cursor.getInt(0),
                        cursor.getString(15),
                        cursor.getString(20),
                        cursor.getString(1),
                        cursor.getString(18),
                        cursor.getInt(13),
                        cursor.getString(21),
                        cursor.getString(6)));

            }while (cursor.moveToNext());

            }
        cursor.close();
        sql.close();

        return calendarItems;

    }

    public void displayDate(){
        Calendar calendar = Calendar.getInstance();


        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.ENGLISH);
        date = dateFormat.format(calendar.getTime());
        currentDateTextView.setText(date);
        currentDate = date;
        //Toast.makeText(this," " + currentDateTextView,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.ENGLISH);
        currentDate = dateFormat.format(calendar.getTime());
        currentDateTextView.setText(currentDate);

        setData();
        initializeAdapter();
    }

    public void navigationOpen() {
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();     // Show navigation drawer when clicked

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.mm1);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawerSubject:
                        Intent intent = new Intent(CalendarEvents.this, Subject.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.drawerArchive:
                        intent = new Intent(CalendarEvents.this, Archive.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.drawerSettings:
                        intent = new Intent(CalendarEvents.this, Settings.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.drawerLogout:
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CalendarEvents.this);
                        builder.setTitle("Confirm Exit");
                        builder.setIcon(R.drawable.ic_baseline_warning_24);
                        builder.setMessage("Are you sure you want to exit?");
                        builder.setCancelable(false)
                                .setPositiveButton("Yes", (dialog, id) -> finishAffinity())
                                .setNegativeButton("No", null)
                                .show();
                        break;
                }
                return true;
            }
        }); //navigation drawer item clickable
    }


    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(CalendarEvents.this, TaskGrade.class);
        intent.putExtra("Calendar", calendarItems.get(position));
        startActivity(intent);
    }
}