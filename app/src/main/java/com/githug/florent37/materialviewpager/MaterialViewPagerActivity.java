package com.githug.florent37.materialviewpager;

import android.support.v7.widget.RecyclerView;

/**
 * Created by florentchampigny on 24/04/15.
 */
public interface MaterialViewPagerActivity {
    public void registerRecyclerView(final RecyclerView recyclerView, final RecyclerView.OnScrollListener onScrollListener);
}
