package com.cdelg4do.madridguide.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.manager.image.ImageCacheManager;
import com.cdelg4do.madridguide.model.Experience;

import java.lang.ref.WeakReference;

import static com.cdelg4do.madridguide.util.Constants.DEFAULT_SHOP_IMAGE_ID;
import static com.cdelg4do.madridguide.util.Constants.DEFAULT_SHOP_LOGO_ID;
import static com.cdelg4do.madridguide.util.Constants.ERROR_SHOP_IMAGE_ID;
import static com.cdelg4do.madridguide.util.Constants.ERROR_SHOP_LOGO_ID;

/**
 * This class represents the ViewHolder to present an Experience (a.k.a Activity) in a RecyclerView.
 */
public class ActivityRowViewHolder extends RecyclerView.ViewHolder {

    private WeakReference<Context> context;
    private TextView nameTextView;
    private ImageView logoImageView;
    private ImageView backgroundImageView;

    public ActivityRowViewHolder(View rowActivity) {
        super(rowActivity);

        context = new WeakReference<Context>( rowActivity.getContext() );

        nameTextView = (TextView) rowActivity.findViewById( R.id.row_activity_name );
        logoImageView = (ImageView) rowActivity.findViewById( R.id.row_activity_logo );
        backgroundImageView = (ImageView) rowActivity.findViewById( R.id.row_activity_background_image );
    }


    public void syncViewFromModel(final @NonNull Experience experience) {

        if (experience == null)
            return;

        nameTextView.setText( experience.getName() );

        ImageCacheManager imageMgr = ImageCacheManager.getInstance(context.get());

        imageMgr.loadCachedImage(
                logoImageView,
                experience.getLogoImgUrl(),
                DEFAULT_SHOP_LOGO_ID,
                ERROR_SHOP_LOGO_ID);

        imageMgr.loadCachedImage(
                backgroundImageView,
                experience.getImageUrl(),
                DEFAULT_SHOP_IMAGE_ID,
                ERROR_SHOP_IMAGE_ID);
    }

}
