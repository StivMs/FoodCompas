package da345af1.ai2530.mah.se.foodcompass;


import android.support.v4.app.Fragment;
import android.util.Log;

public class FragmentController {
    private static final String TAG = "FragmentController";
    private MainActivity ma;
    private CompassFragment compassFragment;
    private DistanceFragment distanceFragment;
    private FirstPageFragment firstPageFragment;
    private FoodChoiceFragment foodChoiceFragment;


    public FragmentController(MainActivity ma) {
        this.ma = ma;
        initCompass();
        initDistance();
        initFirst();
        initFood();
        fragmentOption("firstPageFragment");
        Log.d(TAG, "Starting...");

    }


    private void initCompass() {
        compassFragment = (CompassFragment) ma.getFragment("compassFragment");
        if (compassFragment == null) {
            compassFragment = new CompassFragment();
            compassFragment.setMainActivity(ma);
        }
        Log.d(TAG, "Setting compass fragment");
        compassFragment.setController(this);
    }

    private void initDistance() {
        distanceFragment = (DistanceFragment) ma.getFragment("distanceFragment");
        if (distanceFragment == null) {
            distanceFragment = new DistanceFragment();
        }
        Log.d(TAG, "Setting distance fragment");
        distanceFragment.setController(this);
    }

    private void initFirst() {
        firstPageFragment = (FirstPageFragment) ma.getFragment("firstPageFragment");
        if (firstPageFragment == null) {
            firstPageFragment = new FirstPageFragment();
        }
        Log.d(TAG, "Setting first fragment");
        firstPageFragment.setController(this);
    }

    private void initFood() {
        foodChoiceFragment = (FoodChoiceFragment) ma.getFragment("foodChoiceFragment");
        if (foodChoiceFragment == null) {
            foodChoiceFragment = new FoodChoiceFragment();
        }
        Log.d(TAG, "Setting food fragment");
        foodChoiceFragment.setController(this);
    }


    public void fragmentOption(String tag) {
        Log.d(TAG, "FRAGMENT OPTION SETTING");
        switch (tag) {
            case "compassFragment":
                setFragment(compassFragment, "compassFragment");
                break;
            case "distanceFragment":
                setFragment(distanceFragment, "distanceFragment");
                break;
            case "firstPageFragment":
                setFragment(firstPageFragment, "firstPageFragment");
                break;
            case "foodChoiceFragment":
                setFragment(foodChoiceFragment, "foodChoiceFragment");
                break;
        }
    }

    public void setFragment(Fragment fragment, String tag) {
        Log.d(TAG, "FRAGMENT SETTING");
        switch (tag) {
            case "compassFragment":
                ma.setFragment(fragment);
                compassFragment.setChosenFood(foodChoiceFragment.chosenFood());
                compassFragment.setDistance(distanceFragment.getDistance());
                break;
            case "distanceFragment":
                ma.setFragment(fragment);
                break;
            case "firstPageFragment":
                ma.setFragment(fragment);
                break;
            case "foodChoiceFragment":
                ma.setFragment(fragment);
                break;

        }
    }

}