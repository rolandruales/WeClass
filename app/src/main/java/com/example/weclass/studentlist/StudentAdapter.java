package com.example.weclass.studentlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weclass.R;
import com.example.weclass.database.DataBaseHelper;
import com.example.weclass.studentlist.profile.image.ImageUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> implements Filterable {

    private final ArrayList<StudentItems> studentItems;
    private final ArrayList<StudentItems> studentItemsFull;
    private final Context context;
    private final OnNoteListener mOnNoteListener;
    private final ItemCallback itemCallback;


    public StudentAdapter(Context context, ArrayList<StudentItems> studentItems, OnNoteListener onNoteListener, ItemCallback itemCallback){
        this.context = context;
        this.studentItems = studentItems;
        this.mOnNoteListener = onNoteListener;
        this.itemCallback = itemCallback;
        studentItemsFull = new ArrayList<>(studentItems);

    }

    @Override
    public Filter getFilter() {
        return studentFilter;
    }

    private final Filter studentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<StudentItems> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(studentItemsFull);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (StudentItems studentItems: studentItemsFull){
                    if (studentItems.getLastname().toLowerCase().contains(filterPattern) ||
                            studentItems.getFirstname().toLowerCase().contains(filterPattern) ||
                            studentItems.getMiddleName().toLowerCase().contains(filterPattern) ||
                            studentItems.getGender().toLowerCase().contains(filterPattern)
                    ){
                        filteredList.add(studentItems);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            studentItems.clear();
            studentItems.addAll((List)filterResults.values);
            notifyDataSetChanged();

        }
    };

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.studentlist_recyclerview_style, parent,false);
        return new MyViewHolder(view, mOnNoteListener, itemCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StudentItems item = studentItems.get(position);

        byte[] image = item.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0 , image.length);

        holder.id.setText(String.valueOf(studentItems.get(position).getId()));
        holder.parent_id.setText(String.valueOf(studentItems.get(position).getParent_id()));
        holder.lastNameText.setText(String.valueOf(studentItems.get(position).getLastname()));
        holder.firstNameText.setText(String.valueOf(studentItems.get(position).getFirstname()));
        holder.middleNameText.setText(String.valueOf(studentItems.get(position).getMiddleName()));
        holder.genderText.setText(String.valueOf(studentItems.get(position).getGender()));
        holder.absences.setText(String.valueOf(studentItems.get(position).getAbsent()));
        holder.studentImage.setImageBitmap(bitmap);

        int a = Integer.parseInt(holder.absences.getText().toString());
        if(a == 4){
            holder.background.setBackgroundResource(R.color.lightText);
        }else if (a == 5){
            holder.background.setBackgroundResource(R.color.Red);
        }

        // NAVIGATE TO EDIT ACTIVITY, OR DELETE A STUDENT
        holder.optionStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, holder.optionStudent);
                popupMenu.inflate(R.menu.option_subject_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit_subject:

                                Intent intent = new Intent(context, EditStudent.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("id", String.valueOf(item.getId()));
                                bundle.putString("parent_id", String.valueOf(item.getParent_id()));
                                bundle.putString("last_name", String.valueOf(item.getLastname()));
                                bundle.putString("first_name", String.valueOf(item.getFirstname()));
                                bundle.putString("middle_name", String.valueOf(item.getMiddleName()));
                                bundle.putString("gender", String.valueOf(item.getGender()));
                                intent.putExtra("Student", bundle);
                                context.startActivity(intent);


                                break;
                            case R.id.delete_subject:
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                                builder.setTitle("Delete");
                                builder.setIcon(R.drawable.ic_baseline_warning_24);
                                builder.setMessage("Are you sure do you want to delete this?");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        DataBaseHelper db = new DataBaseHelper(context);
                                        db.deleteStudent(item.getId());

                                        int a = holder.getAdapterPosition();
                                        studentItems.remove(a);
                                        notifyItemRemoved(a);
                                        itemCallback.updateTextView();

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
                        return false;
                    }
                });
                popupMenu.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return studentItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView id,parent_id, lastNameText, firstNameText, middleNameText, genderText, absences;
        ImageButton button, optionStudent;
        OnNoteListener onNoteListener;
        ImageView studentImage;
        ConstraintLayout background;

        public MyViewHolder(@NonNull View itemView, OnNoteListener mOnNoteListener, ItemCallback itemCallback) {
            super(itemView);

            id = itemView.findViewById(R.id.iDNumberStudentList);
            parent_id = itemView.findViewById(R.id.parentIDStudentList);
            lastNameText = itemView.findViewById(R.id.studentLastnameRecView);
            middleNameText = itemView.findViewById(R.id.studentMiddleRecView);
            firstNameText = itemView.findViewById(R.id.studentFirstnameRecView);
            genderText = itemView.findViewById(R.id.studentSexRecView);
            optionStudent = itemView.findViewById(R.id.optionButtonSubject);
            studentImage = itemView.findViewById(R.id.studentImageRecView);
            absences =itemView.findViewById(R.id.studentListAbsentWarning);
            background = itemView.findViewById(R.id.studentListBackgroundRecView);

            this.onNoteListener = mOnNoteListener;

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }

    // UPDATE SUM OF STUDENTS IN STUDENT LIST FRAGMENT
    public interface ItemCallback{
        void updateTextView();
    }

}



