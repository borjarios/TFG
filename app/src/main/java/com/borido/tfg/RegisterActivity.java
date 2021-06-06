package com.borido.tfg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.borido.tfg.models.User;
import com.borido.tfg.services.external.Authentication;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, email, password;
    private Button singIn;
    private CheckBox terms;
    private ImageView imgUser;

    private Authentication authentication = new Authentication(this);

    private User addUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*------------------------------------------ INIT --------------------------------------------------*/

        username = (EditText) findViewById(R.id.EditTextUsername);
        email = (EditText) findViewById(R.id.EditTextEmail);
        password = (EditText) findViewById(R.id.EditTextPassword);
        singIn = (Button) findViewById(R.id.ButtomRegister);
        terms = (CheckBox) findViewById(R.id.checkBoxTerms);
        imgUser = (ImageView) findViewById(R.id.ImageViewLibrary);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    register(getWindow().getDecorView().getRootView());
                    return true;
                }
                return false;
            }
        });

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
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.addPhoto:
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 100);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*------------------------------------------ METODS --------------------------------------------------*/

    public void register(View view) {
        if(!username.getText().toString().trim().equals("") && !email.getText().toString().trim().equals("") && !password.getText().toString().trim().equals("")  && terms.isChecked()){
            addUser.setUsername(username.getText().toString().trim());
            addUser.setEmail(email.getText().toString().trim());
            addUser.setAdmin(false);
            addUser.setReserves(null);

            if(addUser.getImage() == null){
                addUser.setImage("null");
            }

            authentication.registerUser(addUser, password.getText().toString().trim());
        }

        if(!terms.isChecked()){
            terms.setError(getText(R.string.error_field_login));
        }

        if(username.getText().toString().trim().equals("")){
            username.setError(getText(R.string.error_field_login));
        }

        if(email.getText().toString().trim().equals("")){
            email.setError(getText(R.string.error_field_login));
        }

        if(password.getText().toString().trim().equals("")){
            password.setError(getText(R.string.error_field_login));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            String imageUri = String.valueOf(data.getData());
            imgUser.setImageURI(Uri.parse(imageUri));
            addUser.setImage(imageUri);
        }
    }
}