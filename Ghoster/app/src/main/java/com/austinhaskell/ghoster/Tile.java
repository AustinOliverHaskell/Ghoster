package com.austinhaskell.ghoster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by Austin on 5/2/2017.
 *
 * RecyclerView Adapter for the main RecyclerView
 *
 */

public class Tile extends RecyclerView.Adapter<Tile.ViewHolder>
{
    private Context context;
    private ArrayList<UserPost>images;

    public Tile(Context context, ArrayList<UserPost>images)
    {
        this.context = context;
        this.images = images;
    }

    @Override
    public Tile.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_layout, parent, false);

        ViewGroup.LayoutParams temp = view.getLayoutParams();

        int height = Resources.getSystem().getDisplayMetrics().heightPixels;

        temp.height = height/3;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final Tile.ViewHolder holder, int position) {

        final int pos = position;

        // TODO: Make this load image titles too
        Glide.with(context).load(images.get(pos).getUrl()).listener(new RequestListener<Uri, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
            {
                holder.progressBar.setVisibility(View.INVISIBLE);

                return false;
            }
        }).centerCrop().into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("USERPOST", images.get(pos).getUrl().toString());

                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("POST", images.get(pos));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.title.setText(images.get(pos).getTitle());

    }

    @Override
    public int getItemCount()
    {
        if (images == null)
        {
            return 0;
        }
        Log.d("ITEMIZED LIST", images.toString());
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title;
        public ImageView img;
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView)  itemView.findViewById(R.id.tile_text);
            img   = (ImageView) itemView.findViewById(R.id.tile_img);
            progressBar = (ProgressBar) itemView.findViewById(R.id.tile_progress_bar);
        }
    }
}
