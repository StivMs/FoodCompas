package da345af1.ai2530.mah.se.foodcompass.Remote;

import da345af1.ai2530.mah.se.foodcompass.Model.MyPlaces;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {
    @GET
    Call<MyPlaces> getNearbyPlaces(@Url String url);

    @GET
    Call<MyPlaces> getDetailsPlace(@Url String url);
}
