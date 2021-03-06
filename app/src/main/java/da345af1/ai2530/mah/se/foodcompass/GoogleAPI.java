package da345af1.ai2530.mah.se.foodcompass;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


public class GoogleAPI {

    JSONObject data = null;
    private CompassFragment compassFragment;
    private KeywordFragment keywordFragment;
    private String keyword;

    private double latitude, longitude;

    private int radius;

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public GoogleAPI() {
    }

    public void run() {
        StringBuilder sbValue = new StringBuilder(sbMethod());
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(sbValue.toString());
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    public StringBuilder sbMethod() {

        double mLatitude = latitude;
        double mLongitude = longitude;
        String keyword = this.keyword;
        Log.d(TAG, "Location in API set to: Latitude: " + mLatitude + ", Longitude: " + mLongitude);
        int mRadius = radius;


        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + mLatitude + "," + mLongitude);
        sb.append("&radius=" + mRadius);
        sb.append("&types=" + "restaurant");
        sb.append("&keyword=" + keyword);
        sb.append("&key=AIzaSyCPKURdtmOBan_08oXQfuO_OJ1Fb8G-NLY");

        Log.d("Map", "api: " + sb.toString());

        return sb;
    }

    public void setFragment(Fragment fragment) {
        if (fragment instanceof CompassFragment)
            this.compassFragment = (CompassFragment) fragment;
        else if (fragment instanceof KeywordFragment)
            this.keywordFragment = (KeywordFragment) fragment;
    }

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exce when dwnld url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJson.parse(jObject);
                final List<HashMap<String, String>> listMap = places;

                if (keywordFragment != null) {
                    Log.d(TAG, "doInBackground: KEYWORD EXIST=");
                    final Handler UIhandler = new Handler(Looper.getMainLooper());
                    UIhandler.post(() -> {
                        keywordFragment.setKeywordsInList(listMap);
                    });
                    Log.d(TAG, "size of list: " + places.size());
                }
            } catch (Exception e) {
                Log.d("Exception parsing", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            Log.d("Map", "list size: " + list.size());

            for (int i = 0; i < list.size(); i++) {

                HashMap<String, String> hmPlace = list.get(i);

                double lat = Double.parseDouble(hmPlace.get("lat"));

                double lng = Double.parseDouble(hmPlace.get("lng"));

                String name = hmPlace.get("place_name");

                Log.d("Map", "place: " + name + " lat: " + String.valueOf(lat) + " lng: " + String.valueOf(lng));


            }
        }
    }

    public class Place_JSON {

        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                jPlaces = jObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return getPlaces(jPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            for (int i = 0; i < placesCount; i++) {
                try {

                    place = getPlace((JSONObject) jPlaces.get(i));
                    placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        /**
         * Parsing the Place JSON object
         */
        public HashMap<String, String> getPlace(JSONObject jPlace) {

            HashMap<String, String> place = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                // Extracting Place name, if available
                if (!jPlace.isNull("name")) {
                    placeName = jPlace.getString("name");
                }

                // Extracting Place Vicinity, if available
                if (!jPlace.isNull("vicinity")) {
                    vicinity = jPlace.getString("vicinity");
                }

                latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jPlace.getString("reference");

                place.put("place_name", placeName);
                place.put("vicinity", vicinity);
                place.put("lat", latitude);
                place.put("lng", longitude);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }
}
