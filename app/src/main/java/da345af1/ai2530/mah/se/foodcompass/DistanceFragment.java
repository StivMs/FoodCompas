package da345af1.ai2530.mah.se.foodcompass;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



/**
 * A simple {@link Fragment} subclass.
 */
public class DistanceFragment extends Fragment {
    private TextView textView;
    private ImageView imageView;
    private Button btnDistanceNext;
    FragmentController controller;


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
        textView = view.findViewById( R.id.textView );
        imageView = view.findViewById( R.id.imageView );
        btnDistanceNext = view.findViewById( R.id.btnDistanceNext );
        btnDistanceNext.setOnClickListener(new ButtonListener() );

    }

    public void setController(FragmentController fragmentController) {
        this.controller = fragmentController;
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Save value from slider
            // Change fragment
            controller.fragmentOption("foodChoiceFragment");

        }
    }
}
