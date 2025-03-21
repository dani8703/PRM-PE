package com.example.sp25_trandangquocdat_njs1706.adapter;

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
import com.example.sp25_trandangquocdat_njs1706.R;
import com.example.sp25_trandangquocdat_njs1706.constant.StringConst;
import com.example.sp25_trandangquocdat_njs1706.db.AppDatabase;
import com.example.sp25_trandangquocdat_njs1706.excutors.AppExecutors;
import com.example.sp25_trandangquocdat_njs1706.model.Major;
import java.util.List;

public class MajorAdapter extends RecyclerView.Adapter<MajorAdapter.ViewHolder> {
    private List<Major> majors;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MajorAdapter(List<Major> majors, Context context) {
        this.majors = majors;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.child_item_layout, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Major major = majors.get(position);
        holder.txtName.setText(major.getNameMajor()); // Hiển thị nameMajor

        holder.imgDelete.setOnClickListener(v -> {
            int positionToDelete = holder.getAdapterPosition();
            Major majorToDelete = majors.get(positionToDelete);
            showDeleteConfirmationDialog(v, majorToDelete, positionToDelete);
        });
    }

    private void delete(Major major, int position) {
        AppDatabase appDatabase = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.majorDAO().delete(major);
        });
        majors.remove(position);
        notifyItemRemoved(position);
    }

    private void showDeleteConfirmationDialog(View view, Major major, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Do you really want to delete this major?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            delete(major, position);
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return majors.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private ImageView imgDelete;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtMajorName);
            imgDelete = itemView.findViewById(R.id.imgDeleteMajor); 

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position);
                }
            });
        }
    }
}
