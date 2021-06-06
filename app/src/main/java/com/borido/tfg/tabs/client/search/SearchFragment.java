package com.borido.tfg.tabs.client.search;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borido.tfg.R;
import com.borido.tfg.models.Book;
import com.borido.tfg.tabs.admin.books.AddBookActivity;
import com.borido.tfg.tabs.admin.books.BooksFragment;
import com.borido.tfg.tabs.admin.books.ControlBookActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference booksRef = database.getReference("books");

    private RecyclerView recyclerView;
    private EditText searchBar;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    private AdapterBooks adapterBooks;

    /*------------------------------------------ UPDATE DATA --------------------------------------------------*/

    @Override
    public void onResume() {
        super.onResume();

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapterBooks = new SearchFragment.AdapterBooks(new ArrayList<Book>(), getContext());
        recyclerView.setAdapter(adapterBooks);

        controlData();

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.client_tabs_fragment_search, container, false);

        /*------------------------------------------ INIT --------------------------------------------------*/

        searchBar = (EditText) root.findViewById(R.id.SearchBar);
        recyclerView = (RecyclerView) root.findViewById(R.id.RecyclerView);
        progressBar = (ProgressBar) root.findViewById(R.id.ProgressBar);
        linearLayout = (LinearLayout) root.findViewById(R.id.LinearLayout);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("info", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                adapterBooks = new AdapterBooks(new ArrayList<Book>(), getContext());
                recyclerView.setAdapter(adapterBooks);

                booksRef.orderByChild("title").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Book book = snapshot.getValue(Book.class);
                            if(book.getIsbn().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase()) || book.getTitle().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase()) || book.getAuthor().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase()) || book.getGender().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase()) || book.getEditorial().toLowerCase().contains(searchBar.getText().toString().trim().toLowerCase())){
                                adapterBooks.addItem(book);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i("info", "onCancelled");
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("info", "afterTextChanged");
            }

        });

        return root;
    }

    /*------------------------------------------ DATA --------------------------------------------------*/

    private void controlData() {
        booksRef.orderByChild("title").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                adapterBooks.addItem(book);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                adapterBooks.updateItem(book);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Book book = snapshot.getValue(Book.class);
                adapterBooks.delItem(book);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.i("info", "onChildMoved:" + snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("info", "onCancelled", error.toException());
            }
        });
    }

    /*------------------------------------------ ADAPTER --------------------------------------------------*/

    class AdapterBooks extends RecyclerView.Adapter<AdapterBooks.MyViewHolder> {

        private List<Book> books;
        private Context context;

        public AdapterBooks(List<Book> books, Context context) {
            this.books = books;
            this.context = context;
        }

        public void addItem(Book b) {
            books.add(b);
            notifyItemInserted(books.size() - 1);
            notifyDataSetChanged();
        }

        public void delItem(Book book) {
            Book book2 = null;
            for (Book b : books) {
                if (b.getIsbn() == book.getIsbn()) {
                    book2 = b;
                }
            }
            books.remove(book2);
            notifyDataSetChanged();
        }

        public void updateItem(Book book) {
            for (Book b : books) {
                b.update(book);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AdapterBooks.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_view_control_book, parent, false);
            return new AdapterBooks.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull AdapterBooks.MyViewHolder holder, int position) {
            Picasso.get().load(books.get(position).getImage()).into(holder.image, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    linearLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onError(Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBar.setText("");
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra("book", books.get(position));
                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), holder.image, "Book_Foto");
                    startActivity(intent, activityOptions.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView image;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                image = (ImageView) itemView.findViewById(R.id.imageViewBook);
            }
        }
    }
}