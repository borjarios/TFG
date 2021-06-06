package com.borido.tfg.services.internal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.borido.tfg.LoginActivity;
import com.borido.tfg.R;
import com.borido.tfg.services.external.Authentication;

public class AlertDialogSesionControl {

    private Authentication authentication;

    private Context context;

    /*------------------------------------------ CONSTRUCTOR --------------------------------------------------*/

    public AlertDialogSesionControl(Context context) {
        this.context = context;
        this.authentication = new Authentication(context);
    }

    /*------------------------------------------ METHODS --------------------------------------------------*/

    public void createDialogExit(){
        if(authentication.getUser() != null){
            createAlertDialogSesion();
        }else{
            createAlertDialogAplicacion();
        }
    }

    public void createAlertDialogSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setIcon(R.drawable.ic_info);
        builder.setMessage(R.string.message_alert_dialog_logout).setTitle(R.string.title_alert_dialog_logout);
        builder.setPositiveButton(R.string.positive_button_alert_dialog_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                authentication.signOut();
            }
        });
        builder.setNegativeButton(R.string.negative_button_alert_dialog_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor("#3B5998"));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#3B5998"));
    }

    public void createAlertDialogAplicacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setIcon(R.drawable.ic_info);
        builder.setMessage(R.string.message_alert_dialog_exit).setTitle(R.string.title_alert_dialog_exit);
        builder.setPositiveButton(R.string.positive_button_alert_dialog_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                System.exit(0);
            }
        });
        builder.setNegativeButton(R.string.negative_button_alert_dialog_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.parseColor("#3B5998"));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor("#3B5998"));
    }
}
