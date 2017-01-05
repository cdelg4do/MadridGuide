package com.cdelg4do.madridguide.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.model.Shop;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

/**
 * This class represents the ViewHolder to present a Shop in a RecyclerView.
 */
public class ShopRowViewHolder extends RecyclerView.ViewHolder {

    private TextView nameTextView;
    private ImageView logoImageView;
    private WeakReference<Context> context;

    public ShopRowViewHolder(View rowShop) {
        super(rowShop);

        nameTextView = (TextView) rowShop.findViewById( R.id.row_shop_name );
        logoImageView = (ImageView) rowShop.findViewById( R.id.row_shop_logo );
        context = new WeakReference<Context>( rowShop.getContext() );
    }


    public void syncViewFromModel(final @NonNull Shop shop) {

        if (shop == null)
            return;

        nameTextView.setText( shop.getName() );

        Picasso.with(context.get())
                .load( shop.getLogoImgUrl() )
                .placeholder(android.R.drawable.ic_menu_camera)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                .into(logoImageView);
    }

}
