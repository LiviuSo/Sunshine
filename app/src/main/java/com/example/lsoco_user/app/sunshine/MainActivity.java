package com.example.lsoco_user.app.sunshine;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_location:
                showPrefLocation();
                break;
            default:
                return false;
        }
        return true;
    }

    private void showPrefLocation() {
        // get the zip code from preferences
        String zip = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        // convert the zip code into geo coords (my solution)
//        Address address = getAddressFromZip(zip);
//        if(address != null) {
//            String uriString = String.format(Locale.getDefault(), "geo:%f,%f", address.getLatitude(), address.getLongitude());
//            Uri uri = Uri.parse(uriString);
//
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(uri);
//            // test if any app can handle the intent & show the location
//            if(intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                Log.e(LOG_TAG, "No activity to show the map");
//            }
//        } else {
//            // Display appropriate message when Geocoder services are not available
//            Toast.makeText(this, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
//        }
        Uri uri = Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", zip)
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        // test if any app can handle the intent & show the location
        if(intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(LOG_TAG, "No activity to show the map");
        }
    }

//    private Address getAddressFromZip(String zip) {
//        final Geocoder geocoder = new Geocoder(this);
//        try {
//            List<Address> addresses = geocoder.getFromLocationName(zip, 1);
//            if (addresses != null && !addresses.isEmpty()) {
//                return addresses.get(0);
//            }
//        } catch (IOException e) {
//            // handle exception
//            Log.e(LOG_TAG, e.getMessage(), e);
//        }
//        return null;
//    }
}
