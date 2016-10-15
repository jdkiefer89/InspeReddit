package com.fofxlabs.phuc.inspereddit.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.fofxlabs.phuc.inspereddit.activites.MainActivity;
import com.fofxlabs.phuc.inspereddit.adapters.PostAdapter;
import com.fofxlabs.phuc.inspereddit.models.Post;

public class PostsContainerFragment extends Fragment {

    protected Context mContext;
    protected AdapterView.OnItemClickListener mOnItemClickListener;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected PostAdapter mPostAdapter;
    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener;

    public PostsContainerFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();

        mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Post.sPosts.clear();
                mPostAdapter.notifyDataSetChanged();

                ((MainActivity) getActivity()).getFrontPagePosts();
            }
        };

        mOnItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity) getActivity()).showImage(i);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    public void stopRefreshSpinner() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void loadPosts() {}
}
