package com.borido.tfg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.borido.tfg.services.external.Authentication;
import com.borido.tfg.tabs.client.TabsClient;

public class LoginActivity extends AppCompatActivity {

    private TextView email, password;
    private Button logIn, singIn;

    private Authentication authentication = new Authentication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*------------------------------------------ INIT --------------------------------------------------*/

        email = (TextView) findViewById(R.id.EditTextEmail);
        password = (TextView) findViewById(R.id.EditTextPassword);
        logIn = (Button) findViewById(R.id.buttomLogin);
        singIn = (Button) findViewById(R.id.ButtomRegister);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    login();
                    return true;
                }
                return false;
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email.setText(extras.getString("email"));
            password.setText(extras.getString("password"));
        }

        /*------------------------------------------ TOOLBAR --------------------------------------------------*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), TabsClient.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*------------------------------------------ METODS --------------------------------------------------*/

    public void forgotPassword(View view) {
        final View alertDialog = getLayoutInflater().inflate(R.layout.alert_dialog_forgot_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(alertDialog);
        builder.setTitle(R.string.title_alert_dialog_forgot_password);
        builder.setMessage(R.string.message_alert_dialog_forgot_password);
        builder.setPositiveButton(R.string.positive_button_alert_dialog_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("info", "setPositiveButton");
            }
        });
        builder.setNegativeButton(R.string.negative_button_alert_dialog_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("info", "setNegativeButton");
                dialog.dismiss();
            }
        });

        AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.show();
        alertDialogCreate.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = alertDialog.findViewById(R.id.EditText);
                if(!email.getText().toString().trim().equals("")) {
                    authentication.forgotPassword(email.getText().toString().trim());
                    alertDialogCreate.dismiss();
                }
                if(email.getText().toString().trim().equals("")){
                    email.setError(getText(R.string.error_field_login));
                }
            }
        });
    }

    private void login() {
        if(!email.getText().toString().trim().equals("") && !password.getText().toString().trim().equals("")){
            authentication.loginUser(email.getText().toString().trim(), password.getText().toString().trim());
        }

        if(email.getText().toString().trim().equals("")){
            email.setError(getText(R.string.error_field_login));
        }

        if(password.getText().toString().trim().equals("")){
            password.setError(getText(R.string.error_field_login));
        }
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void loginWithGoogle() {
        Log.i("info", "loginWithGoogle");
    }

    public void loginWithFacebook() {
        Log.i("info", "loginWithFacebook");
    }

}