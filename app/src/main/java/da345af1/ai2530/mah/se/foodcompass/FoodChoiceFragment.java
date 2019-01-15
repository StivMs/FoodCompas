package da345af1.ai2530.mah.se.foodcompass;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodChoiceFragment extends Fragment {
    private TextView tvFoodQuestion;
    private TextView tvPizza;
    private TextView tvSalad;
    private TextView tvSushi;
    private ImageView ivPizza;
    private ImageView ivSalad;
    private ImageView ivSushi;
    private Button btnFoodNext;
    private RadioButton rbSalad;
    private RadioButton rbPizza;
    private RadioButton rbSushi;
    FragmentController controller;
    private boolean saladChosen;
    private boolean pizzaChosen;
    private boolean sushiChosen;
    private String food;

    public FoodChoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_choice, container, false);
        initComponent(view);
        return view;
    }

    private void initComponent(View view) {
        tvFoodQuestion = view.findViewById(R.id.tvFoodQuestion);
        tvPizza = view.findViewById(R.id.tvPizza);
        tvSalad = view.findViewById(R.id.tvSalad);
        tvSushi = view.findViewById(R.id.tvSushi);
        ivPizza = view.findViewById(R.id.ivPizza);
        ivSalad = view.findViewById(R.id.ivSalad);
        ivSushi = view.findViewById(R.id.ivSushi);
        btnFoodNext = view.findViewById(R.id.btnFoodNext);
        rbSalad = view.findViewById(R.id.rbPizza);
        rbPizza = view.findViewById(R.id.rbPizza);
        rbSushi = view.findViewById(R.id.rbSushi);

        btnFoodNext.setOnClickListener(new ButtonListener());
        rbSalad.setOnClickListener(new ButtonListener());
        rbPizza.setOnClickListener(new ButtonListener());
        rbSushi.setOnClickListener(new ButtonListener());
    }

    public void setController(FragmentController fragmentController) {
        this.controller = fragmentController;
    }


    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.rbPizza:
                    //Save the option
                    pizzaChosen = true;
                    break;
                case R.id.rbSalad:
                    //Save the option
                    saladChosen = true;
                    break;
                case R.id.rbSushi:
                    // Save
                    sushiChosen = true;
                    break;
                case R.id.btnFoodNext:
                    controller.fragmentOption("keywordFragment");
                    break;
            }
        }
    }

    public String chosenFood() {
        food = "";

        if (pizzaChosen)
            food = "pizza";
        else if (saladChosen)
            food = "salad";
        else if (sushiChosen)
            food = "sushi";

        return food;
    }
}
