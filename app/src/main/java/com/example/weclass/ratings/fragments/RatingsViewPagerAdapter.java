package com.example.weclass.ratings.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.weclass.studentlist.profile.attendance.attendanceviewpager.AbsentFragment;
import com.example.weclass.studentlist.profile.attendance.attendanceviewpager.LateFragment;
import com.example.weclass.studentlist.profile.attendance.attendanceviewpager.PresentFragment;

public class RatingsViewPagerAdapter extends FragmentStateAdapter {

    private final String parentId, classType, gradingPeriod;

    public RatingsViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,
                                   String parentId, String classType, String gradingPeriod) {
        super(fragmentManager, lifecycle);

        this.parentId = parentId;
        this.classType = classType;
        this.gradingPeriod = gradingPeriod;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0){
            Bundle bundle = new Bundle();
            bundle.putString("parentId", parentId);
            bundle.putString("classType", classType);
            bundle.putString("gradingPeriod", gradingPeriod);
            Midterm midterm= new Midterm();
            midterm.setArguments(bundle);

            return midterm;
        }else if (position == 1){
            Bundle bundle = new Bundle();
            bundle.putString("parentId", parentId);
            bundle.putString("classType", classType);
            bundle.putString("gradingPeriod", gradingPeriod);
            Finals finals= new Finals();
            finals.setArguments(bundle);

            return finals;
        }else {
            Bundle bundle = new Bundle();
            bundle.putString("parentId", parentId);
            bundle.putString("classType", classType);
            bundle.putString("gradingPeriod", gradingPeriod);
            FinalRating finalRating= new FinalRating();
            finalRating.setArguments(bundle);

            return finalRating;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
