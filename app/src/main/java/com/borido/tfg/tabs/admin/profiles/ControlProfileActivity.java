package com.borido.tfg.tabs.admin.profiles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.borido.tfg.R;
import com.borido.tfg.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ControlProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText username, email;
    private CheckBox admin;
    private ProgressBar progressBar;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference usersImagesRef = storageRef.child("users/");

    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_profile);

        /*------------------------------------------ INIT --------------------------------------------------*/

        imgProfile = (ImageView) findViewById(R.id.ImageViewProfile);
        username = (EditText) findViewById(R.id.EditTextUsername);
        email = (EditText) findViewById(R.id.EditTextEmail);
        admin = (CheckBox) findViewById(R.id.CheckBoxAdmin);
        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

        controlData();

        /*------------------------------------------ TOOLBAR --------------------------------------------------*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*------------------------------------------ OTHER METHODS --------------------------------------------------*/

    private void controlData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (User) extras.getSerializable("user");
        }

        username.setText(user.getUsername());
        email.setText(user.getEmail());
        if (user.isAdmin() == true) {
            admin.setChecked(true);
        } else {
            admin.setChecked(false);
        }

        if (!user.getImage().equals("null")) {
            Picasso.get().load(user.getImage()).into(imgProfile, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    imgProfile.setVisibility(View.VISIBLE);
                    username.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    admin.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onError(Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (user.getImage().equals("null")) {
            imgProfile.setImageResource(R.drawable.user128px);
            imgProfile.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            admin.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void close() {
        finish();
    }
}