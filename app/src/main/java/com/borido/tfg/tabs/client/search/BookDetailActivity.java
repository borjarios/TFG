package com.borido.tfg.tabs.client.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borido.tfg.R;
import com.borido.tfg.models.Book;
import com.borido.tfg.models.Library;
import com.borido.tfg.models.User;
import com.borido.tfg.services.external.RealtimeDatabase;
import com.borido.tfg.tabs.client.TabsClient;
import com.borido.tfg.tabs.client.libraries.LibraryDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView imgBook;
    private TextView ISBN, title, gender, editorial, idiom, author, description, pages;
    private Button info;
    private ImageButton fav;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    private Book book;

    private boolean control;

    ArrayList<String> reserves = new ArrayList<String>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");
    private DatabaseReference librariesRef = database.getReference("libraries");

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();

    private RealtimeDatabase realtimeDatabase = new RealtimeDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        /*------------------------------------------ INIT --------------------------------------------------*/

        linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
        imgBook = (ImageView) findViewById(R.id.ImageViewBook);
        ISBN = (TextView) findViewById(R.id.TextViewDetailISBN);
        title = (TextView) findViewById(R.id.TextViewTituloDetail);
        gender = (TextView) findViewById(R.id.TextViewDetailGender);
        editorial = (TextView) findViewById(R.id.TextViewDetailEditorial);
        idiom = (TextView) findViewById(R.id.TextViewDetaildiom);
        author = (TextView) findViewById(R.id.TextViewDetailAuthor);
        description = (TextView) findViewById(R.id.TextViewDescriptionDetail);
        pages = (TextView) findViewById(R.id.TextViewDetailPages);
        info = (Button) findViewById(R.id.ButtonMoreInfo);
        fav = (ImageButton) findViewById(R.id.ImageButtonFav);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

        controlData();

        if(userAuth != null){
            fav.setVisibility(View.VISIBLE);
        }else{
            fav.setVisibility(View.INVISIBLE);
        }

        /*------------------------------------------ TOOLBAR --------------------------------------------------*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*------------------------------------------ OTHER METHODS --------------------------------------------------*/

    public void moreInfo(View view) {
        ValueEventListener libraryListener = new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Library library = data.getValue(Library.class);
                    if(book.getLibraries().equals(library.getId())){
                        Intent intent = new Intent(getApplicationContext(), LibraryDetailActivity.class);
                        intent.putExtra("library", library);
                        startActivity(intent);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("info", "onCancelled");
            }
        };
        librariesRef.addListenerForSingleValueEvent(libraryListener);

    }

    public void fav(View view) {
        ValueEventListener userListener = new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if(userAuth.getEmail().equals(user.getEmail())){
                        if(user.getReserves() == null){
                            fav.setImageResource(R.drawable.ic_favorite);
                            reserves.add(book.getId());
                            realtimeDatabase.updateReserves(reserves);
                        }else {
                            reserves = user.getReserves();
                            for (String reserve : reserves) {
                                if (reserve.equals(book.getId())) {
                                    control = true;
                                    break;
                                }
                                if (!reserve.equals(book.getId())) {
                                    control = false;
                                }
                            }
                            updateReserves();
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
    }

    private void updateReserves() {
        if(control == true){
            fav.setImageResource(R.drawable.ic_fav_2);
            reserves.remove(book.getId());
        }
        if(control == false){
            fav.setImageResource(R.drawable.ic_favorite);
            reserves.add(book.getId());
        }
        realtimeDatabase.updateReserves(reserves);
    }

    private void controlData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("book")) {
                book = (Book) extras.getSerializable("book");
            } else {
                info.setVisibility(View.GONE);
                book = (Book) extras.getSerializable("book2");
            }
        }

        if(userAuth != null){
            ValueEventListener userListener = new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        if(userAuth.getEmail().equals(user.getEmail())){
                            if(user.getReserves() == null){
                                fav.setImageResource(R.drawable.ic_fav_2);
                            }else {
                                for(String reserve: user.getReserves()){
                                    if (reserve.equals(book.getId())) {
                                        fav.setImageResource(R.drawable.ic_favorite);
                                    }
                                }
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
            fav.setVisibility(View.INVISIBLE);
        }

        ISBN.setText(book.getIsbn());
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        description.setText(book.getDescripcion());
        editorial.setText(book.getEditorial());
        gender.setText(book.getGender());
        idiom.setText(book.getIdiom());
        pages.setText("" + book.getN_pages());

        Picasso.get().load(book.getImage()).into(imgBook, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void close() {
        finish();
    }


}