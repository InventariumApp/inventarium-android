package com.inventariumapp.inventarium.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inventariumapp.inventarium.R;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import java.io.InputStream;

public class ItemDetail extends AppCompatActivity {

    private TextView itemName;
    private ImageView itemImage;
    private WebView webView;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        itemImage = (ImageView) findViewById(R.id.item_detail_product_image);
        itemName = (TextView) findViewById(R.id.item_detail_product_name);
        webView = (WebView) findViewById(R.id.item_detail_web_view);
        name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String img = getIntent().getStringExtra("img");
        itemName.setText(name);

        new DownloadImageTask(itemImage).execute(img);


    }

    public void purchaseItem(View v){

        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
        String amazonUrl = "https://www.amazon.com/s/ref=nb_sb_noss_1?url=search-alias%3Daps&field-keywords=";
        String myItem = name.replaceAll(" ", "+");
        intent.putExtra("url", amazonUrl + myItem);
        startActivity(intent);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
