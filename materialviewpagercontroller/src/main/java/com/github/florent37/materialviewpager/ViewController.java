package com.github.florent37.materialviewpager;

import android.app.Activity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.carpaccio.Carpaccio;
import com.github.florent37.carpaccio.CarpaccioHelper;
import com.github.florent37.carpaccio.controllers.CommonViewController;
import com.github.florent37.carpaccio.controllers.adapter.CarpaccioRecyclerViewAdapter;
import com.github.florent37.carpaccio.controllers.adapter.Holder;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;

import static com.github.florent37.carpaccio.controllers.ControllerHelper.getLayoutIdentifierFromString;

/**
 * Created by florentchampigny on 04/08/15.
 */
public class ViewController {

    protected int recyclerColumnCount = 1;

    public void materialColumns(View view, int number) {
        if (view instanceof RecyclerView) {
            recyclerColumnCount = number;

            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), number));
        }
    }

    public void materialAdapter(View view, String mappedName, String layoutName) {
        final int layoutResId = getLayoutIdentifierFromString(view.getContext(), layoutName);
        final Carpaccio carpaccio = CarpaccioHelper.findParentCarpaccio(view);
        if (carpaccio != null && layoutResId != -1 && view instanceof RecyclerView) {
            CommonViewController commonViewController = new CommonViewController();

            MaterialCarpaccioRecyclerViewAdapter adapter = new MaterialCarpaccioRecyclerViewAdapter(recyclerColumnCount, carpaccio, layoutResId, mappedName);

            commonViewController.setAdapterForRecyclerView(view, mappedName, layoutName, adapter);

            MaterialViewPagerHelper.registerRecyclerView((Activity) view.getContext(), (RecyclerView) view);
        }
    }

    public class MaterialCarpaccioRecyclerViewAdapter extends CarpaccioRecyclerViewAdapter {

        //the constants value of the header view
        static final int TYPE_PLACEHOLDER = Integer.MIN_VALUE;

        //the size taken by the header
        protected int mPlaceholderSize = 1;

        public MaterialCarpaccioRecyclerViewAdapter(int mPlaceholderSize, Carpaccio carpaccio, int layoutResId, String mappedName) {
            super(carpaccio, layoutResId, mappedName);
            this.mPlaceholderSize = mPlaceholderSize;
        }

        public int getItemViewType(int position) {
            if (position < mPlaceholderSize)
                return TYPE_PLACEHOLDER;
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            int itemCount = super.getItemCount();
            if( itemCount > 0)
                return itemCount+mPlaceholderSize;
            else
                return 0;
        }

        @Override
        public Object getItemForRow(View view, int position) {
            if(position >= mPlaceholderSize) {
                return super.getItemForRow(view, position - mPlaceholderSize);
            }else
                return null;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_PLACEHOLDER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_view_pager_placeholder, parent, false);
                return new Holder(view);
            } else
                return super.onCreateViewHolder(parent, viewType);
        }
    }

}
