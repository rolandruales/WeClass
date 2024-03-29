package com.example.weclass.taskGrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weclass.ExtendedRecyclerView;
import com.example.weclass.R;
import com.example.weclass.SharedPref;
import com.example.weclass.calendar.CalendarItems;
import com.example.weclass.database.DataBaseHelper;
import com.example.weclass.studentlist.profile.activities.ActivitiesItems;
import com.example.weclass.tasks.TaskItems;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;

public class TaskGrade extends AppCompatActivity{

    ExtendedRecyclerView recyclerView;
    ImageView backButton;
    TextView _progress, _score, _description, _taskType, _taskNumber,
            _subjectID , _noStudentToGradeTextView, _gradingPeriod, _due,
            totalStudent, gradedStudent, _taskId;
    View _noStudentToGradeView;
    TabLayout _tabLayout;
    ViewPager2 _viewPager2;
    TaskGradeViewPagerAdapter taskGradeViewPagerAdapter;
    SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);

        if (sharedPref.loadNightModeState()){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        }else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_grade);

        //status bar white background
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.WHITE);

        initialize();
        getDataFromTaskRecView();
        backToTask();
        fragmentManager();
        getSumOfStudents();
        gradedStudent();
        updateGradedStudents();
        insertDataToTaskGrade();




    }

    @Override
    public void onResume() {
        initialize();
        getDataFromTaskRecView();
        backToTask();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.transition.animation_enter,R.transition.animation_leave);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }



    private void initialize() {
        backButton = findViewById(R.id.backToTaskButton);
        _progress = findViewById(R.id.progressTextViewGrade);
        _score = findViewById(R.id.scoreTextViewGrade);
        _description = findViewById(R.id.descriptionTextViewGrade);
        _taskType = findViewById(R.id.taskTypeTextViewGrade);
        _taskNumber = findViewById(R.id.taskNumberTextViewGrade);
        _subjectID = findViewById(R.id.subjectIDTextViewGrade);
        recyclerView = findViewById(R.id.taskGradeRecyclerView);
        _noStudentToGradeTextView = findViewById(R.id.noStudentTextViewGrade);
        _noStudentToGradeView = findViewById(R.id.noStudentViewGrade);
        _gradingPeriod = findViewById(R.id.gradingPeriodTextViewTaskGrade);
        _viewPager2 = findViewById(R.id.viewPagerTaskGrade);
        _tabLayout = findViewById(R.id.tabLayoutTaskGrade);
        _due = findViewById(R.id.dueTextViewTaskGrade);
        gradedStudent = findViewById(R.id.gradedTextViewTaskGraded);
        totalStudent = findViewById(R.id.totalStudentTextViewTaskGrade);
        _taskId = findViewById(R.id.taskIdTaskGrade);

    }

    private void backToTask(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.transition.animation_enter,R.transition.animation_leave);
            }
        });
    }

    public void getDataFromTaskRecView(){
        Intent intent = getIntent();
        TaskItems taskItems = intent.getParcelableExtra("Task");
        String subjectID = intent.getStringExtra("id");
        CalendarItems calendarItems = intent.getParcelableExtra("Calendar");

        if (taskItems!=null) {
            String progress = taskItems.getProgress();
            String score = taskItems.getScore();
            String description = taskItems.getTaskDescription();
            String taskType = taskItems.getTaskType();
            String period = taskItems.getGradingPeriod();
            String due = taskItems.getDue();
            int taskId = taskItems.getTaskID();
            int taskNumber = taskItems.getTaskNumber();
            String a = "                        ";
            String c = a + description;

            _progress.setText(progress);
            _score.setText(score);
            _description.setText(c);
            _taskType.setText(taskType);
            _taskNumber.setText(String.valueOf(taskNumber));
            _subjectID.setText(subjectID);
            _gradingPeriod.setText(period);
            _due.setText(due);
            _taskId.setText(String.valueOf(taskId));
        } else {
            int taskId = calendarItems.getTaskId();
            int parentId = calendarItems.getParentId();

            getDataFromDb(parentId, taskId);

        }
    }

    // this method display data from database
    private void getDataFromDb(int parentId, int taskId){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(" select * from "
                + DataBaseHelper.TABLE_MY_TASKS + " where "
                + DataBaseHelper.COLUMN_PARENT_ID_SUBJECT + " = "
                + parentId + " and "
                + DataBaseHelper.COLUMN_ID4 + " = "
                + taskId, null);
        if (cursor.moveToFirst()){
            _progress.setText(cursor.getString(6));
            _score.setText(cursor.getString(4));
            _description.setText(cursor.getString(5));
            _taskType.setText(cursor.getString(2));
            _taskNumber.setText(cursor.getString(7));
            _gradingPeriod.setText(cursor.getString(8));
             _due.setText(cursor.getString(3));
            _subjectID.setText(String.valueOf(cursor.getInt(1)));
            String a = "                        ";
            String desc = cursor.getString(5);
            String b = a + desc;
            _description.setText(b);
            cursor.close();
        }
    }

    // Method for tab layout and viewpager2
    public void fragmentManager(){

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Pass data from this activity to pager adapter
        String taskType = _taskType.getText().toString();
        String taskNumber = _taskNumber.getText().toString();
        String gradingPeriod = _gradingPeriod.getText().toString();
        String subjectId = _subjectID.getText().toString();
        String idTask = _taskId.getText().toString();

        taskGradeViewPagerAdapter = new TaskGradeViewPagerAdapter(fragmentManager, getLifecycle(), taskType, taskNumber, gradingPeriod, subjectId, idTask);


        _viewPager2.setAdapter(taskGradeViewPagerAdapter);

        _tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                _viewPager2.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        _viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                _tabLayout.selectTab(_tabLayout.getTabAt(position));
            }
        });
    }

    public void getTotalAndGradedStudents(){
        String getTotal = totalStudent.getText().toString();
        String getGraded = gradedStudent.getText().toString();
        int a = Integer.parseInt(getTotal);
        int b = Integer.parseInt(getGraded);
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);

        if(a == b){
            String completed = "Completed";
            _progress.setText(completed);

            dataBaseHelper.updateTaskProgress(_taskId.getText().toString(),
                    _subjectID.getText().toString(),
                    completed);

        }else  {
            String inProgress = "In-progress";
            _progress.setText(inProgress);

            dataBaseHelper.updateTaskProgress(_taskId.getText().toString(),
                    _subjectID.getText().toString(),
                    inProgress);
        }
    }

    // GET SUM OF ALL STUDENTS BASED ON THEIR SUBJECT ID
    public void getSumOfStudents(){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(" SELECT COUNT(*) FROM "
                + DataBaseHelper.TABLE_MY_STUDENTS + " WHERE "
                + DataBaseHelper.COLUMN_PARENT_ID + " = "
                + _subjectID.getText().toString(), null);

        if (cursor.moveToFirst() ){
            totalStudent.setText(String.valueOf(cursor.getInt(0)));
            cursor.close();
        }
    }

    public void gradedStudent(){

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(" SELECT COUNT(*) FROM "
                + DataBaseHelper.TABLE_MY_GRADE + " WHERE "
                + DataBaseHelper.COLUMN_PARENT_ID_MY_GRADE + " = "
                + _subjectID.getText().toString() + " AND "
                + DataBaseHelper.COLUMN_TASK_TYPE_MY_GRADE + " ='"
                + _taskType.getText().toString() + "' AND "
                + DataBaseHelper.COLUMN_TASK_NUMBER_MY_GRADE + " = "
                + _taskNumber.getText().toString() + " AND "
                + DataBaseHelper.COLUMN_GRADE_MY_GRADE + " != '" + "" + "' AND "
                + DataBaseHelper.COLUMN_GRADING_PERIOD_MY_GRADE + "='"
                + _gradingPeriod.getText().toString() + "'", null);

        if (cursor.moveToFirst() ){
            gradedStudent.setText(String.valueOf(cursor.getInt(0)));
            cursor.close();
        }

    }

    public void updateGradedStudents(){
        Thread thread = new Thread(){

            @Override
            public void run() {
                try {
                    while (!isInterrupted()){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                gradedStudent();
                                getTotalAndGradedStudents();

                            }
                        });
                    }
                } catch (InterruptedException ignored){

                }
            }

        };
        thread.start();
    }

    public void insertDataToTaskGrade(){
        DataBaseHelper dbHelper = DataBaseHelper.getInstance(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        SQLiteDatabase sqL = dbHelper.getWritableDatabase();


        // MERGE 2 TABLES USING LEFT JOIN

        Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * FROM "
                + DataBaseHelper.TABLE_MY_STUDENTS + " LEFT JOIN "
                + DataBaseHelper.TABLE_MY_TASKS + " ON "
                + DataBaseHelper.TABLE_MY_STUDENTS + "."
                + DataBaseHelper.COLUMN_PARENT_ID + " = "
                + DataBaseHelper.TABLE_MY_TASKS + "."
                + DataBaseHelper.COLUMN_PARENT_ID_SUBJECT + " WHERE "
                + DataBaseHelper.TABLE_MY_STUDENTS + "."
                + DataBaseHelper.COLUMN_PARENT_ID + " = "
                + _subjectID.getText().toString() + " AND "
                + DataBaseHelper.COLUMN_TASK_TYPE + " = '"
                + _taskType.getText().toString() + "' AND "
                + DataBaseHelper.COLUMN_TASK_NUMBER + " = "
                + _taskNumber.getText().toString() + " AND "
                + DataBaseHelper.COLUMN_GRADING_PERIOD_TASK + " = '"
                + _gradingPeriod.getText().toString() + "'", null);

        if (cursor.moveToFirst()){
            do {
                Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM "
                        + DataBaseHelper.TABLE_MY_GRADE + " WHERE "
                        + DataBaseHelper.COLUMN_STUDENT_ID_MY_GRADE + " = '"
                        + cursor.getString(1) + "'" + " AND "
                        + DataBaseHelper.COLUMN_PARENT_ID_MY_GRADE + " = "
                        + _subjectID.getText().toString() + " AND "
                        + DataBaseHelper.COLUMN_TASK_TYPE_MY_GRADE + " = '"
                        + _taskType.getText().toString() + "' AND "
                        + DataBaseHelper.COLUMN_TASK_NUMBER_MY_GRADE + " = "
                        + _taskNumber.getText().toString() + " AND "
                        + DataBaseHelper.COLUMN_GRADING_PERIOD_MY_GRADE + " ='"
                        + _gradingPeriod.getText().toString() + "'", null);

                if (!c.moveToFirst()){
                    ContentValues cv = new ContentValues();
                    cv.put(DataBaseHelper.COLUMN_STUDENT_ID_MY_GRADE, cursor.getString(1));
                    cv.put(DataBaseHelper.COLUMN_TASK_ID_MY_GRADE, _taskId.getText().toString());
                    cv.put(DataBaseHelper.COLUMN_PARENT_ID_MY_GRADE, _subjectID.getText().toString());
                    cv.put(DataBaseHelper.COLUMN_LAST_NAME_MY_GRADE, cursor.getString(3));
                    cv.put(DataBaseHelper.COLUMN_FIRST_NAME_MY_GRADE, cursor.getString(4));
                    cv.put(DataBaseHelper.COLUMN_TASK_TYPE_MY_GRADE, _taskType.getText().toString());
                    cv.put(DataBaseHelper.COLUMN_TASK_NUMBER_MY_GRADE, _taskNumber.getText().toString());
                    cv.put(DataBaseHelper.COLUMN_GRADE_MY_GRADE, "");
                    cv.put(DataBaseHelper.COLUMN_GRADING_PERIOD_MY_GRADE, _gradingPeriod.getText().toString());
                    cv.put(DataBaseHelper.COLUMN_ITEMS_MY_GRADE, _score.getText().toString());
                    sqL.insert(DataBaseHelper.TABLE_MY_GRADE, null,cv);
                    c.close();
                }

            }while (cursor.moveToNext());

        }cursor.close();
        dbHelper.close();
        sqLiteDatabase.close();
        sqL.close();


    }

}