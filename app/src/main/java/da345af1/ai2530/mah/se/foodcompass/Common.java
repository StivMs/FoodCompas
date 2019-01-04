package da345af1.ai2530.mah.se.foodcompass;


import da345af1.ai2530.mah.se.foodcompass.Model.Results;
import da345af1.ai2530.mah.se.foodcompass.Remote.IGoogleAPIService;
import da345af1.ai2530.mah.se.foodcompass.Remote.RetrofitClient;

public class Common {

    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static IGoogleAPIService getGoogleAPIService() {
        return RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService.class);
    }
}
