package com.cdelg4do.madridguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.manager.image.ImageCacheManager;
import com.cdelg4do.madridguide.model.Shop;
import com.squareup.picasso.Callback;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.cdelg4do.madridguide.util.Constants.DEFAULT_SHOP_IMAGE_ID;
import static com.cdelg4do.madridguide.util.Constants.DEFAULT_SHOP_LOGO_ID;
import static com.cdelg4do.madridguide.util.Constants.ERROR_SHOP_IMAGE_ID;
import static com.cdelg4do.madridguide.util.Constants.ERROR_SHOP_LOGO_ID;
import static com.cdelg4do.madridguide.util.Constants.INTENT_KEY_DETAIL_SHOP;

/**
 * This class represents the screen showing the details of a shop.
 */
public class ShopDetailActivity extends AppCompatActivity {

    private Shop shop;

    @BindView(R.id.activity_shop_detail_shop_name_text) TextView nameText;
    @BindView(R.id.activity_shop_detail_logo_image) ImageView logoImage;
    @BindView(R.id.activity_shop_detail_shop_image) ImageView shopImage;
    @BindView(R.id.activity_shop_detail_url_text) TextView urlText;
    @BindView(R.id.activity_shop_detail_openingHours_text) TextView openingHoursText;
    @BindView(R.id.activity_shop_detail_address_text) TextView addressText;
    @BindView(R.id.activity_shop_detail_map_button) ToggleButton mapButton;
    @BindView(R.id.activity_shop_detail_map_frame) FrameLayout mapFrame;
    @BindView(R.id.activity_shop_detail_map_image) ImageView mapImage;
    @BindView(R.id.activity_shop_detail_description_text) TextView descriptionText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        setTitle(R.string.activity_shop_detail_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Bind all views of this activity (with ButterKnife)
        ButterKnife.bind(this);

        // Get the Shop object passed to the activity and show it
        Intent intent = getIntent();
        shop = (Shop) intent.getSerializableExtra(INTENT_KEY_DETAIL_SHOP);
        syncViewFromModel();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Auxiliary methods:

    private void syncViewFromModel() {

        if (shop != null) {

            nameText.setText( shop.getName() );
            urlText.setText( shop.getUrl() );
            openingHoursText.setText( shop.getLocalizedOpeningHours() );
            addressText.setText( shop.getAddress() );
            descriptionText.setText( shop.getLocalizedDescription() );

            mapButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked)
                        mapFrame.setVisibility(VISIBLE);
                    else
                        mapFrame.setVisibility(GONE);
                }
            });

            ImageCacheManager.getInstance(this).loadCachedImage(
                    logoImage,
                    shop.getLogoImgUrl(),
                    DEFAULT_SHOP_LOGO_ID,
                    ERROR_SHOP_LOGO_ID);

            ImageCacheManager.getInstance(this).loadCachedImage(
                    shopImage,
                    shop.getImageUrl(),
                    DEFAULT_SHOP_IMAGE_ID,
                    ERROR_SHOP_IMAGE_ID);

            ImageCacheManager.getInstance(this).loadCachedImage(
                    mapImage,
                    shop.getMapUrl(),
                    DEFAULT_SHOP_LOGO_ID,
                    ERROR_SHOP_LOGO_ID,
                    new Callback() {

                        @Override
                        public void onSuccess() {
                            // Make the ImageView zoomable
                            new PhotoViewAttacher(mapImage);
                        }

                        @Override
                        public void onError() {
                        }
                    });

        }
    }

}
