package com.example.michele.myparty_new;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import utils.BottomNavigationViewHelper;

public class HomeActivity extends AppCompatActivity {

    private String URL;
    private String name;
    private String userId;
    private String itemId;
    private String itemDate;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        URL = getString(R.string.server_address) +"users/";
        name = getIntent().getStringExtra("name");
        actionGetUserId(name);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        // Show the first fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, PartyListFragment.newInstance())
                .commit();
    }

    // Get user's id from his name
    public void actionGetUserId(String name) {
        StringRequest req = new StringRequest(Request.Method.GET, URL +name,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (response) {
                            case "Bad":
                                AlertDialog.Builder build = new AlertDialog.Builder(HomeActivity.this);
                                build.setTitle(R.string.error);
                                build.setMessage(R.string.error_id_dialog);
                                build.setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface d, int id) {
                                                // Non fa niente
                                            }
                                        });
                                build.setIcon(android.R.drawable.ic_dialog_alert);
                                build.show();
                                break;
                            default:
                                userId = response;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(req);
    }

    // Change the upper fragment based on the user's selection on the lower fragment
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Get which fragment is visible
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
            switch (item.getItemId()) {
                // Based on the lower fragment selection do one of the following
                case R.id.navigation_parties:
                    // Based on the upper fragment visible do one of the following
                    if (currentFragment instanceof PartyFromMapFragment || currentFragment instanceof PartyFragmentQR) {
                        // Detach the fragment (do not destroy it)
                        getSupportFragmentManager()
                                .beginTransaction()
                                .detach(currentFragment)
                                .commit();
                    }
                    else if (currentFragment instanceof MapFragment || currentFragment instanceof TicketsFragment || currentFragment instanceof SettingsFragment) {
                        // Remove the fragment (destroy it)
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .commit();
                    }
                    else {
                        break;
                    }
                    // Search for an existing instance of PartyFragment
                    Fragment f = getSupportFragmentManager().findFragmentByTag("party_details");
                    if (f != null) {
                        // If PartyFragment exists show it
                        getSupportFragmentManager()
                                .beginTransaction()
                                .attach(f)
                                .commit();
                    }
                    else {
                        // Create a new instance of PartyListFragment
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.frame, PartyListFragment.newInstance())
                                .commit();
                    }
                    break;
                case R.id.navigation_map:
                    if (currentFragment instanceof PartyFragment || currentFragment instanceof PartyFragmentQR) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .detach(currentFragment)
                                .commit();
                    }
                    else if (currentFragment instanceof PartyListFragment || currentFragment instanceof TicketsFragment || currentFragment instanceof SettingsFragment) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .commit();
                    }
                    else {
                        break;
                    }
                    Fragment f1 = getSupportFragmentManager().findFragmentByTag("party_from_map");
                    if (f1 != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .attach(f1)
                                .commit();
                    }
                    else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.frame, MapFragment.newInstance())
                                .commit();
                    }
                    break;
                case R.id.navigation_tickets:
                    if (currentFragment instanceof PartyFragment || currentFragment instanceof PartyFromMapFragment) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .detach(currentFragment)
                                .commit();
                    }
                    else if (currentFragment instanceof PartyListFragment || currentFragment instanceof MapFragment || currentFragment instanceof SettingsFragment) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .commit();
                    }
                    else {
                        break;
                    }
                    Fragment f2 = getSupportFragmentManager().findFragmentByTag("party_details_qr");
                    if (f2 != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .attach(f2)
                                .commit();
                    }
                    else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.frame, TicketsFragment.newInstance())
                                .commit();
                    }
                    break;
                case R.id.navigation_settings:
                    if (currentFragment instanceof PartyFragment || currentFragment instanceof PartyFromMapFragment || currentFragment instanceof PartyFragmentQR) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .detach(currentFragment)
                                .commit();
                    }
                    else if (currentFragment instanceof PartyListFragment || currentFragment instanceof MapFragment || currentFragment instanceof TicketsFragment) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(currentFragment)
                                .commit();
                    }
                    else {
                        break;
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.frame, SettingsFragment.newInstance())
                            .commit();
                    break;
            }
            return true;
        }
    };

    // What to do when the user presses back
    public void onBackPressed(){
        // Find which fragment is visible
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame);
        // Based on the fragment do one of the following
         if (currentFragment instanceof PartyFragment) {
             // Replace = remove the current fragment and add the new one
             getSupportFragmentManager()
                     .beginTransaction()
                     .replace(R.id.frame, PartyListFragment.newInstance())
                     .commit();
         }
         else if (currentFragment instanceof PartyFromMapFragment) {
             getSupportFragmentManager()
                     .beginTransaction()
                     .replace(R.id.frame, MapFragment.newInstance())
                     .commit();
         }
         else if (currentFragment instanceof PartyFragmentQR) {
             getSupportFragmentManager()
                     .beginTransaction()
                     .replace(R.id.frame, TicketsFragment.newInstance())
                     .commit();
         }
         else {
             return;
         }
    }

    // Check if the App has location permission, if not try to get it
    public boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation
                AlertDialog.Builder build = new AlertDialog.Builder(HomeActivity.this);
                build.setTitle(R.string.location_permission_title);
                build.setMessage(R.string.location_permission_text);
                build.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                ActivityCompat.requestPermissions(HomeActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        });
                build.setIcon(android.R.drawable.ic_dialog_info);
                build.show();
            } else {
                // Don't need to show an explanation
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        else {
            return true;
        }
    }

    // Check if location permission was granted, if yes update MapFragment
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted
                    if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, MapFragment.newInstance())
                                .commit();
                    }
                } else {
                    // Permission was not granted, do nothing
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public String getName() {
        return this.name;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getItemId() {
        return this.itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemDate() {
        return this.itemDate;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

}
