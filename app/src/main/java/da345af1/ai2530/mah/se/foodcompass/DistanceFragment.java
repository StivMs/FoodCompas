package da345af1.ai2530.mah.se.foodcompass;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.warkiz.widget.IndicatorSeekBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DistanceFragment extends Fragment {
    private static final String TAG = "DistanceFragment";
    private TextView textView;
    private ImageView imageView;
    private Button btnDistanceNext;
    FragmentController controller;
    private IndicatorSeekBar slider;
    private int radius;


    public DistanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_distance, container, false);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {
        slider = view.findViewById(R.id.distSlider);
        slider.setMin(1);
        slider.setMax(2500);
        textView = view.findViewById(R.id.textViewSelected);
        imageView = view.findViewById(R.id.imageView);
        btnDistanceNext = view.findViewById(R.id.btnDistanceNext);
        btnDistanceNext.setOnClickListener(new ButtonListener());

    }


    public void setController(FragmentController fragmentController) {
        this.controller = fragmentController;
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            radius = slider.getProgress();
            Log.d(TAG, "onClick: IS THIS RADIUS LEL " + radius);
            controller.fragmentOption("foodChoiceFragment");
        }
    }

    public int getRadius() {
        return this.radius;
    }
}
