package com.projects.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.MainApplication;
import com.db.Queries;
import com.libraries.asynctask.MGAsyncTaskNoDialog;
import com.models.Category;
import com.models.Restaurant;
import com.projects.activities.DetailActivity;
import com.projects.patteo.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Vinicius on 18/01/2017.
 */
public class AnuncioFragment extends Fragment implements View.OnClickListener {

    MGAsyncTaskNoDialog task;
    private View viewInflate;
    Restaurant restaurants;
    Queries q;

    TextView txtTel;
    TextView txtEmail;
    TextView txtWeb;

    private FragmentActivity act;

    public AnuncioFragment() {

    }

    public AnuncioFragment(FragmentActivity act) {
        this.act = act;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewInflate = inflater.inflate(R.layout.fragment_indicacao, null);
        return viewInflate;
    }

    public void onDestroyView() {
        super.onDestroyView();

        if(task != null)
            task.cancel(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        restaurants = new Restaurant();
        q = MainApplication.getQueriesInstance(getContext());

        txtTel = (TextView) viewInflate.findViewById(R.id.txttel1) ;
        txtEmail = (TextView) viewInflate.findViewById(R.id.txtemail1) ;
        txtWeb = (TextView) viewInflate.findViewById(R.id.txtweb1) ;

        getData();

    }

    public void getData() {
        task = new MGAsyncTaskNoDialog(getActivity());
        task.setMGAsyncTaskListener(new MGAsyncTaskNoDialog.OnMGAsyncTaskListenerNoDialog() {

            @Override
            public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask) { }

            @Override
            public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask) {
                // TODO Auto-generated method stub
                restaurants = q.getRestaurantByRestaurantId(1);
                txtTel.setText(restaurants.getPhone());
                txtEmail.setText(restaurants.getEmail());
                txtWeb.setText(restaurants.getWebsite());

                txtTel.setPaintFlags(txtTel.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                txtEmail.setPaintFlags(txtEmail.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
                txtWeb.setPaintFlags(txtWeb.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

                txtTel.setOnClickListener(AnuncioFragment.this);
                txtEmail.setOnClickListener(AnuncioFragment.this);
                txtWeb.setOnClickListener(AnuncioFragment.this);

                }
        });
        task.execute();
    }

    @Override
    public void onClick(View v) {
        Log.i("________",restaurants.getPhone());
        // TODO Auto-generated method stub
        switch(v.getId()) {
            case R.id.txttel1:
                call(restaurants.getPhone());
                break;
            case R.id.txtemail1:
                email(restaurants.getEmail());
                break;
            case R.id.txtweb1:
                website(restaurants.getWebsite());
                break;
        }
    }

    private void call(String phoneNo) {
        permissionCall(phoneNo);
    }

    private void website(String url) {
        if(url == null || url.length() == 0) {
            Toast.makeText(act, "No website provided.", Toast.LENGTH_SHORT).show();
            return;
        }
        String strUrl = url;
        if(!url.contains("http")) {
            strUrl = "http://" + url;
        }
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(strUrl));
        act.startActivity(Intent.createChooser(webIntent, "Choose a browser :"));
    }

    private void email(String email) {
        if(email == null || email.length() == 0) {
            Toast.makeText(act, "No email provided.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email} );
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Restaurant Inquiry");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your Message Here...");
        emailIntent.setType("message/rfc822");
        act.startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
    }

    public void permissionCall(String phoneNo) {

        String uri = "tel:" + phoneNo.trim();
        Log.i("", uri);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(String.valueOf(Uri.parse(uri))));
        startActivity(intent);
    }
}
