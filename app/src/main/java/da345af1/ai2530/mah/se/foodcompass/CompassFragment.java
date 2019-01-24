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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.maps.model.LatLng;
import com.rockcode.har.HarDataListener;
import com.rockcode.har.HarMode;
import com.rockcode.har.HumanActivity;
import com.rockcode.har.HumanActivityRecognizer;
import com.rockcode.har.RawData;

import java.text.DecimalFormat;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompassFragment extends Fragment implements SensorEventListener, Listener {

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
    LocationListener locationListener;
    EasyWayLocation easyWayLocation;
    private Double lati, longi;
    Button btnRefresh;


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
        easyWayLocation = new EasyWayLocation(getContext());
        easyWayLocation.setListener(this);
        btnRefresh = view.findViewById(R.id.btnRegret);
        btnRefresh.setOnClickListener(new ButtonListener());

        setOrientationSensor();
        setDeviceLocation();
        locationOn();
        onPositionChanged();
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
        Log.d(TAG, "Setting device location.teeest..");

        locationManager = (LocationManager) ma.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Log.d(TAG, "onLocationChanged: " + latitude + " " + longitude);
                locationOn();
                onPositionChanged();


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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
           // onPositionChanged();
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
            Log.d(TAG, "Restaurang position latitude: " + target.getLatitude() + " longitude: " + target.getLongitude());
           //  onPositionChanged();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    private void rotateUsingOrientationSensor(float angle) {

        //setDeviceLocation(); // supposed to find your devices current location

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


    @Override
    public void locationOn() {
        easyWayLocation.beginUpdates();
        lati = easyWayLocation.getLatitude();
        longi = easyWayLocation.getLongitude();

    }



    public double CalculationByDistance(EasyWayLocation.Point StartP, EasyWayLocation.Point EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + meter + "   M  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c*1000;
    }

    public void setLocation(double latutide, double longitude){
        this.latitude = latutide;
        this.longitude = longitude;
    }

    @Override
    public void onPositionChanged() {
        double arrived = 10.0;
        Double distance = 0.0;
        Log.d(TAG, "locationOn: position changed lati: " + latitude + " longi: " + longitude);
        //EasyWayLocation.Point pointStart = new EasyWayLocation.Point(lati, longi);
       // EasyWayLocation.Point pointFinal = new EasyWayLocation.Point(target.getLatitude(), target.getLongitude());

                EasyWayLocation.Point pointStart = new EasyWayLocation.Point(latitude, longitude);
                EasyWayLocation.Point pointFinal = new EasyWayLocation.Point(target.getLatitude(), target.getLongitude());
                distance = CalculationByDistance(pointStart, pointFinal);


        Log.d(TAG, "Distance left: " + Math.floor(distance) + " meter");
                tvNavStatus.setText(String.valueOf(Math.floor(distance)) + " meter left");

        if(distance < arrived){
            locationCancelled();
            Log.d(TAG, "onPositionChanged: " + easyWayLocation.getSpeed());
          //  Toast.makeText(getActivity().getApplicationContext(), "You have arrived!", Toast.LENGTH_SHORT).show();
            // FIX this plz
            controller.fragmentOption("arrivedFragment");

        }




    }

    @Override
    public void locationCancelled() {
        easyWayLocation.endUpdates();
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            locationOn();
            onPositionChanged();
            Toast.makeText(getContext(), "REFRESHED", Toast.LENGTH_SHORT).show();
        }
    }
}