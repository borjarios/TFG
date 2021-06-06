package com.borido.tfg.tabs.admin.libraries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borido.tfg.models.Library;
import com.borido.tfg.R;
import com.borido.tfg.models.User;
import com.borido.tfg.services.external.RealtimeDatabase;
import com.borido.tfg.services.external.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ControlLibraryActivity extends AppCompatActivity {

    private ImageView imgLibrary;
    private EditText name, tel, latitude, longitude, direction, location, schedule;
    private Button edit, remove;
    private ProgressBar progressBar;

    private Library library;
    private Library updatelibrary = new Library();

    private Storage storage = new Storage(this);
    private RealtimeDatabase realtimeDatabase = new RealtimeDatabase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_library);

        /*------------------------------------------ INIT --------------------------------------------------*/

        imgLibrary = (ImageView) findViewById(R.id.ImageViewLibrary);
        name = (EditText) findViewById(R.id.EditTextName);
        tel = (EditText) findViewById(R.id.EditTextTel);
        latitude = (EditText) findViewById(R.id.EditTextLatitud);
        longitude = (EditText) findViewById(R.id.EditTextLongitud);
        direction = (EditText) findViewById(R.id.EditTextDirection);
        location = (EditText) findViewById(R.id.EditTextLocation);
        schedule = (EditText) findViewById(R.id.EditTextSchedule);
        edit = (Button) findViewById(R.id.ButtonEdit);
        remove = (Button) findViewById(R.id.ButtonRemove);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

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

    public void removeLibrary(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(this.getDrawable(R.drawable.ic_warning));
        builder.setMessage(R.string.message_alert_dialog_remove_libraries).setTitle(R.string.title_alert_dialog_remove_libraries);
        builder.setPositiveButton(R.string.positive_button_alert_dialog_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                storage.delImgLibrary(library);
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

    public void editLibrary(View view) {
        if(!name.getText().toString().trim().equals("") && !latitude.getText().toString().trim().equals("") && !longitude.getText().toString().trim().equals("") && !tel.getText().toString().trim().equals("") && !schedule.getText().toString().trim().equals("") && !direction.getText().toString().trim().equals("") && !location.getText().toString().trim().equals("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(this.getDrawable(R.drawable.ic_alert));
            builder.setMessage(R.string.message_alert_dialog_edit_libraries).setTitle(R.string.title_alert_dialog_edit_libraries);
            builder.setPositiveButton(R.string.positive_button_alert_dialog_logout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updatelibrary.setId(library.getId());
                    updatelibrary.setTel(tel.getText().toString().trim());
                    updatelibrary.setName(name.getText().toString().trim());
                    updatelibrary.setDirection(direction.getText().toString().trim());
                    updatelibrary.setLocation(location.getText().toString().trim());
                    updatelibrary.setSchedule(schedule.getText().toString().trim());
                    updatelibrary.setLatitude(latitude.getText().toString().trim());
                    updatelibrary.setLongitude(longitude.getText().toString().trim());

                    if(library.getImage() == updatelibrary.getImage()){
                        realtimeDatabase.updateLibrary(updatelibrary);
                    }else{
                        storage.updateImgLibrary(updatelibrary, library);
                    }
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

    private void controlData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            library = (Library) extras.getSerializable("library");
            updatelibrary.setImage(library.getImage());
        }

        name.setText(library.getName());
        tel.setText(library.getTel());
        latitude.setText(""+library.getLatitude());
        longitude.setText(""+library.getLongitude());
        direction.setText(library.getDirection());
        location.setText(library.getLocation());
        schedule.setText(library.getSchedule());

        if (!library.getImage().equals("null")) {
            Picasso.get().load(library.getImage()).into(imgLibrary, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    imgLibrary.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    tel.setVisibility(View.VISIBLE);
                    latitude.setVisibility(View.VISIBLE);
                    longitude.setVisibility(View.VISIBLE);
                    direction.setVisibility(View.VISIBLE);
                    location.setVisibility(View.VISIBLE);
                    schedule.setVisibility(View.VISIBLE);
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
        if (library.getImage().equals("null")) {
            imgLibrary.setImageResource(R.drawable.library128px);
            imgLibrary.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            tel.setVisibility(View.VISIBLE);
            latitude.setVisibility(View.VISIBLE);
            longitude.setVisibility(View.VISIBLE);
            direction.setVisibility(View.VISIBLE);
            location.setVisibility(View.VISIBLE);
            schedule.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            String imageUri = String.valueOf(data.getData());
            imgLibrary.setImageURI(Uri.parse(imageUri));
            updatelibrary.setImage(imageUri);
        }
    }

    private void close() {
        finish();
    }

}