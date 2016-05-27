package com.github.florent37.materialviewpager.header;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.flaviofaria.kenburnsview.KenBurnsView;

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
        MaterialViewPagerImageHelper.setImageUrl(this, urlImage, fadeDuration);
    }

    /**
     * change the image with a fade
     *
     * @param drawable
     * @param fadeDuration
     */
    public void setImageDrawable(final Drawable drawable, final int fadeDuration) {
        MaterialViewPagerImageHelper.setImageDrawable(this, drawable, fadeDuration);
    }

}
