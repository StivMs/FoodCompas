package da345af1.ai2530.mah.se.foodcompass;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import da345af1.ai2530.mah.se.foodcompass.Model.MyPlaces;
import da345af1.ai2530.mah.se.foodcompass.Model.Results;
import da345af1.ai2530.mah.se.foodcompass.Remote.IGoogleAPIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NearbyPlaceFragment extends Fragment implements OnMapReadyCallback {
    private final String API_KEY = "AIzaSyCPKURdtmOBan_08oXQfuO_OJ1Fb8G-NLY";
    private final int MY_PERMISSION_CODE = 100;
    private final int REQUEST_CODE = 1000;
    private static final String TAG = "NearbyPlaceFragment";
    private ArrayList<Results> results = new ArrayList<>();

    protected GoogleMap mMap;
    MapView mMapView;
    View mView;


    private double latitude, longitude;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    private MainActivity ma;
    private FragmentController controller;

    private String chosenFood;


    IGoogleAPIService mService;

    public NearbyPlaceFragment() {

    }

    public void setMainActivity(MainActivity mainActivity) {
        this.ma = mainActivity;
    }

    public void setController(FragmentController controller) {
        this.controller = controller;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_nearby_place, container, false);

        // Init service
        mService = Common.getGoogleAPIService();

        //Request runtime permission

        if (ActivityCompat.shouldShowRequestPermissionRationale(ma, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(ma, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            buildLocationRequest();
            buildLocationCallback();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ma);
        }

        if (ActivityCompat.checkSelfPermission(ma, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ma, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ma, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());


        mMapView = (MapView) mView.findViewById(R.id.map);

        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }


        BottomNavigationView bottomNavigationView = (BottomNavigationView) mView.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_restaurant:
                        nearbyPlace("restaurant");
                        break;
                    case R.id.action_hospital:
                        nearbyPlace("hospital");
                        break;
                    case R.id.action_market:
                        nearbyPlace("market");
                        break;
                    case R.id.action_school:
                        nearbyPlace("school");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        return mView;
    }

    public void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();


                if (mMarker != null) {
                    mMarker.remove();
                }

                LatLng latLng = new LatLng(latitude, longitude);

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Your position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                mMarker = mMap.addMarker(markerOptions);

                //Move camera

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


            }
        };
    }

    public void buildLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setSmallestDisplacement(10);
    }


    private void nearbyPlace(final String placeType) {
        if (mMap != null)
            mMap.clear();
        String url = getUrl(latitude, longitude, placeType);

        mService.getNearbyPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(@NonNull Call<MyPlaces> call, @NonNull Response<MyPlaces> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().length; i++) {

                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults()[i];
                                double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                String placeName = googlePlace.getName();
                                String vicinity = googlePlace.getVicinity();
                                LatLng latLng = new LatLng(lat, lng);
                                markerOptions.position(latLng);
                                markerOptions.title(placeName);
                                
                                switch (placeType) {
                                    case "restaurant":
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_restaurant));
                                        break;
                                    case "hospital":
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital));
                                        break;
                                    case "market":
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_market));
                                        break;
                                    case "school":
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_school));
                                        break;
                                    default:
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker());
                                        break;
                                }


                                markerOptions.snippet(String.valueOf(i)); // Assign index for marker

                                // Add to map
                                mMap.addMarker(markerOptions);

                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                                results.add(response.body().getResults()[i]);

                                Log.d(TAG, "onResponse: " + results.get(i).getName()
                                        + results.get(i).getOpening_hours()
                                        + results.get(i).getGeometry().getLocation().toString());

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        // googlePlacesUrl.append("&keyword=" + getChosenFood());
        googlePlacesUrl.append("&type=" + placeType);
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.google_maps_key));
        Log.d("getUrl", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }

    private String getChosenFood() {
        return chosenFood;
    }

    public void setChosenFood(String chosenFood) {
        this.chosenFood = chosenFood;
    }

    @Override
    public void onStop() {
        super.onStop();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(ma, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(ma, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(ma.getApplicationContext());

        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // Init google play services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ma, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
