package com.cdelg4do.madridguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.model.Shop;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import static com.cdelg4do.madridguide.util.Constants.DEFAULT_SHOP_LOGO_ID;


/**
 * This class is the adapter to generate customized Info Windows for the map's shop markers.
 */
public class ShopInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context context;
    private final View myContentsView;

    public ShopInfoWindowAdapter(Context context){

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        myContentsView = inflater.inflate(R.layout.marker_shop_info_window, null);
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

        Shop shop = (Shop) marker.getTag();

        final ImageView markerShopLogo = (ImageView) myContentsView.findViewById(R.id.marker_shop_logo);
        final TextView markerShopName = (TextView) myContentsView.findViewById(R.id.marker_shop_name);

        markerShopName.setText( shop.getName() );

        Picasso.with(context).
                load(shop.getLogoImgUrl()).
                placeholder(DEFAULT_SHOP_LOGO_ID).
                into(markerShopLogo);

        // Note:
        //
        // The image will appear only if it loads into the ImageView immediately (i.e. if the image
        // is already cached). If the image is not cached, it will not be visible
        // because the Info Window will appear BEFORE the image is downloaded.
        //
        // This is because the Info Window is not a live view (it is rendered as an image), so
        // any subsequent changes to it will not be reflected.

        return myContentsView;
    }

}