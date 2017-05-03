package com.austinhaskell.ghoster;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    private ArrayList<Uri>images;

    public Tile(Context context, ArrayList<Uri>images)
    {
        this.context = context;
        this.images = images;
    }

    @Override
    public Tile.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Tile.ViewHolder holder, int position) {

        final int pos = position;

        // TODO: Make this load image titles too
        Glide.with(context).load(images.get(pos)).centerCrop().into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("IMG_URI", images.get(pos));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount()
    {
        if (images == null)
        {
            return 0;
        }
        return images.size();
    }

    public void updateList(ArrayList<Uri> list)
    {
        images = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title;
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);

            // TODO: Add progress bar
            title = (TextView)  itemView.findViewById(R.id.tile_text);
            img   = (ImageView) itemView.findViewById(R.id.tile_img);
        }
    }
}
