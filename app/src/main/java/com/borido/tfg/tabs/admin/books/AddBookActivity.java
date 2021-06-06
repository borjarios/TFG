package com.borido.tfg.tabs.admin.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.borido.tfg.R;
import com.borido.tfg.models.Book;
import com.borido.tfg.models.Library;
import com.borido.tfg.services.external.RealtimeDatabase;
import com.borido.tfg.services.external.Storage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddBookActivity extends AppCompatActivity {

    private ImageView imgBook;
    private EditText ISBN, title, gender, editorial, idiom, author, description, pages;
    private Spinner library;
    private Button add;

    private Book addBook = new Book();

    private Storage storage = new Storage(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        /*------------------------------------------ INIT --------------------------------------------------*/

        imgBook = (ImageView) findViewById(R.id.ImageViewBook);
        ISBN = (EditText) findViewById(R.id.EditTextISBN);
        title = (EditText) findViewById(R.id.EditTextTitle);
        gender = (EditText) findViewById(R.id.EditTextGender);
        editorial = (EditText) findViewById(R.id.EditTextEditorial);
        idiom = (EditText) findViewById(R.id.EditTextIdiom);
        author = (EditText) findViewById(R.id.EditTextAuthor);
        description = (EditText) findViewById(R.id.EditTextDescription);
        pages = (EditText) findViewById(R.id.EditTextPages);
        library = (Spinner) findViewById(R.id.SpinnerLibrary);
        add = (Button) findViewById(R.id.ButtonAdd);

        library.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Library library = (Library) parent.getSelectedItem();
                addBook.setLibraries(library.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadLibraries();

        /*------------------------------------------ TOOLBAR --------------------------------------------------*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflaterMenu = getMenuInflater();
        inflaterMenu.inflate(R.menu.photo_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                close();
                return true;
            case R.id.addPhoto:
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 100);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*------------------------------------------ FORM --------------------------------------------------*/

    public void addBook(View view) {
        if(!ISBN.getText().toString().trim().equals("") && !title.getText().toString().trim().equals("") && !gender.getText().toString().trim().equals("") && !editorial.getText().toString().trim().equals("") && !idiom.getText().toString().trim().equals("") && !author.getText().toString().trim().equals("") && !description.getText().toString().trim().equals("") && !pages.getText().toString().trim().equals("") && addBook.getImage() != null){
            addBook.setId(""+(int) Math.floor(Math.random()*(0-1000000)+1000000));
            addBook.setIsbn(ISBN.getText().toString().trim());
            addBook.setTitle(title.getText().toString().trim());
            addBook.setGender(gender.getText().toString().trim());
            addBook.setEditorial(editorial.getText().toString().trim());
            addBook.setIdiom(idiom.getText().toString().trim());
            addBook.setAuthor(author.getText().toString().trim());
            addBook.setDescripcion(description.getText().toString().trim());
            addBook.setN_pages(Integer.parseInt(pages.getText().toString().trim()));

            storage.setImgBook(addBook);
            close();

        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_error_fields_books, Toast.LENGTH_LONG).show();
        }

        if(ISBN.getText().toString().trim().equals("")){
            ISBN.setError(getText(R.string.toast_error_field_books));
        }

        if(title.getText().toString().trim().equals("")){
            title.setError(getText(R.string.toast_error_field_books));
        }

        if(gender.getText().toString().trim().equals("")){
            gender.setError(getText(R.string.toast_error_field_books));
        }

        if(editorial.getText().toString().trim().equals("")){
            editorial.setError(getText(R.string.toast_error_field_books));
        }

        if(idiom.getText().toString().trim().equals("")){
            idiom.setError(getText(R.string.toast_error_field_books));
        }

        if(author.getText().toString().trim().equals("")){
            author.setError(getText(R.string.toast_error_field_books));
        }

        if(description.getText().toString().trim().equals("")){
            description.setError(getText(R.string.toast_error_field_books));
        }

        if(pages.getText().toString().trim().equals("")){
            pages.setError(getText(R.string.toast_error_field_books));
        }

        if(addBook.getImage() == null){
            Toast.makeText(getApplicationContext(), R.string.toast_error_photo, Toast.LENGTH_LONG).show();
        }
    }

    /*------------------------------------------ OTHER METHODS --------------------------------------------------*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            String imageUri = String.valueOf(data.getData());
            imgBook.setImageURI(Uri.parse(imageUri));
            addBook.setImage(imageUri);
        }
    }

    private void close() {
        finish();
    }

    private void loadLibraries() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference librariesRef = database.getReference("libraries");
        List<Library> libraries = new ArrayList<Library>();

        ValueEventListener librariesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Library library = data.getValue(Library.class);
                    libraries.add(library);
                }
                ArrayAdapter<Library> adapter = new ArrayAdapter<Library>(getApplicationContext(), android.R.layout.simple_spinner_item, libraries);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                library.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("info", "onCancelled");
            }
        };
        librariesRef.addListenerForSingleValueEvent(librariesListener);
    }
}