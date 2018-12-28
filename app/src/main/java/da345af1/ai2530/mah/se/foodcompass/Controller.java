package da345af1.ai2530.mah.se.foodcompass;


public class Controller {

    private APItestFragment testAPIFrag;
    private MainActivity mainActivity;
    private GoogleAPI PlacesAPI;

    public Controller(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        testAPIFrag = new APItestFragment();
        mainActivity.setFragment(testAPIFrag, false);
        testAPIFrag.setController(this);

    }


    public void btnAPI() {
        PlacesAPI = new GoogleAPI(this, testAPIFrag);
        PlacesAPI.run();
    }
}
