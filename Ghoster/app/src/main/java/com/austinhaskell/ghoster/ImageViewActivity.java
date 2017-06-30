package com.austinhaskell.ghoster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ImageViewActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_view);

        getIntent().setExtrasClassLoader(UserPost.class.getClassLoader());
        UserPost post = (UserPost) getIntent().getParcelableExtra("POST");

        Log.d("USERPOST",post.toString());

        Glide.with(ImageViewActivity.this)
                .load(post.getUrl())
                .listener(new RequestListener<Uri, GlideDrawable>()
                {
            @Override
            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                findViewById(R.id.image_view_progress_bar).setVisibility(View.INVISIBLE);
                return false;
            }
        }).centerCrop()
                .into((ImageView)findViewById(R.id.fullscreen_content));

        Log.d("---- ANDROID ----", post.getUrl().toString());

        findViewById(R.id.img_view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((TextView)findViewById(R.id.title_view_text)).setText(post.getTitle());


    }


}
