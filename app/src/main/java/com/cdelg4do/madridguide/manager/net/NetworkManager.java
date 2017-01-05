package com.cdelg4do.madridguide.manager.net;

import android.content.Context;
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


/**
 * This class manages the network data download.
 */
public class NetworkManager {

    public interface NetworkShopsRequestListener {
        void onNetworkShopsRequestFinished(List<ShopsResponse.ShopEntity> shopEntities);
        void onNetworkShopsRequestError(Exception e);
    }

    WeakReference<Context> context;


    public NetworkManager(Context context) {

        this.context = new WeakReference<Context>(context);
    }


    // Request a string response from the shops URL
    public void getShopsFromServer(final @NonNull NetworkShopsRequestListener listener) {

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
                        listener.onNetworkShopsRequestFinished(shopEntities);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onNetworkShopsRequestError(error);
                    }
                }
        );

        queue.add(shopsRequest);
    }


    // Auxiliary methods:

    private List<ShopsResponse.ShopEntity> parseShopsRequestResponse(String response) {

        List<ShopsResponse.ShopEntity> result = null;

        try {
            Reader reader = new StringReader(response);

            // Use Gson to parse the JSON response reader to a ShopsResponse object
            Gson gson = new GsonBuilder().create();
            ShopsResponse shopResponse = gson.fromJson(reader, ShopsResponse.class);

            result = shopResponse.getResult();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }

        return result;
    }

}
