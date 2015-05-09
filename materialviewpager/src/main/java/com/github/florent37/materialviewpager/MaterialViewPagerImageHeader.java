package com.github.florent37.materialviewpager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by florentchampigny on 29/04/15.
 * The MaterialViewPager animated Header
 * Using com.flaviofaria.kenburnsview.KenBurnsView
 * https://github.com/flavioarfaria/KenBurnsView
 */
public class MaterialViewPagerImageHeader extends KenBurnsView {

    //region construct

    public MaterialViewPagerImageHeader(Context context) {
        super(context);
    }

    public MaterialViewPagerImageHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialViewPagerImageHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //endregion

    /**
     * change the image with a fade
     *
     * @param urlImage
     * @param fadeDuration TODO : remove Picasso
     */
    public void setImageUrl(final String urlImage, final int fadeDuration) {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {

            final float alpha = getAlpha();
            final ImageView viewToAnimate = this;

            //fade to alpha=0
            final ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewToAnimate, "alpha", 0).setDuration(fadeDuration);
            fadeOut.setInterpolator(new DecelerateInterpolator());
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    //change the image when alpha=0
                    changeImage(urlImage, viewToAnimate, alpha, fadeDuration);
                }
            });
            fadeOut.start();
        }else{

            final float alpha = ViewHelper.getAlpha(this);
            final ImageView viewToAnimate = this;

            //fade to alpha=0
            final com.nineoldandroids.animation.ObjectAnimator fadeOut = com.nineoldandroids.animation.ObjectAnimator.ofFloat(viewToAnimate, "alpha", 0).setDuration(fadeDuration);
            fadeOut.setInterpolator(new DecelerateInterpolator());
            fadeOut.addListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                    super.onAnimationEnd(animation);

                    //change the image when alpha=0
                    changeImage(urlImage, viewToAnimate, alpha, fadeDuration);
                }
            });
            fadeOut.start();

        }
    }

    private void changeImage(final String urlImage, final ImageView viewToAnimate, final float alpha, final long fadeDuration) {
        Picasso.with(getContext()).load(urlImage)
                .centerCrop().fit().into(viewToAnimate, new Callback() {
            @Override
            public void onSuccess() {

                //then fade to alpha=1
                if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {
                    final ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewToAnimate, "alpha", alpha).setDuration(fadeDuration);
                    fadeIn.setInterpolator(new AccelerateInterpolator());
                    fadeIn.start();
                } else {
                    final com.nineoldandroids.animation.ObjectAnimator fadeIn = com.nineoldandroids.animation.ObjectAnimator.ofFloat(viewToAnimate, "alpha", alpha).setDuration(fadeDuration);
                    fadeIn.setInterpolator(new AccelerateInterpolator());
                    fadeIn.start();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    /**
     * change the image with a fade
     *
     * @param drawable
     * @param fadeDuration
     */
    public void setImageDrawable(final Drawable drawable, final int fadeDuration) {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.GINGERBREAD_MR1) {

            final float alpha = getAlpha();
            final ImageView viewToAnimate = this;

            //fade to alpha=0
            final ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewToAnimate, "alpha", 0).setDuration(fadeDuration);
            fadeOut.setInterpolator(new DecelerateInterpolator());
            fadeOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    //change the image when alpha=0

                    setImageDrawable(drawable);

                    //then fade to alpha=1
                    final ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewToAnimate, "alpha", alpha).setDuration(fadeDuration);
                    fadeIn.setInterpolator(new AccelerateInterpolator());
                    fadeIn.start();
                }
            });
            fadeOut.start();
        }else{
            final float alpha = getAlpha();
            final ImageView viewToAnimate = this;

            //fade to alpha=0
            final com.nineoldandroids.animation.ObjectAnimator fadeOut = com.nineoldandroids.animation.ObjectAnimator.ofFloat(viewToAnimate, "alpha", 0).setDuration(fadeDuration);
            fadeOut.setInterpolator(new DecelerateInterpolator());
            fadeOut.addListener(new com.nineoldandroids.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                    super.onAnimationEnd(animation);
                    //change the image when alpha=0

                    setImageDrawable(drawable);

                    //then fade to alpha=1
                    final com.nineoldandroids.animation.ObjectAnimator fadeIn = com.nineoldandroids.animation.ObjectAnimator.ofFloat(viewToAnimate, "alpha", alpha).setDuration(fadeDuration);
                    fadeIn.setInterpolator(new AccelerateInterpolator());
                    fadeIn.start();
                }
            });
            fadeOut.start();

        }
    }

}
