package com.cdelg4do.madridguide.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.fragment.ShopListFragment;
import com.cdelg4do.madridguide.manager.db.ShopDAO;
import com.cdelg4do.madridguide.model.Shop;
import com.cdelg4do.madridguide.model.Shops;
import com.cdelg4do.madridguide.navigator.Navigator;
import com.cdelg4do.madridguide.view.OnElementClickedListener;

import java.util.List;

/**
 * This class represents the screen showing the shop list.
 */
public class ShopsActivity extends AppCompatActivity {

    private ShopListFragment shopListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        // Get the ShopList fragment, and pass a Shops object to it
        shopListFragment = (ShopListFragment) getSupportFragmentManager().findFragmentById(R.id.activity_shops_fragment_shops);

        loadShopsFromDatabase();

        // Add an anonymous OnElementClickedListener to the fragment,
        // that navigates to the Shop Detail activity when a cell is clicked
        shopListFragment.setOnElementClickedListener(new OnElementClickedListener<Shop>() {

            @Override
            public void onElementClicked(Shop shop, int position) {
                Navigator.navigateFromShopsActivityToShopDetailActivity(shop, ShopsActivity.this);
            }
        });
    }


    // Loads all the shops from the database, and then pass them to the fragment
    private void loadShopsFromDatabase() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // The query is performed on background
                final List<Shop> shopList = new ShopDAO( getBaseContext() ).query();

                // Once the shop list has been retrieved from the database,
                // get back to the main thread and update the fragment UI with the data
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        shopListFragment.setShopsAndUpdateFragmentUI( Shops.newInstance(shopList) );
                    }
                });
            }
        }).start();


    }
}
