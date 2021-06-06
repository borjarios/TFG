package com.borido.tfg.services.external;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.borido.tfg.LoginActivity;
import com.borido.tfg.models.User;
import com.borido.tfg.R;
import com.borido.tfg.tabs.client.TabsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Authentication {

    private static final String TAG = "info";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();

    private Context context;

    private RealtimeDatabase realtimeDatabase;
    private Storage storage;

    /*------------------------------------------ CONSTRUCTOR --------------------------------------------------*/

    public Authentication(Context context) {
        this.context = context;
    }

    /*------------------------------------------ METHODS --------------------------------------------------*/

    public FirebaseUser getUser(){
        return userAuth;
    }

    public String getUid(){
        return mAuth.getUid();
    }

    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, R.string.toast_login_users, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(context, TabsClient.class);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void registerUser(User newUser, String password) {
        this.storage = new Storage(context);
        this.realtimeDatabase = new RealtimeDatabase(context);
        mAuth.createUserWithEmailAndPassword(newUser.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    newUser.setId(mAuth.getUid());
                    if(!newUser.getImage().equals("null")){
                        storage.setImgUser(newUser, password);
                    }
                    if(newUser.getImage().equals("null")){
                        realtimeDatabase.setUser(newUser);
                    }
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void forgotPassword(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, R.string.toast_fogot_password_users, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void reauthenticateUser(String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(userAuth.getEmail(), password);
        userAuth.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "User re-authenticated.");
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateEmail(String newEmail) {
        this.realtimeDatabase = new RealtimeDatabase(context);
        userAuth.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    realtimeDatabase.updateEmail(newEmail);
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updatePassword(String newPassword) {
        userAuth.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, R.string.toast_change_password, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void delUser() {
        this.realtimeDatabase = new RealtimeDatabase(context);
        userAuth.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    realtimeDatabase.deleteUser();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signOut() {
        mAuth.getInstance().signOut();
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        Toast.makeText(context, R.string.toast_logout_users, Toast.LENGTH_SHORT).show();
    }







}
