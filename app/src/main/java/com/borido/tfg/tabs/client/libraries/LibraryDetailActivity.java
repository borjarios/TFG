package com.borido.tfg.tabs.client.libraries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.borido.tfg.models.Book;
import com.borido.tfg.models.Library;
import com.borido.tfg.R;
import com.borido.tfg.tabs.client.search.BookDetailActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LibraryDetailActivity extends AppCompatActivity {

    private EditText name, tel, directionLocation, schedule;
    private ImageView imgLibrary;
    private ProgressBar progressBar;
    private Button view;

    private Library library;
    private ArrayList<Book> books = new ArrayList<Book>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference booksRef = database.getReference("books");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_detail);

        /*------------------------------------------ INIT --------------------------------------------------*/

        imgLibrary = (ImageView) findViewById(R.id.ImageViewLibrary);
        name = (EditText) findViewById(R.id.EditTextName);
        tel = (EditText) findViewById(R.id.EditTextTel);
        directionLocation = (EditText) findViewById(R.id.EditTextDirectionLocation);
        schedule = (EditText) findViewById(R.id.EditTextSchedule);
        view = (Button) findViewById(R.id.ButtonView);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

        controlData();

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

    public void view(View view) {

        booksRef.orderByChild("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    if(book.getLibraries().equals(library.getId())){
                        books.add(book);
                    }
                }
                Intent intent = new Intent(getApplicationContext(), ListBookActivity.class);
                intent.putExtra("books", books);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("info", "onCancelled");
            }
        });

    }

    private void controlData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            library = (Library) extras.getSerializable("library");
        }

        name.setText(library.getName());
        tel.setText(library.getTel());
        directionLocation.setText(library.getDirection()+" "+library.getLocation());
        schedule.setText(library.getSchedule());

        if (!library.getImage().equals("null")) {
            Picasso.get().load(library.getImage()).into(imgLibrary, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    imgLibrary.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    tel.setVisibility(View.VISIBLE);
                    directionLocation.setVisibility(View.VISIBLE);
                    schedule.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onError(Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (library.getImage().equals("null")) {
            imgLibrary.setImageResource(R.drawable.library128px);
            imgLibrary.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            tel.setVisibility(View.VISIBLE);
            directionLocation.setVisibility(View.VISIBLE);
            schedule.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}