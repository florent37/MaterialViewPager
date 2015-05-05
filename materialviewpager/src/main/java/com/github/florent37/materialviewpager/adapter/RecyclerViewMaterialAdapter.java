package com.github.florent37.materialviewpager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.R;

import java.util.List;

/**
 * Created by florentchampigny on 24/04/15.
 * A RecyclerView.Adapter which inject a header to the actual RecyclerView.Adapter
 */
public class RecyclerViewMaterialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //the constants value of the header view
    static final int TYPE_PLACEHOLDER = Integer.MIN_VALUE;

    //the size taken by the header
    static final int PLACEHOLDER_SIZE = 1;

    //the actual RecyclerView.Adapter
    private RecyclerView.Adapter mAdapter;

    /**
     * Construct the RecyclerViewMaterialAdapter, which inject a header into an actual RecyclerView.Adapter
     * @param adapter The really RecyclerView.Adapter which display content
     */
    public RecyclerViewMaterialAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: //add the placeholder at the first position
                return TYPE_PLACEHOLDER;
            default:
                return mAdapter.getItemViewType(position-PLACEHOLDER_SIZE); //call getItemViewType on the adapter, less PLACEHOLDER_SIZE
        }
    }

    //displatch getItemCount to the actual adapter, add PLACEHOLDER_SIZE
    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + PLACEHOLDER_SIZE;
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
                return mAdapter.onCreateViewHolder(parent,viewType);
        }
    }

    //displatch onBindViewHolder on the actual mAdapter
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_PLACEHOLDER:
                break;
            default:
                mAdapter.onBindViewHolder(holder,position-PLACEHOLDER_SIZE);
                break;
        }
    }
}