package da345af1.ai2530.mah.se.foodcompass;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompassFragment extends Fragment implements SensorEventListener {
    private static final String TAG = "Compass Fragment";
    private MainActivity ma;
    private FragmentController controller;
    private SensorManager sensorManager;
    private Sensor orientation;
    private ImageView ivCompass;
    private float current = 0f;
    private float ori;
    private Location target = new Location("Y");
    private Location location = new Location("X");
    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GeomagneticField geoField;
    private double longitude;
    private double latitude;


    public CompassFragment() {
    }

    public CompassFragment(MainActivity ma) {
        this.ma = ma;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compass, container, false);

        ivCompass = view.findViewById(R.id.ivCompass);

        setOrientationSensor();
        setDeviceLocation();
        return view;
    }

    private void setOrientationSensor() {

        Log.d(TAG, "setting Sensors...");
        sensorManager = (SensorManager) ma.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(3) != null) {
            orientation = sensorManager.getDefaultSensor(3);
            Log.d(TAG, "Orientation sensor found!");
        } else if (sensorManager.getDefaultSensor(3) == null) {
            toastMessage("Orientation sensor is null");
            Log.d(TAG, "Orientation sensor is null");
        }

        sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
        toastMessage("orientation listener registered");
        Log.d(TAG, "orientation listener registered");


    }

    private void setDeviceLocation() {

        Log.d(TAG, "Setting device location...");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        locationManager = (LocationManager) ma.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(ma.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ma.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            Log.d(TAG, "Permissons OK!");
        }else {
            ActivityCompat.requestPermissions(ma, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

        geoField = new GeomagneticField(
                Double.valueOf(location.getLatitude()).floatValue(),
                Double.valueOf(location.getLongitude()).floatValue(),
                Double.valueOf(location.getAltitude()).floatValue(),
                System.currentTimeMillis());

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == 3) {
            ori = event.values[0];
            rotateUsingOrientationSensor(ori);
        }
    }


    @Override public void onAccuracyChanged (Sensor sensor,int accuracy){ }




    private void rotateUsingOrientationSensor (float angle){
            setDeviceLocation(); // supposed to find your devices current location

            target.setLatitude(55.60282483143015); // change for testing until google places is implemented
            target.setLongitude(13.000476497005366); // change for testing until google places is implemented

            location.setLatitude(latitude);
            location.setLongitude(longitude);

            float degree = Math.round(angle);
            degree += geoField.getDeclination(); //adjusting compass between true north/magnetic north
            float bearing = location.bearingTo(target);

            float direction = bearing - (bearing - degree) * -1;
            direction = normalizeDegree(direction);

            RotateAnimation ra = new RotateAnimation(current, -direction, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            ra.setDuration(210);
            ra.setFillAfter(true);
            ivCompass.startAnimation(ra);
            current = -direction;
        }

        private float normalizeDegree ( float value){
            if (value >= 0.0f && value <= 180.0f) {
                return value;
            } else {
                return 180 + (180 + value);
            }
        }

        private void toastMessage (String message){
            Toast.makeText(ma, message, Toast.LENGTH_LONG).show();
        }

    public void setController(FragmentController controller) {
        this.controller = controller;
    }


    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG, "MyLocationListener: " + latitude + ", " + longitude);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }

    }
}
