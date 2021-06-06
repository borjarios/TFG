package com.borido.tfg.tabs.admin.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.borido.tfg.R;
import com.borido.tfg.models.Book;
import com.borido.tfg.models.Library;
import com.borido.tfg.services.external.Storage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ControlBookActivity extends AppCompatActivity {

    private ImageView imgBook;
    private EditText ISBN, title, gender, editorial, idiom, author, description, pages;
    private Spinner library;
    private Button edit, remove;
    private ProgressBar progressBar;

    private Book book;
    private Book updateBook = new Book();

    private boolean control = false;

    private Storage storage = new Storage(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_book);

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
        edit = (Button) findViewById(R.id.ButtonEdit);
        remove = (Button) findViewById(R.id.ButtonRemove);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

        library.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Library library = (Library) parent.getSelectedItem();
                updateBook.setLibraries(library.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("info", "onNothingSelected");
                Library library = (Library) parent.getSelectedItem();
                updateBook.setLibraries(library.getId());
            }
        });

        loadLibraries();
        controlData();

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

    public void removeBook(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(this.getDrawable(R.drawable.ic_warning));
        builder.setMessage(R.string.message_alert_dialog_remove_books).setTitle(R.string.title_alert_dialog_remove_books);
        builder.setPositiveButton(R.string.positive_button_alert_dialog_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                storage.delImgBook(book);
                close();
            }
        });
        builder.setNegativeButton(R.string.negative_button_alert_dialog_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor("#FF000E"));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#FF000E"));
    }

    public void editBook(View view) {
        if(!ISBN.getText().toString().trim().equals("") && !title.getText().toString().trim().equals("") && !gender.getText().toString().trim().equals("") && !editorial.getText().toString().trim().equals("") && !idiom.getText().toString().trim().equals("") && !author.getText().toString().trim().equals("") && !description.getText().toString().trim().equals("") && !pages.getText().toString().trim().equals("") && updateBook.getImage() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(this.getDrawable(R.drawable.ic_alert));
            builder.setMessage(R.string.message_alert_dialog_edit_books).setTitle(R.string.title_alert_dialog_edit_books);
            builder.setPositiveButton(R.string.positive_button_alert_dialog_logout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateBook.setId(book.getId());
                    updateBook.setIsbn(ISBN.getText().toString().trim());
                    updateBook.setTitle(title.getText().toString().trim());
                    updateBook.setGender(gender.getText().toString().trim());
                    updateBook.setEditorial(editorial.getText().toString().trim());
                    updateBook.setIdiom(idiom.getText().toString().trim());
                    updateBook.setAuthor(author.getText().toString().trim());
                    updateBook.setDescripcion(description.getText().toString().trim());
                    updateBook.setN_pages(Integer.parseInt(pages.getText().toString().trim()));

                    storage.updateImgBook(updateBook, control);
                    close();
                }
            });
            builder.setNegativeButton(R.string.negative_button_alert_dialog_logout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(Color.parseColor("#FFDB00"));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(Color.parseColor("#FFDB00"));
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

        if(updateBook.getImage() == null){
            Toast.makeText(getApplicationContext(), R.string.toast_error_photo, Toast.LENGTH_LONG).show();
        }
    }

    /*------------------------------------------ OTHER METHODS --------------------------------------------------*/

    private void controlData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            book = (Book) extras.getSerializable("book");
            updateBook.setImage(book.getImage());
        }

        ISBN.setText(book.getIsbn());
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        description.setText(book.getDescripcion());
        editorial.setText(book.getEditorial());
        gender.setText(book.getGender());
        idiom.setText(book.getIdiom());
        pages.setText(""+book.getN_pages());

        if (!book.getImage().equals("null")) {
            Picasso.get().load(book.getImage()).into(imgBook, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    imgBook.setVisibility(View.VISIBLE);
                    ISBN.setVisibility(View.VISIBLE);
                    title.setVisibility(View.VISIBLE);
                    author.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    editorial.setVisibility(View.VISIBLE);
                    gender.setVisibility(View.VISIBLE);
                    idiom.setVisibility(View.VISIBLE);
                    pages.setVisibility(View.VISIBLE);
                    library.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                    remove.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onError(Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            String imageUri = String.valueOf(data.getData());
            imgBook.setImageURI(Uri.parse(imageUri));
            updateBook.setImage(imageUri);
            control = true;
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
            int position = -1;
            int value;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    position++;

                    Library library2 = data.getValue(Library.class);
                    libraries.add(library2);

                    if(book.getLibraries().equals(library2.getId())){
                        value = position;
                    }
                }
                ArrayAdapter<Library> adapter = new ArrayAdapter<Library>(getApplicationContext(), android.R.layout.simple_spinner_item, libraries);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                library.setAdapter(adapter);
                library.setSelection(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("info", "onCancelled");
            }
        };
        librariesRef.addListenerForSingleValueEvent(librariesListener);
    }
}