package com.borido.tfg.services.external;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.borido.tfg.models.Book;
import com.borido.tfg.models.Library;
import androidx.annotation.NonNull;

import com.borido.tfg.LoginActivity;
import com.borido.tfg.models.User;

import com.borido.tfg.R;
import com.borido.tfg.tabs.client.TabsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RealtimeDatabase {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");
    private DatabaseReference librariesRef = database.getReference("libraries");
    private DatabaseReference booksRef = database.getReference("books");

    private Authentication authentication;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();

    private Context context;

    /*------------------------------------------ CONSTRUCTOR --------------------------------------------------*/

    public RealtimeDatabase(Context context) {
        this.context = context;
    }

    /*------------------------------------------ BOOKS --------------------------------------------------*/

    public void updateBook(Book updateBook){
        booksRef.child(updateBook.getId()).setValue(updateBook).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, R.string.toast_edit_books, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void removeBook(Book removeBook){
        booksRef.child(removeBook.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, R.string.toast_remove_books, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addBook(Book addBook){
        booksRef.child(addBook.getId()).setValue(addBook).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, R.string.toast_add_books, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*------------------------------------------ LIBRARIES --------------------------------------------------*/

    public void updateLibrary(Library updateLibrary){
        librariesRef.child(updateLibrary.getId()).setValue(updateLibrary).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, R.string.toast_edit_libraries, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void removeLibrary(Library removeLibrary){
        librariesRef.child(removeLibrary.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, R.string.toast_remove_libraries, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addLibrary(Library addLibrary){
        librariesRef.child(addLibrary.getId()).setValue(addLibrary).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, R.string.toast_add_libraries, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*------------------------------------------ USERS --------------------------------------------------*/

    public void setUser(User newUser){
        usersRef.child(newUser.getId()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, R.string.toast_register_users, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, TabsClient.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateReserves(ArrayList<String> reserves){
        usersRef.child(userAuth.getUid()).child("reserves").setValue(reserves).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, R.string.toast_add_reserves, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateName(String newUsername){
        usersRef.child(userAuth.getUid()).child("username").setValue(newUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, R.string.toast_change_username, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateEmail(String newEmail){
        usersRef.child(userAuth.getUid()).child("email").setValue(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, R.string.toast_change_email, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateImg(String url) {
        usersRef.child(userAuth.getUid()).child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, R.string.toast_change_image, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void deleteUser(){
        this.authentication = new Authentication(context);
        usersRef.child(userAuth.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, R.string.toast_delete_user, Toast.LENGTH_LONG).show();
                    authentication.signOut();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
