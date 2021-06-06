package com.borido.tfg.tabs.admin.libraries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.borido.tfg.models.Book;
import com.borido.tfg.models.Library;
import com.borido.tfg.R;
import com.borido.tfg.services.external.RealtimeDatabase;
import com.borido.tfg.services.external.Storage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddLibraryActivity extends AppCompatActivity {

    private ImageView imgLibrary;
    private EditText name, tel, latitude, longitude, direction, location, schedule;
    private Button add;

    private Library addLibrary = new Library();

    private Storage storage = new Storage(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_library);

        /*------------------------------------------ INIT --------------------------------------------------*/

        imgLibrary = (ImageView) findViewById(R.id.ImageViewLibrary);
        name = (EditText) findViewById(R.id.EditTextName);
        tel = (EditText) findViewById(R.id.EditTextTel);
        latitude = (EditText) findViewById(R.id.EditTextLatitud);
        longitude = (EditText) findViewById(R.id.EditTextLongitud);
        direction = (EditText) findViewById(R.id.EditTextDirection);
        location = (EditText) findViewById(R.id.EditTextLocation);
        schedule = (EditText) findViewById(R.id.EditTextSchedule);
        add = (Button) findViewById(R.id.ButtonAdd);

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

    public void addLibrary(View view) {
        if(!name.getText().toString().trim().equals("") && !latitude.getText().toString().trim().equals("") && !longitude.getText().toString().trim().equals("") && !tel.getText().toString().trim().equals("") && !schedule.getText().toString().trim().equals("") && !direction.getText().toString().trim().equals("") && !location.getText().toString().trim().equals("")){
            addLibrary.setId(""+(int) Math.floor(Math.random()*(0-1000000)+1000000));
            addLibrary.setTel(tel.getText().toString().trim());
            addLibrary.setName(name.getText().toString().trim());
            addLibrary.setDirection(direction.getText().toString().trim());
            addLibrary.setLocation(location.getText().toString().trim());
            addLibrary.setSchedule(schedule.getText().toString().trim());
            addLibrary.setLatitude(latitude.getText().toString().trim());
            addLibrary.setLongitude(longitude.getText().toString().trim());

            if(addLibrary.getImage() == null){
                addLibrary.setImage("null");
            }

            storage.setImgLibrary(addLibrary);
            close();
        }else{
            Toast.makeText(getApplicationContext(), R.string.toast_error_fields_libraries, Toast.LENGTH_LONG).show();
        }

        if(name.getText().toString().trim().equals("")){
            name.setError(getText(R.string.toast_error_field_libraries));
        }

        if(latitude.getText().toString().trim().equals("")){
            latitude.setError(getText(R.string.toast_error_field_libraries));
        }

        if(longitude.getText().toString().trim().equals("")){
            longitude.setError(getText(R.string.toast_error_field_libraries));
        }

        if(schedule.getText().toString().trim().equals("")){
            schedule.setError(getText(R.string.toast_error_field_libraries));
        }

        if(tel.getText().toString().trim().equals("")){
            tel.setError(getText(R.string.toast_error_field_libraries));
        }

        if(direction.getText().toString().trim().equals("")){
            direction.setError(getText(R.string.toast_error_field_libraries));
        }

        if(location.getText().toString().trim().equals("")){
            location.setError(getText(R.string.toast_error_field_libraries));
        }
    }

    /*------------------------------------------ OTHER METHODS --------------------------------------------------*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            String imageUri = String.valueOf(data.getData());
            imgLibrary.setImageURI(Uri.parse(imageUri));
            addLibrary.setImage(imageUri);
        }
    }

    private void close() {
        finish();
    }

}