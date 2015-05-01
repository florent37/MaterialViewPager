package com.github.florent37.materialviewpager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.R;

import java.util.List;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class RecyclerViewMaterialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int TYPE_PLACEHOLDER = Integer.MIN_VALUE;
    static final int PLACEHOLDER_SIZE = 1;
    private RecyclerView.Adapter mAdapter;

    public RecyclerViewMaterialAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_PLACEHOLDER;
            default:
                return mAdapter.getItemViewType(position-PLACEHOLDER_SIZE);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + PLACEHOLDER_SIZE;
    }

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