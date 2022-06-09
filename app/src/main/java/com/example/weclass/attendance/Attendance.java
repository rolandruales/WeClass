package com.example.weclass.attendance;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weclass.ExtendedRecyclerView;
import com.example.weclass.R;
import com.example.weclass.database.DataBaseHelper;
import com.example.weclass.studentlist.StudentInformation;
import com.example.weclass.tasks.TaskAdapter;
import com.example.weclass.tasks.TaskItems;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Attendance extends Fragment implements AttendanceAdapter.OnNoteListener{

    ExtendedRecyclerView extendedRecyclerView;
    ArrayList<AttendanceItems> attendanceItems, id, parentID;
    AttendanceAdapter attendanceAdapter;
    DataBaseHelper dataBaseHelper;
    TextView _noStudentsTextView, _id, _parentID, dateTimeDisplay;
    View view;
    View _noStudentsView;
    EditText _search;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_attendance, container, false);

        initialize();
        getDataFromBottomNaviActivity();
        display();
        initializeAdapter();
        textListener();
        displayDate();

        return view;
    }

    @Override
    public void onResume() {

        initialize();
        getDataFromBottomNaviActivity();
        display();
        initializeAdapter();
        super.onResume();
    }

    public void initialize(){
        dateTimeDisplay = view.findViewById(R.id.currentDateTextView);
        extendedRecyclerView = view.findViewById(R.id.attendanceRecyclerView);
        _noStudentsView = view.findViewById(R.id.noAttendanceView);
        _noStudentsTextView = view.findViewById(R.id.noAttendanceTextView);
        _id = view.findViewById(R.id.idAttendanceRecView);
        _parentID = view.findViewById(R.id.parentIDAttendance);
        _search = view.findViewById(R.id.searchAttendanceEditText);
    }

    // INITIALIZE ADAPTER FOR RECYCLERVIEW
    public void initializeAdapter(){
        attendanceAdapter = new AttendanceAdapter(getContext(), attendanceItems, this);
        extendedRecyclerView.setAdapter(attendanceAdapter);
        extendedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        extendedRecyclerView.setEmptyView(_noStudentsView,_noStudentsTextView);
    }

    // DATA TO BE DISPLAY IN RECYCLERVIEW
    public void display(){
        id = new ArrayList<>();
        parentID = new ArrayList<>();
        attendanceItems = new ArrayList<>();
        dataBaseHelper = new DataBaseHelper(getContext());
        attendanceItems = displayData();
    }

    // GET DATA FROM DATABASE DEPEND ON THE PARENT'S ID
    private ArrayList<AttendanceItems> displayData(){
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(" SELECT * FROM " + DataBaseHelper.TABLE_NAME2 + " WHERE " + DataBaseHelper.COLUMN_PARENT_ID + " = " + _parentID.getText().toString(), null);
        ArrayList<AttendanceItems> attendanceItems = new ArrayList<>();

        if (cursor.moveToFirst()){
            do {
                attendanceItems.add(new AttendanceItems(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(5),
                        cursor.getInt(6),
                        cursor.getInt(7)));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return attendanceItems;
    }

    public void displayDate(){
        calendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("EEEE - MMM d, yyyy");
        date = dateFormat.format(calendar.getTime());
        dateTimeDisplay.setText(date);
    }

    // GET DATA FROM BOTTOM NAVI THE NEEDS to DISPLAY SPECIFIC DATA FROM EACH SUBJECT
    public void getDataFromBottomNaviActivity() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            _parentID.setText(bundle.getString("IDParent"));
//            _taskSubjectCode.setText(bundle.getString("SubjectCode"));
//            _course.setText(bundle.getString("CourseCode"));
        }
    }


    // SEARCH FUNCTION FOR LIST OF STUDENTS
    public void textListener(){
        _search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                attendanceAdapter.getFilter().filter(editable);

            }
        });
    }

    @Override
    public void onNoteClick(int position) {

    }
}