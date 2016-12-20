package com.libraries.location;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.libraries.utilities.MGUtilities;
import com.projects.restaurantfinder.R;

/**
 * Created by mg on 24/10/16.
 */
public class MGLocationManagerUtils {

    OnAlertListener mCallback;

    public interface OnAlertListener {
        public void onPositiveTapped();
        public void onNegativeTapped();
    }

    public void setOnAlertListener(OnAlertListener listener) {
        try {
            mCallback = (OnAlertListener) listener;
        } catch (ClassCastException e)  {
            throw new ClassCastException(this.toString() + " must implement OnAlertListener");
        }
    }

    public void showAlertView(Activity act, int resIdTitle, int resIdMessage, int resIdPositiveButton, int resIdNegativeButton) {
        AlertDialog.Builder alert = new AlertDialog.Builder(act);
        alert.setTitle(resIdTitle);
        alert.setMessage(resIdMessage);
        alert.setPositiveButton(act.getResources().getString(resIdPositiveButton),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        if(mCallback != null)
                            mCallback.onPositiveTapped();
                    }
                });

        alert.setNegativeButton(act.getResources().getString(resIdNegativeButton),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        if(mCallback != null)
                            mCallback.onNegativeTapped();
                    }
                });

        alert.create();
        alert.show();
    }
}
