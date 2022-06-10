package com.example.weclass.taskGrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weclass.R;
import com.example.weclass.database.DataBaseHelper;
import com.example.weclass.tasks.TaskAdapter;
import com.example.weclass.tasks.TaskItems;

import java.util.ArrayList;

public class TaskGrade extends AppCompatActivity implements TaskGradeAdapter.OnNoteListener {

    RecyclerView recyclerView;
    ArrayList<TaskGradeItems> taskGradeItems, studentID, subjectID;
    TaskGradeAdapter taskGradeAdapter;
    DataBaseHelper dataBaseHelper;
    ImageView backButton;
    TextView _progress, _deadline, _score, _description, _taskType, _taskNumber, _subjectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_grade);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);    //enable full screen

        initialize();
        getDataFromTaskRecView();
        display();
        initializeAdapter();
        backToTask();

    }

    private void initialize() {
        backButton = findViewById(R.id.backToTaskButton);
        _progress = findViewById(R.id.proressTextViewGrade);
        _deadline = findViewById(R.id.deadLineTextViewGrade);
        _score = findViewById(R.id.scoreTextViewGrade);
        _description = findViewById(R.id.descriptionTextViewGrade);
        _taskType = findViewById(R.id.taskTypeTextViewGrade);
        _taskNumber = findViewById(R.id.taskNumberTextViewGrade);
        _subjectID = findViewById(R.id.subjectIDTextViewGrade);
        recyclerView = findViewById(R.id.taskGradeRecyclerView);

    }

    // INITIALIZE ADAPTER FOR RECYCLERVIEW
    public void initializeAdapter(){
        taskGradeAdapter = new TaskGradeAdapter(taskGradeItems, TaskGrade.this, this);
        recyclerView.setAdapter(taskGradeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(TaskGrade.this));
    }

    // DATA TO BE DISPLAY IN RECYCLERVIEW
    public void display(){
        studentID = new ArrayList<>();
        subjectID = new ArrayList<>();
        taskGradeItems = new ArrayList<>();
        dataBaseHelper = new DataBaseHelper(TaskGrade.this);
        taskGradeItems = displayData();
    }

    // GET DATA FROM DATABASE DEPEND ON THE PARENT'S ID
    private ArrayList<TaskGradeItems> displayData(){
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * FROM " + DataBaseHelper.TABLE_NAME2 + " WHERE " + DataBaseHelper.COLUMN_PARENT_ID + " = " + _subjectID.getText().toString(), null);
        ArrayList<TaskGradeItems> taskGradeItems = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                taskGradeItems.add(new TaskGradeItems(
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(0),
                        cursor.getInt(1)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return taskGradeItems;
    }

    private void backToTask(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void getDataFromTaskRecView(){
        Intent intent = getIntent();
        TaskItems taskItems = intent.getParcelableExtra("Task");
        String subjectID = intent.getStringExtra("id");


        String progress =taskItems.getProgress();
        String deadline = taskItems.getDueDate();
        String score = taskItems.getScore();
        String description = taskItems.getTaskDescription();
        String taskType = taskItems.getTaskType();
        int taskNumber = taskItems.getTaskNumber();

        _progress.setText(progress);
        _deadline.setText(deadline);
        _score.setText(score);
        _description.setText(description);
        _taskType.setText(taskType);
        _taskNumber.setText(String.valueOf(taskNumber));
        _subjectID.setText(subjectID);


    }

    @Override
    public void OnNoteClick(int position) {

    }
}