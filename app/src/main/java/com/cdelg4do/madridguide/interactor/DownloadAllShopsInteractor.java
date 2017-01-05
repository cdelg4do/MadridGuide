package com.cdelg4do.madridguide.interactor;


import android.content.Context;

import com.cdelg4do.madridguide.manager.net.NetworkManager;
import com.cdelg4do.madridguide.manager.net.ShopsResponse;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.model.mapper.ShopEntityShopMapper;

import java.util.List;

/**
 * This class is an interactor in charge of:
 *
 * - First (in background): downloads the shop list from the remote server and builds a new Shops object with them.
 *
 * - Second (in the main thread): pass the Shops object to the received GetAllShopsInteractorListener.
 */
public class DownloadAllShopsInteractor {

    // This interface describes the behavior of a listener waiting for the completion of the async operation
    public interface GetAllShopsInteractorListener {

        void onGetAllShopsFinished(Shops shops);
        void onGetAllShopsFailed(Exception e);
    }

    /**
     * Connects to the remote server and gets the shop list, then builds a Shops object with the data.
     *
     * @param context   context for the operation.
     * @param listener  listener that will process the result of the operation.
     */
    public void execute(final Context context, final GetAllShopsInteractorListener listener) {

        if (listener == null)
            return;

        NetworkManager networkMgr = new NetworkManager(context);
        networkMgr.getShopsFromServer(new NetworkManager.NetworkShopsRequestListener() {

            @Override
            public void onNetworkShopsRequestFinished(List<ShopsResponse.ShopEntity> shopEntities) {

                List<Shop> shopList = new ShopEntityShopMapper().map(shopEntities);
                listener.onGetAllShopsFinished( Shops.buildShopsFromList(shopList) );
            }

            @Override
            public void onNetworkShopsRequestError(Exception e) {
                listener.onGetAllShopsFailed(e);
            }
        });
    }

}
