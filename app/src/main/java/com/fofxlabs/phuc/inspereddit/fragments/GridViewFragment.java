package com.fofxlabs.phuc.inspereddit.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.fofxlabs.phuc.inspereddit.R;
import com.fofxlabs.phuc.inspereddit.adapters.PostAdapter;
import com.fofxlabs.phuc.inspereddit.models.Post;
import com.fofxlabs.phuc.inspereddit.utils.Constants;

public class GridViewFragment extends PostsContainerFragment {

    private GridView mGridView;

    public GridViewFragment() {}

    public static GridViewFragment newInstance() {
        return new GridViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid_view, container, false);

        mGridView = (GridView) view.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(mOnItemClickListener);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        return view;
    }

    @Override
    public void loadPosts() {
        super.loadPosts();
        mPostAdapter = new PostAdapter(mContext, Post.sPosts, Constants.GRID);
        mGridView.setAdapter(mPostAdapter);
    }
}
