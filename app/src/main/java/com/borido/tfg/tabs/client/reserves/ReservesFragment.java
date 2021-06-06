package com.borido.tfg.tabs.client.reserves;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.borido.tfg.LoginActivity;
import com.borido.tfg.models.Book;
import com.borido.tfg.models.User;
import com.borido.tfg.R;
import com.borido.tfg.tabs.client.search.BookDetailActivity;
import com.borido.tfg.tabs.client.search.SearchFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReservesFragment extends Fragment {

    private LinearLayout loginTrue2, loginTrue, loginFalse;
    private TextView control;
    private RecyclerView recyclerView;
    private EditText searchBar;
    private ProgressBar progressBar;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");
    private DatabaseReference booksRef = database.getReference("books");

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();

    private AdapterBooks adapterBooks;

    private boolean loged;
    private ArrayList<String> reserves = new ArrayList<String>();

    /*------------------------------------------ UPDATE DATA --------------------------------------------------*/

    @Override
    public void onResume() {
        super.onResume();
        if(userAuth != null){
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if(userAuth.getEmail().equals(user.getEmail())){
                            loged = true;
                            control();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("info", "onCancelled");
                }
            };
            usersRef.addListenerForSingleValueEvent(userListener);
        } else {
            loged = false;
            control();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_tabs_fragment_reserves, container, false);

        /*------------------------------------------ INIT --------------------------------------------------*/

        loginTrue = (LinearLayout) view.findViewById(R.id.LoginTrue);
        loginTrue2 = (LinearLayout) view.findViewById(R.id.LoginTrue2);
        loginFalse = (LinearLayout) view.findViewById(R.id.LoginFalse);
        control = (TextView) view.findViewById(R.id.TextViewControl);
        searchBar = (EditText) view.findViewById(R.id.SearchBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

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

        return view;
    }

    /*------------------------------------------ OTHER METHODS --------------------------------------------------*/

    private void control() {
        if(loged == true){
            loginTrue.setVisibility(View.VISIBLE);
            loginFalse.setVisibility(View.GONE);

            ValueEventListener userListener = new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        if(userAuth.getEmail().equals(user.getEmail())){
                            if(user.getReserves() == null){
                                loginTrue.setVisibility(View.INVISIBLE);
                                loginTrue2.setVisibility(View.VISIBLE);
                                loginFalse.setVisibility(View.INVISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }else {
                                reserves = user.getReserves();

                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                                adapterBooks = new AdapterBooks(new ArrayList<Book>(), getContext());
                                recyclerView.setAdapter(adapterBooks);
                                controlData();
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("info", "onCancelled");
                }
            };
            usersRef.addListenerForSingleValueEvent(userListener);
        }else{
            loginTrue.setVisibility(View.GONE);
            loginFalse.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /*------------------------------------------ DATA --------------------------------------------------*/

    private void controlData() {
        booksRef.orderByChild("title").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                for(String reserve: reserves){
                    if(reserve.equals(book.getId())){
                        adapterBooks.addItem(book);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                for(String reserve: reserves){
                    if(reserve.equals(book.getId())){
                        adapterBooks.updateItem(book);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Book book = snapshot.getValue(Book.class);
                for(String reserve: reserves){
                    if(reserve.equals(book.getId())){
                        adapterBooks.delItem(book);
                    }
                }
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
                    loginTrue.setVisibility(View.VISIBLE);
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