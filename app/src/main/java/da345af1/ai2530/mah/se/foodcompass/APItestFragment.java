package da345af1.ai2530.mah.se.foodcompass;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class APItestFragment extends Fragment {
    private TextView tv;
    private Button btn;
    private Controller controller;


    public APItestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_apitest, container, false);
        initComponent(view);
        registerListeners();
        return view;
    }

    private void initComponent(View view) {
        tv = view.findViewById(R.id.tvValues);
        btn = view.findViewById(R.id.btnPlaces);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private void registerListeners() {
        ButtonListener listener = new ButtonListener();
        btn.setOnClickListener(listener);
    }

    private class ButtonListener implements View.OnClickListener {
        public void onClick(View view) {
            if (view == btn) {
                controller.btnAPI();
            }
        }
    }


}
