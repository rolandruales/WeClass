package com.example.weclass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.weclass.archive.ArchiveItems;
import com.example.weclass.attendance.Attendance;
import com.example.weclass.login.LoginActivity;
import com.example.weclass.ratings.Ratings;
import com.example.weclass.studentlist.StudentList;
import com.example.weclass.subject.SubjectItems;
import com.example.weclass.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavi extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    BottomNavigationView bottomNavigationView;
    TextView parentID, subjectCode, courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navi);

        initialize();   // INITIALIZE ALL VIEWS
        hideActionBarInFragment();  // HIDE ACTIONBAR IN FRAGMENTS
        moveFragment();  //SWITCHING DIFFERENT FRAGMENTS
        backButton();   // BACK BUTTON
        displayData();  // GET DATA FROM SUBJECT ADAPTER (RECYCLERVIEW ITEM CLICK)
        fragmentStudentList();   // GET THE VALUES OF STRING IN displayData() method to PASS THE DATA WE GOT FROM SUBJECT ADAPTER TO STUDENT LIST FRAGMENT


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);    //enable full screen

    }

    // GET DATA FROM SUBJECT ADAPTER (RECYCLERVIEW ITEM CLICK)
    public void displayData(){
//        if (getIntent().getBundleExtra("ParentID") != null) {
//            Bundle bundle = getIntent().getBundleExtra("ParentID");
//
//
//            parentID.setText(bundle.getString("id"));
//            subjectCode.setText(bundle.getString("subject_code"));
//            courseName.setText(bundle.getString("course"));
//        }

        if(getIntent().getParcelableExtra("Subject") != null){

            Intent intent = getIntent();
            SubjectItems subjectItems = intent.getParcelableExtra("Subject");

            int idParent = subjectItems.getId();
            String codeSubject = subjectItems.getSubjectCode();
            String nameCourse = subjectItems.getCourse();

            parentID.setText(String.valueOf(idParent));
            subjectCode.setText(codeSubject);
            courseName.setText(nameCourse);

        }else {
            Intent intent = getIntent();
            ArchiveItems archiveItems = intent.getParcelableExtra("Archive");

            int idParent = archiveItems.getId_subject();
            String codeSubject = archiveItems.getSubjectCode();
            String nameCourse = archiveItems.getCourse();

            parentID.setText(String.valueOf(idParent));
            subjectCode.setText(codeSubject);
            courseName.setText(nameCourse);
        }




    }


    // FRAGMENT TRANSACTION
    public void fragmentLoader(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer,fragment)
                .commit();
    }

    // GET THE VALUES OF STRING IN displayData() method to PASS THE DATA WE GOT FROM SUBJECT ADAPTER TO STUDENT LIST FRAGMENT
    public void fragmentStudentList() {
        StudentList studentList = new StudentList();
        Bundle bundle = new Bundle();
        bundle.putString("IDParent", parentID.getText().toString());
        bundle.putString("SubjectCode", subjectCode.getText().toString());
        bundle.putString("CourseCode", courseName.getText().toString());

        studentList.setArguments(bundle);
        fragmentLoader(studentList);
    }

    // PASS THE DATA WE GOT FROM SUBJECT ADAPTER TO STUDENT RECORD FRAGMENT
    public void fragmentRecord(){
        Tasks record = new Tasks();
        Bundle bundle = new Bundle();
        bundle.putString("IDParent", parentID.getText().toString());
        bundle.putString("SubjectCode", subjectCode.getText().toString());
        bundle.putString("CourseCode", courseName.getText().toString());

        record.setArguments(bundle);
        fragmentLoader(record);
    }

    // OPEN ATTENDANCE FRAGMENT FUNCTION
    public void fragmentAttendance(){
        Attendance attendance = new Attendance();
        Bundle bundle = new Bundle();
        bundle.putString("IDParent", parentID.getText().toString());

        attendance.setArguments(bundle);
        fragmentLoader(attendance);
    }

    // OPEN ATTENDANCE FRAGMENT FUNCTION
    public void fragmentRanks(){
        Ratings ranking = new Ratings();
        Bundle bundle = new Bundle();
        bundle.putString("IDParent", parentID.getText().toString());
        bundle.putString("SubjectCode", subjectCode.getText().toString());
        bundle.putString("CourseCode", courseName.getText().toString());

        ranking.setArguments(bundle);
        fragmentLoader(ranking);
    }

    // BACK BUTTON
    public void backButton(){
        ImageButton imageButton = (ImageButton) findViewById(R.id.backButton);
         imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.transition.animation_enter,R.transition.animation_leave);
            }
        });
    }

    // HIDE ACTIONBAR IN FRAGMENTS
    public void hideActionBarInFragment() {
        ActionBar supportActionBar = ((AppCompatActivity) this).getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.hide();
    }

    // INITIALIZE ALL VIEWS
    public void initialize(){
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavigation);
        parentID = findViewById(R.id.parentIDBottomNavi);
        subjectCode = findViewById(R.id.subjectCodeBottomNavi);
        courseName = findViewById(R.id.courseNameBottomNavi);

    }

    //SWITCHING DIFFERENT FRAGMENTS
    public void moveFragment(){
        //SWITCHING DIFFERENT FRAGMENTS
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()){
                    case R.id.naviStudents:
                        fragmentStudentList();
                        break;
                    case R.id.naviReport:
                        fragmentRecord();
                        break;
                    case R.id.naviAttendance:
                        fragmentAttendance();
                        break;
                    case R.id.naviRanking:
                        fragmentRanks();
                        break;
                    case R.id.drawerLogout:

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(BottomNavi.this);
                        builder.setTitle("Confirm logout");
                        builder.setIcon(R.drawable.ic_baseline_warning_24);
                        builder.setMessage("Do you really want to logout?");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(BottomNavi.this, LoginActivity.class);
                                finish();
                                startActivity(intent);
                                overridePendingTransition(R.transition.animation_enter,R.transition.animation_leave);
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.show();

                        break;
                }
                return true;
            }
        });
    }

}