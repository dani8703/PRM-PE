package com.example.studentsmanagement_njs1706_se171464.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.studentsmanagement_njs1706_se171464.R;
import com.example.studentsmanagement_njs1706_se171464.constant.StringConst;
import com.example.studentsmanagement_njs1706_se171464.db.AppDatabase;
import com.example.studentsmanagement_njs1706_se171464.excutors.AppExecutors;
import com.example.studentsmanagement_njs1706_se171464.model.Student;
import com.example.studentsmanagement_njs1706_se171464.model.Major;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private List<Student> students;
    private List<Major> majors;
    private Context context;

    public StudentAdapter(List<Student> students, List<Major> majors, Context context) {
        this.students = students;
        this.majors = majors;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.main_item_layout, parent, false); // Sử dụng layout cho Student
        return new ViewHolder(context, view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.txtTitle.setText(student.getName());
        holder.txtDate.setText(student.getDate());
        holder.txtGender.setText(student.getGender());
        holder.txtEmail.setText(student.getEmail());
        holder.txtAddress.setText(student.getAddress());

        // Thêm đoạn code để load tên Major
        AppExecutors.getInstance().diskIO().execute(() -> {
            AppDatabase db = Room.databaseBuilder(context.getApplicationContext(), 
                    AppDatabase.class, StringConst.DBNAME).build();
            Major major = db.majorDAO().getById(student.getIdMajor());
            
            AppExecutors.getInstance().mainThread().execute(() -> {
                if (major != null) {
                    holder.txtMajorName.setText(major.getNameMajor());
                } else {
                    holder.txtMajorName.setText("Unknown Major");
                }
            });
        });

        holder.imgDelete.setOnClickListener(v -> {
            int positionToDelete = holder.getAdapterPosition();
            Student studentToDelete = students.get(positionToDelete);
            showDeleteConfirmationDialog(v, studentToDelete, positionToDelete);
        });
    }

    private void delete(Student student, int position) {
        AppDatabase appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.studentDAO().delete(student);
        });
        students.remove(position);
        notifyItemRemoved(position);
    }

    private void showDeleteConfirmationDialog(View view, Student student, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Do you really want to delete this student?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            delete(student, position);
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtEmail, txtGender, txtDate, txtMajorName, txtAddress;
        private ImageView imgDelete;
        private Context context;
        private AppDatabase appDatabase;

        public ViewHolder(Context context, @NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            this.context = context;
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "pe-database").build();

            // Initialize components
            txtTitle = itemView.findViewById(R.id.txtStudentName); 
            txtGender = itemView.findViewById(R.id.txtGender);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtMajorName = itemView.findViewById(R.id.txtMajorName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtAddress = itemView.findViewById(R.id.txtAddress); 
            imgDelete = itemView.findViewById(R.id.imgDeleteStudent); 

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }
}
