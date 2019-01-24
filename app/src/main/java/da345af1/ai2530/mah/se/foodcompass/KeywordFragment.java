package da345af1.ai2530.mah.se.foodcompass;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class KeywordFragment extends Fragment {
    private static final String TAG = "KeywordFragment";
    private ListView listOptions;
    private TextView textViewSelected;
    private Button buttonContinue;

    private MainActivity ma;
    private FragmentController controller;

    private String selectedItem;
    private String[] restaurants;

    private LatLng target;
    private LatLng[] targets;
    FoodChoiceFragment foodChoiceFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyword, container, false);

        initializeComponents(view);
        registerListeners();

        return view;
    }

    public void setKeywordsInList(List<HashMap<String, String>> places) {
        restaurants = new String[places.size()];
        targets = new LatLng[places.size()];

        Log.d(TAG, "setKeywordsInList: " + places.size());

        for (int i = 0; i < places.size(); i++) {
            double lat = Double.parseDouble(places.get(i).get("lat"));
            double lng = Double.parseDouble(places.get(i).get("lng"));

            restaurants[i] = places.get(i).get("place_name");
            targets[i] = new LatLng(lat, lng);
            Log.d(TAG, "setKeywordsInList: " + places.get(i).get("place_name"));
        }

        listOptions.setAdapter(new ArrayAdapter<String>(ma,
                android.R.layout.simple_list_item_1, restaurants));
    }

    private void initializeComponents(View view) {
        listOptions = view.findViewById(R.id.listOptions);
        textViewSelected = view.findViewById(R.id.textViewSelected);
        buttonContinue = view.findViewById(R.id.buttonContinue);
    }

    private void registerListeners() {
        ListViewListener listListener = new ListViewListener();
        listOptions.setOnItemClickListener(listListener);

        ButtonListener listener = new ButtonListener();
        buttonContinue.setOnClickListener(listener);
    }

    public void setController(FragmentController controller) {
        this.controller = controller;
    }

    public void setMainActivity(MainActivity activity) {
        this.ma = activity;
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public LatLng getTarget() {
        return target;
    }

    public void setTarget(LatLng target) {
        this.target = target;
    }

    private class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(selectedItem!=null){
                controller.fragmentOption("compassFragment");
            }else{
                controller.initDialog("Please pick a restaurant from the list!");
            }

        }
    }

    private class ListViewListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (restaurants != null) {
                selectedItem = restaurants[position];
                target = targets[position];
                textViewSelected.setText("Selected: " + selectedItem);
            }
        }
    }
}
