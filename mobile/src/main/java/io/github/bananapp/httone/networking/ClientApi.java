package io.github.bananapp.httone.networking;

import java.util.List;

import io.github.bananapp.httone.model.Place;
import io.github.bananapp.httone.model.UserAccount;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

public interface ClientApi {

    public final static String ENDPOINT = "https://bananapp-httone.appspot.com";

    @POST("/places/")
    public void createPlace(@Body Place place, Callback<String> callback);

    @GET("/places/")
    public void getPlaces(Callback<List<Place>> callback);

    @PUT("/places/{id}")
    public void notifyPlace(@Path("id") String placeId,
            @Header("X-Httone-Authentication") String userName, Callback<Void> callback);

    @POST("/users/")
    public void registerAccount(@Body UserAccount account, Callback<Void> callback);
}
