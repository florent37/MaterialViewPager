package com.github.florent37.materialviewpager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.github.florent37.materialviewpager.R;

/**
 * Created by florentchampigny on 24/04/15.
 */
@Deprecated
public class ListViewMaterialAdapter extends BaseAdapter {

    static final int TYPE_PLACEHOLDER = Integer.MIN_VALUE;
    static final int PLACEHOLDER_SIZE = 1;
    private BaseAdapter mAdapter;

    public ListViewMaterialAdapter(BaseAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getCount() {
        return mAdapter.getCount() + PLACEHOLDER_SIZE;
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position - PLACEHOLDER_SIZE);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position - PLACEHOLDER_SIZE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (position) {
            case 0:
                if (convertView == null)
                    convertView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.material_view_pager_placeholder, parent, false);
                return convertView;
            default:
                return mAdapter.getView(position - PLACEHOLDER_SIZE, convertView, parent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_PLACEHOLDER;
            default:
                return mAdapter.getItemViewType(position - PLACEHOLDER_SIZE);
        }
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount()+PLACEHOLDER_SIZE;
    }
}