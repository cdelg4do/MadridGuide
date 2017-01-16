package com.cdelg4do.madridguide.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.view.OnElementClickedListener;
import com.cdelg4do.madridguide.view.ShopRowViewHolder;

/**
 * This class is the adapter to represent Shop objects in a RecyclerView.
 */
public class ShopsAdapter extends RecyclerView.Adapter<ShopRowViewHolder> {

    private Shops shops;
    private LayoutInflater inflater;
    private OnElementClickedListener<Shop> listener;


    public ShopsAdapter(Shops shops, Context context) {

        this.shops = shops;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnElementClickedListener(@NonNull final OnElementClickedListener<Shop> listener) {
        this.listener = listener;
    }

    // Called to set the total count of items to show in the RecyclerView
    @Override
    public int getItemCount() {
        return (int) shops.size();
    }

    // Called when creating the rows/cells of the RecyclerView
    // (creates a new holder for each)
    @Override
    public ShopRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_shop, parent, false);
        return new ShopRowViewHolder(view);
    }

    // Called when there is need to populate with data a recycled row/cell
    @Override
    public void onBindViewHolder(ShopRowViewHolder holder, final int position) {

        final Shop shop = shops.get(position);
        holder.syncViewFromModel(shop);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (ShopsAdapter.this.listener != null)
                    listener.onElementClicked(shop,position);
            }
        });
    }
}
