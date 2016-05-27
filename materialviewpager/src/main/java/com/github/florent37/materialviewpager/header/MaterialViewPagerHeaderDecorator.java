package com.github.florent37.materialviewpager.header;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.github.florent37.materialviewpager.MaterialViewPagerAnimator;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.Utils;

/**
 * Created by florentchampigny on 27/05/2016.
 */
public class MaterialViewPagerHeaderDecorator extends RecyclerView.ItemDecoration {

    boolean registered = false;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        final RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
        final Context context = recyclerView.getContext();

        if(!registered) {
            MaterialViewPagerHelper.registerRecyclerView(context, recyclerView);
            registered = true;
        }

        int headerCells = 1;

        //don't work with stagged
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            GridLayoutManager gridLayoutManager = (GridLayoutManager)layoutManager;
            headerCells = gridLayoutManager.getSpanCount();
        }

        MaterialViewPagerAnimator animator = MaterialViewPagerHelper.getAnimator(context);
        if (animator != null) {
            if (holder.getAdapterPosition() < headerCells) {
                outRect.top = Math.round(Utils.dpToPx(animator.getHeaderHeight() + 10, context));
            }
        }
    }
}
