package com.borido.tfg.tabs.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.borido.tfg.models.User;
import com.borido.tfg.ProfileActivity;
import com.borido.tfg.R;
import com.borido.tfg.SettingsActivity;
import com.borido.tfg.services.external.Authentication;
import com.borido.tfg.services.internal.AlertDialogSesionControl;
import com.borido.tfg.services.internal.MaterialDrawerSesionControl;
import com.borido.tfg.tabs.client.TabsClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class TabsAdmin extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference usersRef = database.getReference("users");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();

    private User userLoged = new User();

    private AlertDialogSesionControl alertDialogSesionControl  = new AlertDialogSesionControl(this);
    private MaterialDrawerSesionControl materialDrawerSesionControl  = new MaterialDrawerSesionControl(this, this);

    /*------------------------------------------ UPDATE DATA --------------------------------------------------*/

    @Override
    public void onStart() {
        super.onStart();
        if(userAuth != null){
            ValueEventListener userListener = new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        User user = data.getValue(User.class);
                        if(userAuth.getEmail().equals(user.getEmail())){
                            userLoged = user;
                            createMaterialDrawer();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("info", "onCancelled");
                }
            };
            usersRef.addListenerForSingleValueEvent(userListener);
        } else {
            createMaterialDrawer();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_tabs_activity);

        /*------------------------------------------ TOOLBAR --------------------------------------------------*/

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        new DrawerBuilder().withActivity(this).build();

        /*------------------------------------------ NAVEGATION --------------------------------------------------*/

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_control_books, R.id.navigation_control_libraries, R.id.navigation_control_profiles).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    /*------------------------------------------ MATERIAL DRAWER --------------------------------------------------*/

    private void createMaterialDrawer() {
        materialDrawerSesionControl.createMaterialDrawerAdmin2(userLoged, mToolbar);
    }

    /*------------------------------------------ MENU --------------------------------------------------*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflaterMenu = getMenuInflater();
        inflaterMenu.inflate(R.menu.exit_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.exit:
                alertDialogSesionControl.createDialogExit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}