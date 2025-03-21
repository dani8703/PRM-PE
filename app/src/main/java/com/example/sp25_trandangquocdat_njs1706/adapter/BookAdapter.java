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
import com.example.sp25_trandangquocdat_njs1706.model.Book;
import com.example.sp25_trandangquocdat_njs1706.model.Author;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> books;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public BookAdapter(List<Book> books, Context context) {
        this.books = books;
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
        Book book = books.get(position);
        holder.txtTitle.setText(book.getBookTitle());
        holder.txtPublicationDate.setText(book.getPublicationDate());
        holder.txtType.setText(book.getType());

        // Load author name
        AppExecutors.getInstance().diskIO().execute(() -> {
            AppDatabase db = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, StringConst.DBNAME).build();
            Author author = db.authorDAO().getById(book.getAuthorId());

            AppExecutors.getInstance().mainThread().execute(() -> {
                if (author != null) {
                    holder.txtAuthor.setText(author.getName());
                } else {
                    holder.txtAuthor.setText("Unknown Author");
                }
            });
        });

        holder.imgDelete.setOnClickListener(v -> {
            int positionToDelete = holder.getAdapterPosition();
            Book bookToDelete = books.get(positionToDelete);
            showDeleteConfirmationDialog(v, bookToDelete, positionToDelete);
        });
    }

    private void delete(Book book, int position) {
        AppDatabase appDatabase = Room
                .databaseBuilder(context.getApplicationContext(), AppDatabase.class, StringConst.DBNAME).build();
        AppExecutors.getInstance().diskIO().execute(() -> {
            appDatabase.bookDAO().delete(book);
        });
        books.remove(position);
        notifyItemRemoved(position);
    }

    private void showDeleteConfirmationDialog(View view, Book book, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Do you really want to delete this book?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            delete(book, position);
            dialog.dismiss();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtPublicationDate, txtType, txtAuthor;
        private ImageView imgDelete;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtMajorName); // Reusing MajorName for book title
            txtPublicationDate = itemView.findViewById(R.id.txtPublicationDate);
            txtType = itemView.findViewById(R.id.txtType);
            txtAuthor = itemView.findViewById(R.id.txtAuthor);
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
