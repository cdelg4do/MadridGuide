package com.cdelg4do.madridguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.manager.image.ImageCacheManager;
import com.cdelg4do.madridguide.model.Experience;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Callback;

import static com.cdelg4do.madridguide.util.Constants.ERROR_SHOP_LOGO_ID;


/**
 * This class is the adapter to generate customized Info Windows for the map's activity markers.
 */
public class ExperienceInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context context;
    private final View infoWindowContents;


    public ExperienceInfoWindowAdapter(Context context){

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        infoWindowContents = inflater.inflate(R.layout.marker_activity_info_window, null);
    }


    // Returns a View that will be used as a default for the entire Info Window.
    // If this method returns null, then getInfoContents() will be called.
    @Override
    public View getInfoWindow(final Marker marker) {
        return null;
    }


    // Returns a view with the customized contents of the Info Window
    // (but still keeping the default info window frame and background, if any).
    // If this method also returns null, then the default info window will be used.
    @Override
    public View getInfoContents(final Marker marker) {

        Experience experience = (Experience) marker.getTag();

        final ImageView markerShopLogo = (ImageView) infoWindowContents.findViewById(R.id.marker_activity_logo);
        final TextView markerShopName = (TextView) infoWindowContents.findViewById(R.id.marker_activity_name);

        markerShopName.setText( experience.getName() );

        // The callback is necessary to re-show the info window once the image loads successfully
        // (because the Info Window is not a live view, it is rendered as an image)
        ImageCacheManager.getInstance(context).loadCachedImage(
                markerShopLogo,
                experience.getLogoImgUrl(),
                ERROR_SHOP_LOGO_ID,     // the error resource id is ignored in the info windows,
                ERROR_SHOP_LOGO_ID,     // if the image load fails, the placeholder will remain
                new Callback() {

                    @Override
                    public void onSuccess() {
                        refreshInfoWindow(marker);
                    }

                    // refreshInfoWindow(marker) SHOULD NOT be called in case of error
                    // (as it calls again getInfoContents() into an infinite loop)
                    @Override
                    public void onError() {
                        // Do nothing
                    }
                });

        return infoWindowContents;
    }


    // Auxiliary methods:

    private void refreshInfoWindow(Marker marker) {

        if ( marker.isInfoWindowShown() ) {
            marker.hideInfoWindow();
            marker.showInfoWindow();
        }
    }

}