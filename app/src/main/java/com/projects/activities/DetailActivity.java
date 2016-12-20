package com.projects.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.MainApplication;
import com.config.Config;
import com.config.UIConfig;
import com.db.DbHelper;
import com.db.Queries;
import com.libraries.segment.control.MGSegmentControl;
import com.libraries.tab.MGTab;
import com.libraries.utilities.MGUtilities;
import com.models.Favorite;
import com.models.Photo;
import com.models.Restaurant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.projects.restaurantfinder.R;
import com.projects.subdetails.SubDetailAboutView;
import com.projects.subdetails.SubDetailGalleryView;
import com.projects.subdetails.SubDetailMapView;

import java.util.Timer;
import java.util.TimerTask;

public class DetailActivity extends AppCompatActivity implements  View.OnClickListener {

	public Queries q;
    Restaurant restaurant;
    SubDetailAboutView aboutView = null;
    SubDetailMapView mapView = null;
    SubDetailGalleryView galleryView = null;
    boolean isFave = false;
    SwipeRefreshLayout swipeRefresh;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		setContentView(R.layout.fragment_details);
        setTitle(R.string.restaurant_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		q = MainApplication.getQueriesInstance(this);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            swipeRefresh.setProgressViewOffset(false, 0,100);
        }

        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        showRefresh(true);
        restaurant = (Restaurant)this.getIntent().getSerializableExtra("restaurant");

        if(restaurant != null && restaurant.getName() != null && restaurant.getName().length() > 0)
            setTitle(restaurant.getName());

        updateView();
	}

    public void showRefresh(boolean show) {
        swipeRefresh.setRefreshing(show);
        swipeRefresh.setEnabled(show);
    }

    public void updateView() {
        ImageView imgViewPic = (ImageView) findViewById(R.id.imgViewPic);
        Photo p = q.getPhotoByRestaurantId(restaurant.getRestaurant_id());
        if(p != null) {
            String strUrl = p.getPhoto_url();
            if(!strUrl.contains("http")) {
                strUrl = "http://" + strUrl;
            }
            MainApplication.getImageLoaderInstance(this)
                    .displayImage(strUrl, imgViewPic, MainApplication.getDisplayImageOptionsInstance());
        }

        checkFavoriteState();
        showRefresh(false);

        MGTab tabDetails = (MGTab) findViewById(R.id.tabDetails);
        tabDetails.setOnTabListener(new MGTab.OnTabListener() {
            @Override
            public void onTabCreated(MGTab tab, int index, View v) {
                TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                tvTitle.setText(UIConfig.INNER_TAB_TITLE[index]);
                tvTitle.setBackgroundResource(UIConfig.UNSELECTED_INNER_TAB_BG[index]);
            }

            @Override
            public void onTabSelected(MGTab tab, int index, View v) {
                TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                tvTitle.setBackgroundResource(UIConfig.SELECTED_INNER_TAB_BG[index]);
                showDetailFragment(index);
            }

            @Override
            public void onTabPreviouslySelected(MGTab tab, int index, View v) {
                TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
                tvTitle.setText(UIConfig.INNER_TAB_TITLE[index]);
                tvTitle.setBackgroundResource(UIConfig.UNSELECTED_INNER_TAB_BG[index]);
            }
        });
        tabDetails.createTab(3, R.layout.tab_entry, false);
        tabDetails.setSelectedTab(0);
    }

    private void showDetailFragment(int pos) {
        FrameLayout frameDetails = (FrameLayout) findViewById(R.id.containerDetails);
        frameDetails.removeAllViews();
        if(pos == 0) {
            if(aboutView == null) {
                aboutView = new SubDetailAboutView(this);
                aboutView.setDetail(restaurant);
            }
            frameDetails.addView(aboutView.getView());
        }
        else if(pos == 1) {
            if(mapView == null) {
                mapView = new SubDetailMapView(this);
                mapView.loadMap(restaurant);

            }
            frameDetails.addView(mapView.getView());
        }
        else if(pos == 2) {
            if(galleryView == null) {
                galleryView = new SubDetailGalleryView(this);
                galleryView.showGalleries(restaurant);
            }
            frameDetails.addView(galleryView.getView());
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch(v.getId()) {
        }
    }

    private void checkFavoriteState() {
        Restaurant resFave = q.getFavoriteRestaurantsByRestaurantId(restaurant.getRestaurant_id());
        if(resFave == null) {
            isFave = false;
        }
        else {
            isFave = true;
        }
        invalidateOptionsMenu();
    }

    private void addToFavorites() {
        q.insertFavorite(restaurant.getRestaurant_id());
    }

    private void deleteFavorite() {
        Favorite fave = q.getFavoriteByRestaurantId(restaurant.getRestaurant_id());
        q.deleteFavorite(fave.favorite_id);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.addFave:
                Restaurant resFave = q.getFavoriteRestaurantsByRestaurantId(restaurant.getRestaurant_id());
                if(resFave == null) {
                    addToFavorites();
                }
                else {
                    deleteFavorite();
                }
                checkFavoriteState();
                return true;
	        default:
	        	finish();	
	            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        if(isFave)
            getMenuInflater().inflate(R.menu.menu_del_fave, menu);
        else
            getMenuInflater().inflate(R.menu.menu_add_fave, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(android.view.Menu menu) {
        // if nav drawer is opened, hide the action items
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

    public void permissionCall(String phoneNo) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
            if(permissionCheck == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Config.PERMISSION_REQUEST_CALL);
                    return;
                    // PERMISSION_REQUEST_LOCATION is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }

        if(phoneNo == null || phoneNo.length() == 0) {
            Toast.makeText(this, "No phone number provided.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uri = "tel:" + phoneNo.trim() ;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Config.PERMISSION_REQUEST_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permissionCall(restaurant.getPhone());
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    MGUtilities.showAlertView(this, R.string.permission_error, R.string.grant_permission_call);
                }
                return;
            }
        }
    }
    
}
