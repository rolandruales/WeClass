package com.example.weclass.attendance;

import com.example.weclass.studentlist.StudentItems;

import java.util.Comparator;

public class AttendanceItems {

    String lastName, firstName;
    int id, parentID, present, absent;
    byte[] image;



    public AttendanceItems(int id, int parentID, String lastName, String firstName, byte[] image, int present, int absent) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.parentID = parentID;
        this.image = image;
        this.id = id;
        this.present = present;
        this.absent = absent;

    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getParentID() {
        return parentID;
    }

    public byte[] getImage() {
        return image;
    }

    public int getId() {
        return id;
    }

    public int getPresent() {
        return present;
    }

    public int getAbsent() {
        return absent;
    }

    public static Comparator<AttendanceItems> sortAtoZComparator = new Comparator<AttendanceItems>() {
        @Override
        public int compare(AttendanceItems attendanceItems, AttendanceItems t1) {
            return attendanceItems.getLastName().compareTo(t1.getLastName());
        }
    };

    public static Comparator<AttendanceItems> sortZtoAComparator = new Comparator<AttendanceItems>() {
        @Override
        public int compare(AttendanceItems attendanceItems, AttendanceItems t1) {
            return t1.getLastName().compareTo(attendanceItems.getLastName());
        }
    };
}
