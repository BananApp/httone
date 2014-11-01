package io.github.bananapp.httone.networking;

import java.util.List;

import io.github.bananapp.httone.model.Place;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface ClientApi {

    public final static String ENDPOINT = "https://bananapp-httone.appspot.com/";

    @POST("/places")
    public void createPlace(@Body Place place, Callback<String> callback);

    @GET("/places")
    public void getPlaces(Callback<List<Place>> callback);

    @POST("/places/{id}")
    public void notifyPlace(@Path("id") String placeId, Callback<String> callback);
}
