package da345af1.ai2530.mah.se.foodcompass;


import android.Manifest;
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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.rockcode.har.HarDataListener;
import com.rockcode.har.HarMode;
import com.rockcode.har.HumanActivity;
import com.rockcode.har.HumanActivityRecognizer;
import com.rockcode.har.RawData;

import java.util.HashMap;
import java.util.List;
import java.util.Random;


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
    private long lastUpdateTime;
    HumanActivityRecognizer mHAR;
    TextView tvNavStatus;


    public CompassFragment() {
    }


    public void setMainActivity(MainActivity mainActivity) {
        this.ma = mainActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_compass, container, false);

        ivCompass = view.findViewById(R.id.ivCompass);
        tvNavStatus = view.findViewById(R.id.tvNavStatus);


        setOrientationSensor();
        setDeviceLocation();
        return view;
    }


    private void initHAR() {
        mHAR = new HumanActivityRecognizer(ma, true, HarMode.CLASSIFY, mHarDataListener);
        mHAR.start();
    }

    public void setTarget(LatLng target) {
        this.target.setLatitude(target.latitude);
        this.target.setLongitude(target.longitude);
    }

    private HarDataListener mHarDataListener = new HarDataListener() {
        @Override
        public void onHarDataChange(HumanActivity humanActivity) {
            tvNavStatus.setText("Status: " + humanActivity.mActivity);

        }

        @Override
        public void onHarRawDataChange(List<RawData> list) {

        }
    };

    void setOrientationSensor() {

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

    void setDeviceLocation() {
        Log.d(TAG, "Setting device location...");
        LocationManager locationManager = (LocationManager) ma.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.d(TAG, "onLocationChanged: " + longitude + " " + latitude);
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
            // System.out.println("latitude: " + target.getLatitude() + " longitude: " + target.getLongitude());
            Log.d(TAG, "latitude: " + target.getLatitude() + " longitude: " + target.getLongitude());
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private void rotateUsingOrientationSensor(float angle) {

        setDeviceLocation(); // supposed to find your devices current location

        location.setLatitude(latitude);
        location.setLongitude(longitude);

        if (System.currentTimeMillis() - lastUpdateTime > 250) {
            float degree = Math.round(angle);
            degree += geoField.getDeclination(); //adjusting compass between true north/magnetic north
            float bearing = location.bearingTo(target);

            float direction = bearing - (bearing - degree) * -1;
            direction = normalizeDegree(direction);

            RotateAnimation ra = new RotateAnimation(current, direction, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            ra.setDuration(250);
            ra.setFillAfter(true);
            ivCompass.startAnimation(ra);
            current = direction;
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    private float normalizeDegree(float value) {
        if (value >= 0.0f && value <= 180.0f) {
            return value;
        } else {
            return 180 + (180 + value);
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(ma, message, Toast.LENGTH_LONG).show();
    }

    public void setController(FragmentController controller) {
        this.controller = controller;
    }


    public void setLocation(List<HashMap<String, String>> list) {
        Random random = new Random();
        int number = 4;
        HashMap<String, String> hmPlace = list.get(number);

        double lat = Double.parseDouble(hmPlace.get("lat"));
        double lng = Double.parseDouble(hmPlace.get("lng"));

        Log.d(TAG, "strawberries " + lat + " " + lng);

        target.setLatitude(lat); // change for testing until google places is implemented
        target.setLongitude(lng); // change for testing until google places is implemented
    }

}