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
public class FirstPageFragment extends Fragment {
    private TextView tvAppName, tvWelcomeText, tvSlogan;
    private ImageView imgLogo;
    private Button btnStart, btnSettings;
    FragmentController controller;


    public FirstPageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_page, container, false);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {
        tvAppName = view.findViewById(R.id.tvAppName);
        tvWelcomeText = view.findViewById(R.id.tvWelcomeText);
        imgLogo = view.findViewById(R.id.imgLogo);
        tvSlogan = view.findViewById(R.id.tvSlogan);
        btnStart = view.findViewById(R.id.btnStart);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnStart.setOnClickListener(new ButtonListener());
    }

    public void setController(FragmentController fragmentController) {
        this.controller = fragmentController;
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnStart:
                    // Do something
                    controller.fragmentOption("distanceFragment");
                    //testar github

                    break;
                case R.id.btnSettings:
                    // Edit settings UI
                    break;
            }

        }
    }
}
