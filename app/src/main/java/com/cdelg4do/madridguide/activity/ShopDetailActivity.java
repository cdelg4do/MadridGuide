package com.cdelg4do.madridguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdelg4do.madridguide.R;
import com.cdelg4do.madridguide.model.Shop;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cdelg4do.madridguide.util.Constants.DEFAULT_SHOP_LOGO_ID;
import static com.cdelg4do.madridguide.util.Constants.INTENT_KEY_DETAIL_SHOP;

/**
 * This class represents the screen showing the details of a shop.
 */
public class ShopDetailActivity extends AppCompatActivity {

    private Shop shop;

    @BindView(R.id.activity_shop_detail_logo_image)
    ImageView logoImage;

    @BindView(R.id.activity_shop_detail_shop_name_text)
    TextView nameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        // Bind all views of this activity (with Butterknife)
        ButterKnife.bind(this);

        // Get the Shop object passed to the activity
        Intent intent = getIntent();
        shop = (Shop) intent.getSerializableExtra(INTENT_KEY_DETAIL_SHOP);

        if (shop != null) {

            nameText.setText( shop.getName() );
            Picasso.with(this).
                    load( shop.getLogoImgUrl() ).
                    placeholder(DEFAULT_SHOP_LOGO_ID).
                    into(logoImage);
        }
    }
}
