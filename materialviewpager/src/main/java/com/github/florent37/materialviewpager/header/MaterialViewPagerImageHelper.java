package com.github.florent37.materialviewpager.header;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by florentchampigny on 12/06/15.
 */
public class MaterialViewPagerImageHelper {

    private static MaterialViewPager.OnImageLoadListener imageLoadListener;

    /**
     * change the image with a fade
     * @param urlImage
     * @param fadeDuration
     *
     * TODO : remove Picasso
     */
    public static void setImageUrl(final ImageView imageView, final String urlImage, final int fadeDuration) {
        final float alpha = ViewHelper.getAlpha(imageView);
        final ImageView viewToAnimate = imageView;

        //fade to alpha=0
        final ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewToAnimate, "alpha", 0).setDuration(fadeDuration);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                //change the image when alpha=0
                Picasso.with(imageView.getContext()).load(urlImage)
                        .centerCrop().fit().into(viewToAnimate, new Callback() {
                    @Override
                    public void onSuccess() {

                        //then fade to alpha=1
                        final ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewToAnimate, "alpha", alpha).setDuration(fadeDuration);
                        fadeIn.setInterpolator(new AccelerateInterpolator());
                        fadeIn.start();
                        if(imageLoadListener!=null){
                            imageLoadListener.OnImageLoad(imageView,((BitmapDrawable)imageView.getDrawable()).getBitmap());
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });
        fadeOut.start();
    }

    /**
     * change the image with a fade
     * @param drawable
     * @param fadeDuration
     */
    public static void setImageDrawable(final ImageView imageView, final Drawable drawable, final int fadeDuration) {
        final float alpha = ViewHelper.getAlpha(imageView);
        final ImageView viewToAnimate = imageView;

        //fade to alpha=0
        final ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewToAnimate, "alpha", 0).setDuration(fadeDuration);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //change the image when alpha=0

                imageView.setImageDrawable(drawable);

                //then fade to alpha=1
                final ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewToAnimate, "alpha", alpha).setDuration(fadeDuration);
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.start();
            }
        });
        fadeOut.start();
    }

    public static void setImageLoadListener(MaterialViewPager.OnImageLoadListener imageLoadListener) {
        MaterialViewPagerImageHelper.imageLoadListener = imageLoadListener;
    }
}

