package com.github.florent37.materialviewpager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.R;

/**
 * Created by florentchampigny on 24/04/15.
 * A RecyclerView.Adapter which inject a header to the actual RecyclerView.Adapter
 *
 * Please use now MaterialViewPagerDecorator, or with Stagged Layout Manager
 */
@Deprecated
public class RecyclerViewMaterialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //the constants value of the header view
    static final int TYPE_PLACEHOLDER = Integer.MIN_VALUE;

    //the size taken by the header
    private int mPlaceholderSize = 1;

    //the actual RecyclerView.Adapter
    private RecyclerView.Adapter mAdapter;

    /**
     * Construct the RecyclerViewMaterialAdapter, which inject a header into an actual RecyclerView.Adapter
     *
     * @param adapter The real RecyclerView.Adapter which displays content
     */
    public RecyclerViewMaterialAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;

        registerAdapterObserver();
    }

    /**
     * Construct the RecyclerViewMaterialAdapter, which inject a header into an actual RecyclerView.Adapter
     *
     * @param adapter         The real RecyclerView.Adapter which displays content
     * @param placeholderSize The number of placeholder items before real items, default is 1
     */
    public RecyclerViewMaterialAdapter(RecyclerView.Adapter adapter, int placeholderSize) {
        this.mAdapter = adapter;
        mPlaceholderSize = placeholderSize;

        registerAdapterObserver();
    }

    protected void registerAdapterObserver() {
        if(mAdapter != null) {
            this.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

                @Override
                public void onChanged() {
                    super.onChanged();

                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mPlaceholderSize)
            return TYPE_PLACEHOLDER;
        else
            return mAdapter.getItemViewType(position - mPlaceholderSize); //call getItemViewType on the adapter, less mPlaceholderSize
    }

    //dispatch getItemCount to the actual adapter, add mPlaceholderSize
    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + mPlaceholderSize;
    }

    //add the header on first position, else display the true adapter's cells
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_PLACEHOLDER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.material_view_pager_placeholder, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            default:
                return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    //dispatch onBindViewHolder on the actual mAdapter
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_PLACEHOLDER:
                break;
            default:
                mAdapter.onBindViewHolder(holder, position - mPlaceholderSize);
                break;
        }
    }

    public RecyclerView.Adapter getRecyclerAdapter() {
        return mAdapter;
    }

    public void mvp_notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    public void mvp_notifyItemChanged(int position) {
        mAdapter.notifyItemChanged(position-1);
        notifyItemChanged(position);
    }

    public void mvp_notifyItemInserted(int position) {
        mAdapter.notifyItemInserted(position - 1);
        notifyItemInserted(position);
    }

    public void mvp_notifyItemRemoved(int position) {
        mAdapter.notifyItemRemoved(position - 1);
        notifyItemRemoved(position);
    }

    public void mpv_notifyItemRangeChanged(int startPosition, int itemCount) {
        mAdapter.notifyItemRangeChanged(startPosition-1, itemCount-1);
        notifyItemRangeChanged(startPosition,itemCount);
    }

    public void mpv_notifyItemRangeInserted(int startPosition, int itemCount) {
        mAdapter.notifyItemRangeInserted(startPosition - 1, itemCount - 1);
        notifyItemRangeInserted(startPosition, itemCount);
    }

    public void mpv_notifyItemRangeRemoved(int startPosition, int itemCount) {
        mAdapter.notifyItemRangeRemoved(startPosition - 1, itemCount - 1);
        notifyItemRangeRemoved(startPosition, itemCount);
    }

}