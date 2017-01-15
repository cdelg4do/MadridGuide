package com.cdelg4do.madridguide.manager.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cdelg4do.madridguide.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;


/**
 * This class manages the network data download.
 */
public class NetworkManager {

    public static enum ConnectionType {

        NONE,
        WIFI,
        OTHER   // mobile, wimax, vpn, etc.
    }

    // This interface describes the behavior of a listener waiting for the completion of the network operation
    // (object result should be casted to the appropriate object, depending on the request type)
    public interface NetworkRequestListener {

        void onNetworkRequestSuccess(Object result);
        void onNetworkRequestFail(Exception e);
    }

    private WeakReference<Context> context;
    private ConnectivityManager connectivityMgr;


    public NetworkManager(Context context) {

        this.context = new WeakReference<Context>(context);
    }


    // Request a string response from the shops URL
    public void getShopsFromServer(final @NonNull NetworkRequestListener listener) {

        if (listener == null)
            return;

        RequestQueue queue = Volley.newRequestQueue( context.get() );
        String requestUrl = context.get().getString(R.string.shops_url);

        StringRequest shopsRequest = new StringRequest(

                requestUrl,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<ShopsResponse.ShopEntity> shopEntities = parseShopsRequestResponse(response);
                        listener.onNetworkRequestSuccess(shopEntities);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onNetworkRequestFail(error);
                    }
                }
        );

        queue.add(shopsRequest);
    }


    // Request a string response from the activities URL
    public void getActivitiesFromServer(final @NonNull NetworkRequestListener listener) {

        if (listener == null)
            return;

        RequestQueue queue = Volley.newRequestQueue( context.get() );
        String requestUrl = context.get().getString(R.string.activities_url);

        StringRequest activitiesRequest = new StringRequest(

                requestUrl,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<ExperiencesResponse.ExperienceEntity> experienceEntities = parseActivitiesRequestResponse(response);
                        listener.onNetworkRequestSuccess(experienceEntities);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onNetworkRequestFail(error);
                    }
                }
        );

        queue.add(activitiesRequest);
    }


    // Determines what kind of internet connection the device has (if any)
    public static ConnectionType getInternetConnectionType(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();

        if (activeNetworkInfo.isConnectedOrConnecting()) {

            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return ConnectionType.WIFI;
            else
                return ConnectionType.OTHER;
        }
        else {
            return ConnectionType.NONE;
        }
    }


    // Auxiliary methods:

    private List<ShopsResponse.ShopEntity> parseShopsRequestResponse(String response) {

        List<ShopsResponse.ShopEntity> result = null;

        try {
            Reader reader = new StringReader(response);

            // Use Gson to parse the JSON response reader to a ShopsResponse object
            Gson gson = new GsonBuilder().create();
            ShopsResponse shopsResponse = gson.fromJson(reader, ShopsResponse.class);

            result = shopsResponse.getResult();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<ExperiencesResponse.ExperienceEntity> parseActivitiesRequestResponse(String response) {

        List<ExperiencesResponse.ExperienceEntity> result = null;

        try {
            Reader reader = new StringReader(response);

            // Use Gson to parse the JSON response reader to an ExperienceResponse object
            Gson gson = new GsonBuilder().create();
            ExperiencesResponse experiencesResponse = gson.fromJson(reader, ExperiencesResponse.class);

            result = experiencesResponse.getResult();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }

        return result;
    }

}
