package io.github.bananapp.httone.networking;

import java.util.List;

import io.github.bananapp.httone.model.Place;
import io.github.bananapp.httone.model.UserAccount;
import io.github.bananapp.httone.model.UserInfo;
import io.github.bananapp.httone.model.UserMessage;
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

    @GET("/users/status/")
    public void getUserInfos(Callback<List<UserInfo>> callback);

    @POST("/users/{username}/notify/")
    public void notifyMessage(@Path("username") String destUserName, @Body UserMessage message,
            @Header("X-Httone-Authentication") String userName, Callback<Void> callback);

    @PUT("/places/{id}")
    public void notifyPlace(@Path("id") String placeId,
            @Header("X-Httone-Authentication") String userName, Callback<Void> callback);

    @POST("/users/")
    public void registerAccount(@Body UserAccount account, Callback<Void> callback);
}
