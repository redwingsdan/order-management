package com.aventuracctv.ordermangement.net;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.aventuracctv.ordermangement.data.BuildItem;
import com.aventuracctv.ordermangement.data.BuildItemDeserializer;
import com.aventuracctv.ordermangement.data.LoginDeserializer;
import com.aventuracctv.ordermangement.data.RegisterDeserializer;
import com.aventuracctv.ordermangement.data.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.aventuracctv.ordermangement.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Api requests
 */
public class ApiRequests
{

    /**
     * Returns a dummy object
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */
    public static GsonGetRequest<BuildItem> getBuildItem
    (
            @NonNull final Response.Listener<BuildItem> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    )
    {

        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(BuildItem.class, new BuildItemDeserializer())
                .create();

        final String url = BuildConfig.apiDomainName + "/v2/5727ad9e1200002f07c059f7";
        //final String url = "http://192.168.4.28/v2/5727ad9e1200002f07c059f7";


        Map<String, String> headers = new HashMap<String,String>();
        headers.put("Authorization", params[0]);

        return new GsonGetRequest<>
                (
                        url,
                        headers,
                        new TypeToken<BuildItem>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }

    /**
     * Returns a dummy object's array
     *
     * @param listener is the listener for the correct answer
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonGetRequest}
     */
    public static GsonGetRequest<ArrayList<BuildItem>> getBuildItemArray
    (
            @NonNull final Response.Listener<ArrayList<BuildItem>> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    )
    {

        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(BuildItem.class, new BuildItemDeserializer())
                .create();

        //final String url = "http://192.168.4.28/order_management/v1/order";
        final String url = BuildConfig.apiDomainName + "/order_management/v1/order";

        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", params[0]);
        headers.put("content-type", "application/x-www-form-urlencoded");

        return new GsonGetRequest<>
                (
                        url,
                        headers,
                        new TypeToken<BuildItem>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );
    }


    /**
     * An example call (not used in this app example) to demonstrate how to do a Volley POST call
     * and parse the response with Gson.
     *
     * @param listener is the listener for the success response
     * @param errorListener is the listener for the error response
     *
     * @return {@link GsonPostRequest}
     */
    public static GsonPostRequest registerAccount
    (
            @NonNull final Response.Listener<User> listener,
            @NonNull final Response.ErrorListener errorListener,
            String... params
    )
    {

        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(User.class, new RegisterDeserializer())
                .create();
        //final String url = "http://192.168.4.28/order_management/v1/register";
        final String url = BuildConfig.apiDomainName + "/order_management/v1/register";

        Map<String, String> headers = new HashMap<String,String>();
        //headers.put("Authorization", api);

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", params[0]);
        jsonObject.addProperty("password", params[1]);
        jsonObject.addProperty("email", params[2]);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest<>
                (
                        url,
                        headers,
                        jsonObject.toString(),
                        new TypeToken<User>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );

        gsonPostRequest.setShouldCache(false);

        return gsonPostRequest;
    }

    public static GsonPostRequest<User> login
            (
                    @NonNull final Response.Listener<User> listener,
                    @NonNull final Response.ErrorListener errorListener,
                    String... params
            )
    {

        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(User.class, new LoginDeserializer())
                .create();

        //final String url = "http://192.168.4.28/order_management/v1/login";
        final String url = BuildConfig.apiDomainName + "/order_management/v1/login";

        Map<String, String> headers = new HashMap<String,String>();

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", params[0]);
        jsonObject.addProperty("password", params[1]);

        final GsonPostRequest gsonPostRequest = new GsonPostRequest<>
                (
                        url,
                        headers,
                        jsonObject.toString(),
                        new TypeToken<User>() {}.getType(),
                        gson,
                        listener,
                        errorListener
                );

        gsonPostRequest.setShouldCache(false);

        return gsonPostRequest;
    }
}
