package da345af1.ai2530.mah.se.foodcompass;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

//TODO Fixa I regret knappen och finish knappen.
// TODO Fixa settingsknappen eller ta bort den?

public class FragmentController {
    private static final String TAG = "FragmentController";
    private MainActivity ma;
    private CompassFragment compassFragment;
    private DistanceFragment distanceFragment;
    private FirstPageFragment firstPageFragment;
    private FoodChoiceFragment foodChoiceFragment;
    private KeywordFragment keywordFragment;
    private ArrivedFragment arrivedFragment;
    private LoadingFragment loadingFragment;

    private double longitude, latitude;
    private boolean ApiStarted = false;
    private int radius;
    private boolean locationSet = false;

    private GoogleAPI googleAPI;

    public FragmentController(MainActivity ma) {
        this.ma = ma;
        initCompass();
        initDistance();
        initFirst();
        initFood();
        initKeyword();
        initArrived();
        initLoading();
        Log.d(TAG, "Starting...");
        fragmentOption("loadingFragment");
        setLocation();
    }

    private void initKeyword() {
        keywordFragment = (KeywordFragment) ma.getFragment("keywordFragment");

        if (keywordFragment == null) {
            keywordFragment = new KeywordFragment();
            keywordFragment.setMainActivity(ma);
        }
        Log.d(TAG, "Setting keywordFragment");
        keywordFragment.setController(this);
    }


    private void initCompass() {
        compassFragment = (CompassFragment) ma.getFragment("compassFragment");
        if (compassFragment == null) {
            compassFragment = new CompassFragment();
            compassFragment.setMainActivity(ma);
        }
        Log.d(TAG, "Setting compass fragment");
        compassFragment.setController(this);
    }

    private void initDistance() {
        distanceFragment = (DistanceFragment) ma.getFragment("distanceFragment");
        if (distanceFragment == null) {
            distanceFragment = new DistanceFragment();
        }
        Log.d(TAG, "Setting distance fragment");
        distanceFragment.setController(this);
    }

    private void initFirst() {
        firstPageFragment = (FirstPageFragment) ma.getFragment("firstPageFragment");
        if (firstPageFragment == null) {
            firstPageFragment = new FirstPageFragment();
        }
        Log.d(TAG, "Setting first fragment");
        firstPageFragment.setController(this);
    }

    private void initFood() {
        foodChoiceFragment = (FoodChoiceFragment) ma.getFragment("foodChoiceFragment");
        if (foodChoiceFragment == null) {
            foodChoiceFragment = new FoodChoiceFragment();
        }
        Log.d(TAG, "Setting food fragment");
        foodChoiceFragment.setController(this);
    }

    private void initArrived(){
        arrivedFragment = (ArrivedFragment)ma.getFragment("arrivedFragment");
        if(arrivedFragment == null){
            arrivedFragment = new ArrivedFragment();
        }
        arrivedFragment.setController(this);
    }

    private void initLoading(){
        loadingFragment = (LoadingFragment)ma.getFragment("loadingFragment");
        if(loadingFragment == null){
            loadingFragment = new LoadingFragment();
        }
        loadingFragment.setController(this);
    }


    public void fragmentOption(String tag) {
        Log.d(TAG, "FRAGMENT OPTION SETTING");
        switch (tag) {
            case "compassFragment":
                setFragment(compassFragment, "compassFragment");
                break;
            case "distanceFragment":
                setFragment(distanceFragment, "distanceFragment");
                break;
            case "firstPageFragment":
                setFragment(firstPageFragment, "firstPageFragment");
                break;
            case "foodChoiceFragment":
                setFragment(foodChoiceFragment, "foodChoiceFragment");
                break;
            case "keywordFragment":
                setFragment(keywordFragment, "keywordFragment");
                break;
            case "arrivedFragment":
                setFragment(arrivedFragment,"arrivedFragment" );
                break;
            case "loadingFragment":
                setFragment(loadingFragment, "loadingFragment");
        }

    }

    public void setFragment(Fragment fragment, String tag) {
        Log.d(TAG, "FRAGMENT SETTING");
        switch (tag) {
            case "compassFragment":
                compassFragment.setTarget(keywordFragment.getTarget());
                ma.setFragment(fragment);
                break;
            case "distanceFragment":
                ma.setFragment(fragment);
                break;
            case "firstPageFragment":
                ma.setFragment(fragment);
                break;
            case "foodChoiceFragment":
                ma.setFragment(fragment);
                break;
            case "keywordFragment":
                googleAPI = new GoogleAPI();
                googleAPI.setLatitude(latitude);
                googleAPI.setLongitude(longitude);
                googleAPI.setKeyword(foodChoiceFragment.chosenFood());
                googleAPI.setRadius(distanceFragment.getRadius());
                googleAPI.setFragment(keywordFragment);
                Thread thread = new FragmentController.StartAPIThread();
                thread.start();
                ma.setFragment(fragment);
                break;
            case "arrivedFragment":
                ma.setFragment(fragment);
                break;
            case "loadingFragment":
                ma.setFragment(fragment);
                break;
        }
    }

    private void setLocation() {
        Log.d(TAG, "Setting device location...");
        LocationManager locationManager = (LocationManager) ma.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!locationSet) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    Log.d(TAG, "onLocationChanged: " + longitude + " " + latitude);
                    fragmentOption("firstPageFragment");
                    locationSet = true;
                    compassFragment.setLocation(latitude, longitude);
                    //TODO avregistrera lyssnare
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



        if (ContextCompat.checkSelfPermission(ma, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ma, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }
        if (ContextCompat.checkSelfPermission(ma, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

    }
    public void initDialog(String text){
        AlertDialog alertDialog = new AlertDialog.Builder(ma)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Warning!")
                .setMessage(text)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    private class StartAPIThread extends Thread {
        public void run() {
            while (!ApiStarted) {
                if (latitude != 0.0 && longitude != 0.0) {
                    googleAPI.setLatitude(latitude);
                    googleAPI.setLongitude(longitude);
                    googleAPI.run();
                    ApiStarted = true;
                }
            }
        }
    }


}