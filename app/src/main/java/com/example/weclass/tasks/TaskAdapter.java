package com.example.weclass.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weclass.R;
import com.example.weclass.database.DataBaseHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> implements Filterable {

    private final ArrayList<TaskItems> taskItems;
    private final Context context;
    private final OnNoteListener mOnNoteListener;
    private final ArrayList<TaskItems> taskItemsFull;
    private final UpdateRecView updateRecView;
    private final String notArchive;
    private int gradedStudent;
    private int totalStudent;
    private static String tag = "TaskAdapter";

    public TaskAdapter(Context context, ArrayList<TaskItems> taskItems,OnNoteListener onNoteListener,
                       UpdateRecView updateRecView, String notArchive) {
        this.context = context;
        this.taskItems = taskItems;
        this.mOnNoteListener = onNoteListener;
        taskItemsFull = new ArrayList<>(taskItems);
        this.updateRecView = updateRecView;
        this.notArchive = notArchive;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView taskNumber, _parentID, _taskType, _score, _description,_progress,
                _gradingPeriod, _due, _taskId;
        ImageButton _optionTask, _expand;
        OnNoteListener onNoteListener;
        ConstraintLayout constraintLayout;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            taskNumber = itemView.findViewById(R.id.taskNumberRecView);
            _taskType = itemView.findViewById(R.id.courseTypeRecView);
            _score = itemView.findViewById(R.id.scoreTextViewRecView);
            _description = itemView.findViewById(R.id.descriptionHiddenTextView);
            _optionTask = itemView.findViewById(R.id.optionButtonTaskRecView);
            _expand = itemView.findViewById(R.id.expandRecyclerView);
            _parentID = itemView.findViewById(R.id.parentIDTaskRecView);
            _progress = itemView.findViewById(R.id.progressTextView);
            constraintLayout = itemView.findViewById(R.id.hiddenDescription);
            cardView = itemView.findViewById(R.id.cardViewRecView);
            _gradingPeriod = itemView.findViewById(R.id.gradingPeriodTaskRecView);
            _due = itemView.findViewById(R.id.dueTextViewTaskRecView);
            _taskId = itemView.findViewById(R.id.idNumberTask);


            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_recyclerview_style, parent, false);
        return new MyViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TaskItems itemsTask = taskItems.get(position);


        holder._parentID.setText(String.valueOf(taskItems.get(position).getParentID()));
        holder._taskType.setText(String.valueOf(taskItems.get(position).getTaskType()));
        holder._score.setText(String.valueOf(taskItems.get(position).getScore()));
        holder._progress.setText(String.valueOf(taskItems.get(position).getProgress()));
        holder.taskNumber.setText(String.valueOf(taskItems.get(position).getTaskNumber()));
        holder._gradingPeriod.setText(String.valueOf(taskItems.get(position).getGradingPeriod()));
        holder._due.setText(String.valueOf(taskItems.get(position).getDue()));
        holder._taskId.setText(String.valueOf(taskItems.get(position).getTaskID()));

        // hide option button in archive
        hideOptionButton(holder);

        // count graded students and total students
        getGradedStudents(holder);
        getSumOfStudents(holder);
        getTotalAndGradedStudents(holder);

        Log.d(tag,"" + gradedStudent + " / " + totalStudent);

        String b = String.valueOf(taskItems.get(position).getTaskDescription());
        String a = "                       ";
        String c = a + b;
        holder._description.setText(c);

        if(holder._progress.getText().toString().equals("Completed")){
            holder._progress.setTextColor(holder._progress.getContext().getResources().getColor(R.color.lightText));
            holder._progress.setBackgroundResource(R.drawable.rounded_stroke);
        }else{
            holder._progress.setTextColor(holder._progress.getContext().getResources().getColor(R.color.progressColorToDo));
            holder._progress.setBackgroundResource(R.drawable.rounded_stroke_red);
        }
        // EXPAND CARD VIEW WHEN CLICKED
        holder._expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //cardView.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                if(holder.constraintLayout.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                    holder.constraintLayout.setVisibility(View.VISIBLE);
                    holder._expand.setImageResource(R.drawable.ic_up);
                }
                else {
                    holder.constraintLayout.setVisibility(View.GONE);
                    holder._expand.setImageResource(R.drawable.ic_arrow_down1);
                    notifyDataSetChanged();
                }

            }
        });

        // POPUP OPTION MENU WHEN CLICKED THE 3 DOT BUTTON
        holder._optionTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, holder._optionTask);
                popupMenu.inflate(R.menu.option_subject_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edit_subject:
                                Intent intent = new Intent(context, EditTask.class);
                                Bundle bundle = new Bundle();

                                bundle.putString("task_id", String.valueOf(itemsTask.getTaskID()));
                                bundle.putString("task_number", String.valueOf(itemsTask.getTaskNumber()));
                                bundle.putString("task_type", String.valueOf(itemsTask.getTaskType()));
                                bundle.putString("task_progress", String.valueOf(itemsTask.getProgress()));
                                bundle.putString("task_score", String.valueOf(itemsTask.getScore()));
                                bundle.putString("task_description", String.valueOf(itemsTask.getTaskDescription()));
                                bundle.putString("period", String.valueOf(itemsTask.getGradingPeriod()));
                                bundle.putString("due", String.valueOf(itemsTask.getDue()));

                                intent.putExtra("Task", bundle);
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
                                        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

                                        db.deleteTask(itemsTask.getTaskID());


                                        db.deleteAll(itemsTask.getTaskID());

                                        int a = holder.getAdapterPosition();
                                        taskItems.remove(a);
                                        notifyItemRemoved(a);

                                        updateRecView.updateTaskRecView();

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
        return taskItems.size();
    }
    public interface OnNoteListener{
        void onNoteClick(int position);
    }

    @Override
    public Filter getFilter() {
        return taskFilter;
    }

    private final Filter taskFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<TaskItems> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(taskItemsFull);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (TaskItems taskItems: taskItemsFull){
                    String a = taskItems.getTaskType().toLowerCase() + " " + taskItems.getTaskNumber();
                    String b = taskItems.getTaskType().toLowerCase() + "" + taskItems.getTaskNumber();
                    if (taskItems.getTaskType().toLowerCase().contains(filterPattern) ||
                            taskItems.getTaskDescription().toLowerCase().contains(filterPattern) ||
                            taskItems.getGradingPeriod().toLowerCase().contains(filterPattern) ||
                            String.valueOf(taskItems.getTaskNumber()).contains(filterPattern) ||
                            a.contains(filterPattern) ||
                            b.contains(filterPattern) ||
                            taskItems.getProgress().contains(filterPattern)){
                        filteredList.add(taskItems);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            taskItems.clear();
            taskItems.addAll((List)filterResults.values);

            if(taskItems.size()==0){
                Toast.makeText(context, "Task doesn't exist" , Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        }
    };

    public interface UpdateRecView{
        void updateTaskRecView();
    }

    // hide option button in archive
    public void hideOptionButton(MyViewHolder holder){
        if (notArchive.equals("Archive")){
            if (holder._optionTask.getVisibility() == View.VISIBLE){
                holder._optionTask.setVisibility(View.GONE);
            }
        }
    }

    public void getGradedStudents(MyViewHolder holder){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase sql = dataBaseHelper.getWritableDatabase();
        Cursor cursor = sql.rawQuery(" SELECT COUNT(*) FROM "
                + DataBaseHelper.TABLE_MY_GRADE + " WHERE "
                + DataBaseHelper.COLUMN_PARENT_ID_MY_GRADE + " = "
                + holder._parentID.getText().toString() + " AND "
                + DataBaseHelper.COLUMN_TASK_TYPE_MY_GRADE + " ='"
                + holder._taskType.getText().toString() + "' AND "
                + DataBaseHelper.COLUMN_TASK_NUMBER_MY_GRADE + " = "
                + holder.taskNumber.getText().toString() + " AND "
                + DataBaseHelper.COLUMN_GRADE_MY_GRADE + " != '" + "" + "' AND "
                + DataBaseHelper.COLUMN_GRADING_PERIOD_MY_GRADE + "='"
                + holder._gradingPeriod.getText().toString() + "'", null);

        if (cursor.moveToFirst() ){
            gradedStudent = cursor.getInt(0);
            cursor.close();
        }
    }

    public void getSumOfStudents(MyViewHolder holder){
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(" SELECT COUNT(*) FROM "
                + DataBaseHelper.TABLE_MY_STUDENTS + " WHERE "
                + DataBaseHelper.COLUMN_PARENT_ID + " = "
                + holder._parentID.getText().toString(), null);

        if (cursor.moveToFirst() ){
            totalStudent = cursor.getInt(0);
            cursor.close();
        }
    }

    public void getTotalAndGradedStudents(MyViewHolder holder){

        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context);

        if(gradedStudent == totalStudent){
            String completed = "Completed";
            holder._progress.setText(completed);

            dataBaseHelper.updateTaskProgress(holder._taskId.getText().toString(),
                    holder._parentID.getText().toString(),
                    completed);

        }else  {
            String inProgress = "In-progress";
            holder._progress.setText(inProgress);

            dataBaseHelper.updateTaskProgress(holder._taskId.getText().toString(),
                    holder._parentID.getText().toString(),
                    inProgress);
        }
    }
}
