package com.borido.tfg.tabs.client.libraries;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borido.tfg.R;
import com.borido.tfg.models.Book;
import com.borido.tfg.models.Library;
import com.borido.tfg.tabs.admin.books.AddBookActivity;
import com.borido.tfg.tabs.admin.books.BooksFragment;
import com.borido.tfg.tabs.admin.books.ControlBookActivity;
import com.borido.tfg.tabs.client.search.BookDetailActivity;
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

public class ListBookActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference booksRef = database.getReference("books");

    private RecyclerView recyclerView;
    private EditText searchBar;
    private ProgressBar progressBar;

    private AdapterBooks adapterBooks;

    private ArrayList<Book> books = new ArrayList<Book>();

    /*------------------------------------------ UPDATE DATA --------------------------------------------------*/

    @Override
    public void onResume() {
        super.onResume();

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        adapterBooks = new AdapterBooks(new ArrayList<Book>(), getApplicationContext(), this);
        recyclerView.setAdapter(adapterBooks);

        controlData();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_book);

        /*------------------------------------------ INIT --------------------------------------------------*/

        searchBar = (EditText) findViewById(R.id.SearchBar);
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i("info", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                adapterBooks = new AdapterBooks(new ArrayList<Book>(), getApplicationContext(), getParent());
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

        /*------------------------------------------ TOOLBAR --------------------------------------------------*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*------------------------------------------ OTHER METHODS --------------------------------------------------*/

    private void controlData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            books = (ArrayList<Book>) extras.getSerializable("books");
        }

        for(Book book: books){
            adapterBooks.addItem(book);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /*------------------------------------------ ADAPTER --------------------------------------------------*/

    class AdapterBooks extends RecyclerView.Adapter<AdapterBooks.MyViewHolder> {

        private List<Book> books;
        private Context context;
        private Activity activity;

        public AdapterBooks(List<Book> books, Context context, Activity activity) {
            this.books = books;
            this.context = context;
            this.activity = activity;
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
            if (!books.get(position).getImage().equals("null")) {
                Picasso.get().load(books.get(position).getImage()).into(holder.image);
            }
            if (books.get(position).getImage().equals("null")) {
                holder.image.setImageResource(R.drawable.library128px);
            }
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchBar.setText("");
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra("book2", books.get(position));
                    ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, holder.image, "Book_Foto");
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