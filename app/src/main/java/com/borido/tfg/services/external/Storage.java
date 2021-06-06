package com.borido.tfg.services.external;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.borido.tfg.LoginActivity;
import com.borido.tfg.R;
import com.borido.tfg.models.Book;
import com.borido.tfg.models.Library;
import com.borido.tfg.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.UUID;

public class Storage {

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference usersImagesRef = storageRef.child("users/");
    private StorageReference librariesImagesRef = storageRef.child("libraries/");
    private StorageReference booksImagesRef = storageRef.child("books/");

    private Context context;

    private RealtimeDatabase realtimeDatabase;

    /*------------------------------------------ CONSTRUCTOR --------------------------------------------------*/

    public Storage(Context context) {
        this.context = context;
    }

    /*------------------------------------------ USERS --------------------------------------------------*/

    public void setImgUser(User newUser, String password){
        this.realtimeDatabase = new RealtimeDatabase(context);
        usersImagesRef = storageRef.child("users/"+newUser.getId());
        usersImagesRef.putFile(Uri.parse(newUser.getImage())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    usersImagesRef = storageRef.child("users/");
                    usersImagesRef.child(newUser.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newUser.setImage(uri.toString());
                            realtimeDatabase.setUser(newUser);
                        }
                    });
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void delImgUser(User delUser){
        usersImagesRef = storageRef.child("users/"+delUser.getId());
        usersImagesRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("info", "OnComplete");
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateImgUser(User uploadUser){
        this.realtimeDatabase = new RealtimeDatabase(context);
        usersImagesRef = storageRef.child("users/"+uploadUser.getId());
        usersImagesRef.putFile(Uri.parse(uploadUser.getImage())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    usersImagesRef = storageRef.child("users/");
                    usersImagesRef.child(uploadUser.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            uploadUser.setImage(uri.toString());
                            realtimeDatabase.updateImg(uploadUser.getImage());
                        }
                    });
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /*------------------------------------------ LIBRARIES --------------------------------------------------*/

    public void setImgLibrary(Library addLibrary) {
        this.realtimeDatabase = new RealtimeDatabase(context);
        if(!addLibrary.getImage().equals("null")){
            librariesImagesRef = storageRef.child("libraries/"+addLibrary.getId());
            librariesImagesRef.putFile(Uri.parse(addLibrary.getImage())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        usersImagesRef = storageRef.child("libraries/");
                        usersImagesRef.child(addLibrary.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                addLibrary.setImage(uri.toString());
                                realtimeDatabase.addLibrary(addLibrary);
                            }
                        });
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if(addLibrary.getImage().equals("null")){
            realtimeDatabase.addLibrary(addLibrary);
        }
    }

    public void delImgLibrary(Library delLibrary){
        this.realtimeDatabase = new RealtimeDatabase(context);
        if(!delLibrary.getImage().equals("null")) {
            librariesImagesRef = storageRef.child("libraries/" + delLibrary.getId());
            librariesImagesRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        realtimeDatabase.removeLibrary(delLibrary);
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if(delLibrary.getImage().equals("null")){
            realtimeDatabase.removeLibrary(delLibrary);
        }
    }

    public void updateImgLibrary(Library uploadLibrary, Library beforeLibrary){
        this.realtimeDatabase = new RealtimeDatabase(context);
        if(beforeLibrary.getImage().equals("null")){
            usersImagesRef = storageRef.child("libraries/" + uploadLibrary.getId());
            usersImagesRef.putFile(Uri.parse(uploadLibrary.getImage())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        usersImagesRef = storageRef.child("libraries/");
                        usersImagesRef.child(uploadLibrary.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uploadLibrary.setImage(uri.toString());
                                realtimeDatabase.updateLibrary(uploadLibrary);
                            }
                        });
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else{
            if(!beforeLibrary.getImage().equals("null")){
                librariesImagesRef = storageRef.child("libraries/" + uploadLibrary.getId());
                librariesImagesRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            usersImagesRef = storageRef.child("libraries/" + uploadLibrary.getId());
                            usersImagesRef.putFile(Uri.parse(uploadLibrary.getImage())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        usersImagesRef = storageRef.child("libraries/");
                                        usersImagesRef.child(uploadLibrary.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                uploadLibrary.setImage(uri.toString());
                                                realtimeDatabase.updateLibrary(uploadLibrary);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    /*------------------------------------------ BOOKS --------------------------------------------------*/

    public void setImgBook(Book addBook) {
        this.realtimeDatabase = new RealtimeDatabase(context);
        if(!addBook.getImage().equals("null")){
            booksImagesRef = storageRef.child("books/"+addBook.getId());
            booksImagesRef.putFile(Uri.parse(addBook.getImage())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        booksImagesRef = storageRef.child("books/");
                        booksImagesRef.child(addBook.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                addBook.setImage(uri.toString());
                                realtimeDatabase.addBook(addBook);
                            }
                        });
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if(addBook.getImage().equals("null")){
            realtimeDatabase.addBook(addBook);
        }
    }

    public void delImgBook(Book delBook){
        this.realtimeDatabase = new RealtimeDatabase(context);
        if(!delBook.getImage().equals("null")) {
            booksImagesRef = storageRef.child("books/" + delBook.getId());
            booksImagesRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        realtimeDatabase.removeBook(delBook);
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if(delBook.getImage().equals("null")){
            realtimeDatabase.removeBook(delBook);
        }
    }

    public void updateImgBook(Book uploadBook, boolean control){
        this.realtimeDatabase = new RealtimeDatabase(context);
        if(control == true){
            booksImagesRef = storageRef.child("books/" + uploadBook.getId());
            booksImagesRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        usersImagesRef = storageRef.child("books/" + uploadBook.getId());
                        usersImagesRef.putFile(Uri.parse(uploadBook.getImage())).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    usersImagesRef = storageRef.child("books/");
                                    usersImagesRef.child(uploadBook.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            uploadBook.setImage(uri.toString());
                                            realtimeDatabase.updateBook(uploadBook);
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if(control == false) {
            realtimeDatabase.updateBook(uploadBook);
        }
    }
}
