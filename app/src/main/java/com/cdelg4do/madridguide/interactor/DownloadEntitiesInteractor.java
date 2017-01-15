package com.cdelg4do.madridguide.interactor;


import android.content.Context;

import com.cdelg4do.madridguide.manager.net.ExperiencesResponse.ExperienceEntity;
import com.cdelg4do.madridguide.manager.net.NetworkManager;
import com.cdelg4do.madridguide.manager.net.NetworkManager.NetworkRequestListener;
import com.cdelg4do.madridguide.manager.net.ShopsResponse.ShopEntity;
import com.cdelg4do.madridguide.model.Experience;
import com.cdelg4do.madridguide.model.Experiences;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.model.mapper.ExperienceEntityToExperienceMapper;
import com.cdelg4do.madridguide.model.mapper.ShopEntityToShopMapper;

import java.util.List;

/**
 * This class is an interactor in charge of:
 *
 * - First (in background): downloads the appropriate entity data from the remote server and builds a new model object with them.
 *
 * - Second (in the main thread): pass the model object to the received DownloadEntitiesInteractorListener.
 */
public class DownloadEntitiesInteractor {

    // Discriminators for all the entities available to download
    public static enum RemoteSource {

        SHOPS,
        ACTIVITIES
    }

    // This interface describes the behavior of a listener waiting for the completion of the async operation
    // (object downloadedEntities should be casted to the corresponding model object (Shops, Experiences, etc)
    public interface DownloadEntitiesInteractorListener {

        void onDownloadEntitiesSuccess(Object downloadedEntities);
        void onDownloadEntitiesFail(Exception e);
    }

    /**
     * Connects to the remote server and gets the requested entities, then builds an aggregate object with the data.
     *
     * @param context   context for the operation.
     * @param source    indicates the source to download data from.
     * @param listener  listener that will process the result of the operation.
     */
    public void execute(final Context context, RemoteSource source, final DownloadEntitiesInteractorListener listener) {

        if (listener == null)
            return;

        NetworkManager networkMgr = new NetworkManager(context);

        switch (source) {

            case SHOPS:

                networkMgr.getShopsFromServer(new NetworkRequestListener() {

                    @Override
                    public void onNetworkRequestSuccess(Object result) {

                        List<ShopEntity> shopEntities = (List<ShopEntity>) result;
                        List<Shop> shopList = new ShopEntityToShopMapper().map(shopEntities);

                        listener.onDownloadEntitiesSuccess( Shops.buildShopsFromList(shopList) );
                    }

                    @Override
                    public void onNetworkRequestFail(Exception e) {
                        listener.onDownloadEntitiesFail(e);
                    }
                });

                break;

            case ACTIVITIES:

                networkMgr.getActivitiesFromServer(new NetworkRequestListener() {

                    @Override
                    public void onNetworkRequestSuccess(Object result) {

                        List<ExperienceEntity> experienceEntities = (List<ExperienceEntity>) result;
                        List<Experience> experienceList = new ExperienceEntityToExperienceMapper().map(experienceEntities);

                        listener.onDownloadEntitiesSuccess( Experiences.buildExperiencesFromList(experienceList) );
                    }

                    @Override
                    public void onNetworkRequestFail(Exception e) {
                        listener.onDownloadEntitiesFail(e);
                    }
                });

                break;


            default:
                break;
        }




    }

}
