package com.austinhaskell.ghoster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Custom View to allow for circular images
 *
 * Created by Austin on 6/5/2017.
 */

public class CircleImageView extends android.support.v7.widget.AppCompatImageView
{
    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Override method where we can put our own drawing logic into <br />
     * this is where we can change the shape of the view by creating
     *
     * @param canvas Canvas of the ImageView
     */
    @Override
    public void onDraw(Canvas canvas)
    {

        // Get the drawable for this view
        Drawable drawable = getDrawable();

        // The drawable hasent been set up yet
        if (drawable == null)
        {
            // Stop
            return;
        }

        // Drawable has been set up but it doesn't
        // have one of the dimensions
        if (getWidth() == 0 || getHeight() == 0)
        {
            return;
        }

        // Turn the drawable into a bitmap that we can use
        Bitmap bitmap =
                ((BitmapDrawable)drawable)
                .getBitmap()
                .copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth();
        int h = getHeight();

        Bitmap circleBitmap = createBitmap(bitmap,w);
        canvas.drawBitmap(circleBitmap, 0, 0, null);
    }


    private Bitmap createBitmap(Bitmap bmp, int radius)
    {
        Bitmap retVal;
        Bitmap temp;

        temp = bmp;

        // We have the diameter, we need the radius
        radius = radius/2;

        int xOff = -(temp.getWidth()/2);
        int yOff = -(temp.getHeight()/2);

        retVal = Bitmap.createBitmap(temp.getWidth(), temp.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(retVal);

        final Paint paint = new Paint();
        final Rect  rect  = new Rect(xOff, yOff, temp.getWidth(), temp.getHeight());
        final Rect  orect = new Rect(rect);
        orect.offset(xOff+radius, yOff+radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(Color.parseColor("#BAB399"));

        canvas.drawCircle(radius, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(temp, rect, orect, paint);

        return retVal;
    }

}
















