package com.cdelg4do.madridguide.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.manager.image.ImageCacheManager;
import com.cdelg4do.madridguide.model.Shop;

import java.lang.ref.WeakReference;

import static com.cdelg4do.madridguide.util.Constants.DEFAULT_SHOP_IMAGE_ID;
import static com.cdelg4do.madridguide.util.Constants.DEFAULT_SHOP_LOGO_ID;
import static com.cdelg4do.madridguide.util.Constants.ERROR_SHOP_IMAGE_ID;
import static com.cdelg4do.madridguide.util.Constants.ERROR_SHOP_LOGO_ID;

/**
 * This class represents the ViewHolder to present a Shop in a RecyclerView.
 */
public class ShopRowViewHolder extends RecyclerView.ViewHolder {

    private WeakReference<Context> context;
    private TextView nameTextView;
    private ImageView logoImageView;
    private ImageView backgroundImageView;

    public ShopRowViewHolder(View rowShop) {
        super(rowShop);

        context = new WeakReference<Context>( rowShop.getContext() );

        nameTextView = (TextView) rowShop.findViewById( R.id.row_shop_name );
        logoImageView = (ImageView) rowShop.findViewById( R.id.row_shop_logo );
        backgroundImageView = (ImageView) rowShop.findViewById( R.id.row_shop_background_image );
    }


    public void syncViewFromModel(final @NonNull Shop shop) {

        if (shop == null)
            return;

        nameTextView.setText( shop.getName() );

        ImageCacheManager imageMngr = ImageCacheManager.getInstance(context.get());

        imageMngr.loadCachedImage(
                logoImageView,
                shop.getLogoImgUrl(),
                DEFAULT_SHOP_LOGO_ID,
                ERROR_SHOP_LOGO_ID);

        imageMngr.loadCachedImage(
                backgroundImageView,
                shop.getImageUrl(),
                DEFAULT_SHOP_IMAGE_ID,
                ERROR_SHOP_IMAGE_ID);
    }

}
