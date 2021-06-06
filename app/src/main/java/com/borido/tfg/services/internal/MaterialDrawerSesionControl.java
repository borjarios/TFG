package com.borido.tfg.services.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.borido.tfg.models.User;
import androidx.appcompat.widget.Toolbar;

import com.borido.tfg.ProfileActivity;
import com.borido.tfg.R;
import com.borido.tfg.SettingsActivity;

import com.borido.tfg.tabs.admin.TabsAdmin;
import com.borido.tfg.tabs.client.TabsClient;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

public class MaterialDrawerSesionControl {

    private Context context;
    private Activity activity;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private StorageReference usersImagesRef = storageRef.child("users/");

    private Drawer mDrawer;
    private AlertDialogSesionControl alertDialogSesionControl;

    /*------------------------------------------ CONSTRUCTOR --------------------------------------------------*/

    public MaterialDrawerSesionControl(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    /*------------------------------------------ METHODS --------------------------------------------------*/

    public void createMaterialDrawerAdmin(User userLoged, Toolbar mToolbar) {

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                if(!userLoged.getImage().equals("")){
                    Picasso.get().load(uri).placeholder(placeholder).into(imageView);
                }
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().load(R.drawable.user128px).into(imageView);
            }
        });

        alertDialogSesionControl = new AlertDialogSesionControl(context);

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.color.white)
                .addProfiles(
                        new ProfileDrawerItem().withIcon(userLoged.getImage()).withName(userLoged.getUsername()).withEmail(userLoged.getEmail())
                )
                .build();

        mDrawer = new DrawerBuilder()
                .withActivity(activity)
                .withActionBarDrawerToggle(true)
                .withToolbar(mToolbar)
                .withSelectedItem(10)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.START)
                .withAccountHeader(header)
                .withSliderBackgroundColor(context.getResources().getColor(R.color.white))
                .addDrawerItems(
                        new SecondaryDrawerItem()
                                .withIdentifier(1)
                                .withName(R.string.section_administration)
                                .withIcon(R.drawable.ic_admin),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withIdentifier(2)
                                .withName(R.string.section_profile)
                                .withIcon(R.drawable.ic_profile),
                        new SecondaryDrawerItem()
                                .withIdentifier(3)
                                .withName(R.string.section_settings)
                                .withIcon(R.drawable.ic_settings),
                        new SecondaryDrawerItem()
                                .withIdentifier(4)
                                .withName(R.string.section_logout)
                                .withIcon(R.drawable.ic_exit)
                ).withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                switch ((int) drawerItem.getIdentifier()) {
                                    case 1:
                                        Intent i1 = new Intent(context, TabsAdmin.class);
                                        context.startActivity(i1);
                                        break;
                                    case 2:
                                        Intent i2 = new Intent(context, ProfileActivity.class);
                                        i2.putExtra("user", userLoged);
                                        context.startActivity(i2);
                                        break;
                                    case 3:
                                        Intent i3 = new Intent(context, SettingsActivity.class);
                                        i3.putExtra("user", userLoged);
                                        context.startActivity(i3);
                                        break;
                                    case 4:
                                        alertDialogSesionControl.createDialogExit();
                                        break;
                                    default:
                                        Log.i("info", "DEFAULT");
                                        break;
                                }
                                mDrawer.deselect();
                                mDrawer.closeDrawer();
                                return true;
                            }
                        })
                .build();
    }

    public void createMaterialDrawerAdmin2(User userLoged, Toolbar mToolbar){
        alertDialogSesionControl = new AlertDialogSesionControl(context);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                if(!userLoged.getImage().equals("")){
                    Picasso.get().load(uri).placeholder(placeholder).into(imageView);
                }
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().load(R.drawable.user128px).into(imageView);
            }
        });

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.color.white)
                .addProfiles(
                        new ProfileDrawerItem().withIcon(userLoged.getImage()).withName(userLoged.getUsername()).withEmail(userLoged.getEmail())
                )
                .build();

        mDrawer = new DrawerBuilder()
                .withActivity(activity)
                .withActionBarDrawerToggle(true)
                .withToolbar(mToolbar)
                .withSelectedItem(10)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.START)
                .withAccountHeader(header)
                .withSliderBackgroundColor(context.getResources().getColor(R.color.white))
                .addDrawerItems(
                        new SecondaryDrawerItem()
                                .withIdentifier(1)
                                .withName(R.string.section_libraries)
                                .withIcon(R.drawable.ic_books),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withIdentifier(2)
                                .withName(R.string.section_profile)
                                .withIcon(R.drawable.ic_profile),
                        new SecondaryDrawerItem()
                                .withIdentifier(3)
                                .withName(R.string.section_settings)
                                .withIcon(R.drawable.ic_settings),
                        new SecondaryDrawerItem()
                                .withIdentifier(4)
                                .withName(R.string.section_logout)
                                .withIcon(R.drawable.ic_exit)
                ).withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                switch ((int) drawerItem.getIdentifier()) {
                                    case 1:
                                        Intent i1 = new Intent(context, TabsClient.class);
                                        context.startActivity(i1);
                                        break;
                                    case 2:
                                        Intent i2 = new Intent(context, ProfileActivity.class);
                                        i2.putExtra("user", userLoged);
                                        context.startActivity(i2);
                                        break;
                                    case 3:
                                        Intent i3 = new Intent(context, SettingsActivity.class);
                                        i3.putExtra("user", userLoged);
                                        context.startActivity(i3);
                                        break;
                                    case 4:
                                        alertDialogSesionControl.createDialogExit();
                                        break;
                                    default:
                                        Log.i("info", "DEFAULT");
                                        break;
                                }
                                mDrawer.deselect();
                                mDrawer.closeDrawer();
                                return true;
                            }
                        })
                .build();
    }

    public void createMaterialDrawerUser(User userLoged, Toolbar mToolbar) {
        alertDialogSesionControl = new AlertDialogSesionControl(context);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                if(!userLoged.getImage().equals("")){
                    Picasso.get().load(uri).placeholder(placeholder).into(imageView);
                }
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().load(R.drawable.user128px).into(imageView);
            }
        });

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.color.white)
                .addProfiles(
                        new ProfileDrawerItem().withIcon(userLoged.getImage()).withName(userLoged.getUsername()).withEmail(userLoged.getEmail())
                )
                .build();

        mDrawer = new DrawerBuilder()
                .withActivity(activity)
                .withActionBarDrawerToggle(true)
                .withToolbar(mToolbar)
                .withSelectedItem(10)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.START)
                .withAccountHeader(header)
                .withSliderBackgroundColor(context.getResources().getColor(R.color.white))
                .addDrawerItems(
                        new SecondaryDrawerItem()
                                .withIdentifier(1)
                                .withName(R.string.section_profile)
                                .withIcon(R.drawable.ic_profile),
                        new SecondaryDrawerItem()
                                .withIdentifier(2)
                                .withName(R.string.section_settings)
                                .withIcon(R.drawable.ic_settings),
                        new SecondaryDrawerItem()
                                .withIdentifier(3)
                                .withName(R.string.section_logout)
                                .withIcon(R.drawable.ic_exit)
                ).withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                switch ((int) drawerItem.getIdentifier()) {
                                    case 1:
                                        Intent i1 = new Intent(context, ProfileActivity.class);
                                        i1.putExtra("user", userLoged);
                                        context.startActivity(i1);
                                        break;
                                    case 2:
                                        Intent i2 = new Intent(context, SettingsActivity.class);
                                        i2.putExtra("user", userLoged);
                                        context.startActivity(i2);
                                        break;
                                    case 3:
                                        alertDialogSesionControl.createDialogExit();
                                        break;
                                    default:
                                        Log.i("info", "DEFAULT");
                                        break;
                                }
                                mDrawer.deselect();
                                mDrawer.closeDrawer();
                                return true;
                            }
                        })
                .build();
    }

    public void closeDrawer(){
        mDrawer.closeDrawer();
    }


}
