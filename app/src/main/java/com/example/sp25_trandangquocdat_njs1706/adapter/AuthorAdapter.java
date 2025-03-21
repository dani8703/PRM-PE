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
import com.example.sp25_trandangquocdat_njs1706.model.Author;
import com.example.sp25_trandangquocdat_njs1706.model.Book;

import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private List<Author> authors;
    private List<Book> books;
    private Context context;

    public AuthorAdapter(List<Author> authors, List<Book> books, Context context) {
        this.authors = authors;
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.main_item_layout, parent, false); // Sử dụng layout cho Author
        return new ViewHolder(context, view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Author author = authors.get(position);
        holder.txtTitle.setText(author.getName());
        holder.txtEmail.setText(author.getEmail());
        holder.txtPhone.setText(author.getPhone());
        holder.txtAddress.setText(author.getAddress());

        holder.imgDelete.setOnClickListener(v -> {
            int positionToDelete = holder.getAdapterPosition();
            Author authorToDelete = authors.get(positionToDelete);
            showDeleteConfirmationDialog(v, authorToDelete, positionToDelete);
        });
    }

    private void delete(Author author, int position) {
        AppDatabase appDatabase = Room
                .databaseBuilder(context.getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.authorDAO().delete(author);
        });
        authors.remove(position);
        notifyItemRemoved(position);
    }

    private void showDeleteConfirmationDialog(View view, Author author, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Do you really want to delete this author?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            delete(author, position);
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return authors.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtEmail, txtPhone, txtAddress;
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
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtPhone = itemView.findViewById(R.id.txtGender); // Reusing gender field for phone
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
