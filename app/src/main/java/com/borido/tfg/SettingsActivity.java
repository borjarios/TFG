package com.borido.tfg;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.borido.tfg.models.User;
import com.borido.tfg.services.external.Authentication;
import com.borido.tfg.services.external.RealtimeDatabase;
import com.borido.tfg.services.external.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    private Authentication authentication = new Authentication(this);
    private RealtimeDatabase realtimeDatabase = new RealtimeDatabase(this);
    private Storage storage = new Storage(this);

    private User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = (User) extras.getSerializable("user");
        }

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

    /*------------------------------------------ ALERT DIALOGS MANAGAMENT USERS --------------------------------------------------*/

    public void ChangePhoto(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            storage.delImgUser(user);
            String imageUri = String.valueOf(data.getData());
            user.setImage(imageUri);
            storage.updateImgUser(user);
        }
    }

    public void ChangeUsername(View view) {
        final View alertDialog = getLayoutInflater().inflate(R.layout.alert_dialog_change_username, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(this.getDrawable(R.drawable.ic_alert));
        builder.setView(alertDialog);
        builder.setTitle(R.string.title_alert_dialog_change_username);
        builder.setMessage(R.string.message_alert_dialog_change_username);
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
        Button nbutton = alertDialogCreate.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor("#FFDB00"));
        Button pbutton = alertDialogCreate.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#FFDB00"));

        alertDialogCreate.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newUsername = alertDialog.findViewById(R.id.EditText);
                if(!newUsername.getText().toString().trim().equals("")) {
                    realtimeDatabase.updateName(newUsername.getText().toString().trim());
                    alertDialogCreate.dismiss();
                }
                if(newUsername.getText().toString().trim().equals("")) {
                    newUsername.setError(getText(R.string.error_field_settings));
                }
            }
        });
    }

    public void ChangeEmail(View view) {
        final View alertDialog = getLayoutInflater().inflate(R.layout.alert_dialog_change_email, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(this.getDrawable(R.drawable.ic_alert));
        builder.setView(alertDialog);
        builder.setTitle(R.string.title_alert_dialog_change_email);
        builder.setMessage(R.string.message_alert_dialog_change_email);
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
        Button nbutton = alertDialogCreate.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor("#FFDB00"));
        Button pbutton = alertDialogCreate.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#FFDB00"));

        alertDialogCreate.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newEmail = alertDialog.findViewById(R.id.EditText);
                EditText currentPassword = alertDialog.findViewById(R.id.EditText2);
                if(!currentPassword.getText().toString().trim().equals("") && !newEmail.getText().toString().trim().equals("")){
                    authentication.reauthenticateUser(currentPassword.getText().toString().trim());
                    authentication.updateEmail(newEmail.getText().toString().trim());
                    alertDialogCreate.dismiss();
                }
                if(currentPassword.getText().toString().trim().equals("")){
                    currentPassword.setError(getText(R.string.error_field_settings));
                }
                if(newEmail.getText().toString().trim().equals("")){
                    newEmail.setError(getText(R.string.error_field_settings));
                }
            }
        });
    }

    public void ChangePassword(View view) {
        final View alertDialog = getLayoutInflater().inflate(R.layout.alert_dialog_change_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(this.getDrawable(R.drawable.ic_alert));
        builder.setView(alertDialog);
        builder.setTitle(R.string.title_alert_dialog_change_password);
        builder.setMessage(R.string.message_alert_dialog_change_password);
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
        Button nbutton = alertDialogCreate.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor("#FFDB00"));
        Button pbutton = alertDialogCreate.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#FFDB00"));

        alertDialogCreate.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newPassword = alertDialog.findViewById(R.id.EditTextNewPassword);
                EditText currentPassword = alertDialog.findViewById(R.id.EditTextCurrentPassword);
                if(!currentPassword.getText().toString().trim().equals("") && !newPassword.getText().toString().trim().equals("")){
                    authentication.reauthenticateUser(currentPassword.getText().toString().trim());
                    authentication.updatePassword(newPassword.getText().toString().trim());
                    alertDialogCreate.dismiss();
                }
                if(newPassword.getText().toString().trim().equals("")){
                    newPassword.setError(getText(R.string.error_field_settings));
                }
                if(currentPassword.getText().toString().trim().equals("")){
                    currentPassword.setError(getText(R.string.error_field_settings));
                }
            }
        });
    }

    public void deleteUser(View view) {
        final View alertDialog = getLayoutInflater().inflate(R.layout.alert_dialog_delete_user, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(alertDialog);
        builder.setIcon(this.getDrawable(R.drawable.ic_warning));
        builder.setTitle(R.string.title_alert_dialog_delete_user);
        builder.setMessage(R.string.message_alert_dialog_delete_user);
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
        Button nbutton = alertDialogCreate.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor("#FF000E"));
        Button pbutton = alertDialogCreate.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#FF000E"));

        alertDialogCreate.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText password = alertDialog.findViewById(R.id.EditText);
                if(!password.getText().toString().trim().equals("")){
                    authentication.reauthenticateUser(password.getText().toString().trim());
                    authentication.delUser();
                    alertDialogCreate.dismiss();
                }
                if(password.getText().toString().trim().equals("")){
                    password.setError(getText(R.string.error_field_settings));
                }
            }
        });
    }
}