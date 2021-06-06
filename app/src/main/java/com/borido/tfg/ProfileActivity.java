package com.borido.tfg;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.borido.tfg.models.User;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private EditText username, email, password;
    private ImageView photo;
    private ProgressBar progressBar;

    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*------------------------------------------ INIT --------------------------------------------------*/

        username = (EditText) findViewById(R.id.EditTextUsername);
        email = (EditText) findViewById(R.id.EditTextEmail);
        photo = (ImageView) findViewById(R.id.ImageViewLibrary);
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

    /*------------------------------------------ DATA --------------------------------------------------*/

    private void controlData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (User) extras.getSerializable("user");
        }

        username.setText(user.getUsername());
        email.setText(user.getEmail());

        if (!user.getImage().equals("")) {
            Picasso.get().load(user.getImage()).into(photo, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    photo.setVisibility(View.VISIBLE);
                    username.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                @Override
                public void onError(Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (user.getImage().equals("null")) {
            photo.setImageResource(R.drawable.user128px);
            photo.setVisibility(View.VISIBLE);
            username.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}