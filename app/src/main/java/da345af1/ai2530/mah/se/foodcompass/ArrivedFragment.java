package da345af1.ai2530.mah.se.foodcompass;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArrivedFragment extends Fragment {
    FragmentController controller;
    TextView tvArrivedText;
    Button btnFinish;
    public ArrivedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_arrived, container, false);
            tvArrivedText = view.findViewById(R.id.tvArrivedDest);
            btnFinish = view.findViewById(R.id.btnFinishDest);
        return view;

    }

    public void setController(FragmentController fragmentController) {
        this.controller = fragmentController;
    }
}
