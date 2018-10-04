package com.github.florent37.materialviewpager;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;

public interface GlideImageLoadFailedCallback {

    boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource);

}
