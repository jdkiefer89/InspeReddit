package com.fofxlabs.phuc.inspereddit.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fofxlabs.phuc.inspereddit.R;
import com.fofxlabs.phuc.inspereddit.async_tasks.ShowImagesTask;
import com.fofxlabs.phuc.inspereddit.models.Post;
import com.fofxlabs.phuc.inspereddit.utils.CommonFunctions;
import com.fofxlabs.phuc.inspereddit.utils.Constants;

import java.util.ArrayList;

/**
 * Created by phuc on 10/11/2016.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    private Context mContext;
    private String mCollectionType;

    private class ViewHolder {
        RelativeLayout mRlItem;
        TextView mTvTitle;
        ImageView mIvThumbnail;
        ProgressBar mProgressBar;

        private void setRowBackgroundColor(int color) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mRlItem.setBackgroundColor(mContext.getResources().getColor(color, null));
            }
            else {
                mRlItem.setBackgroundColor(mContext.getResources().getColor(color));
            }
        }

        private void setTitleColor(int color) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mTvTitle.setTextColor(mContext.getResources().getColor(color, null));
            }
            else {
                mTvTitle.setTextColor(mContext.getResources().getColor(color));
            }
        }
    }

    public PostAdapter(Context context, ArrayList<Post> posts, String collectionType) {
        super(context, 0, posts);

        mContext = context;
        mCollectionType = collectionType;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post post = getItem(position);

        if (post != null) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                LayoutInflater inflater = LayoutInflater.from(mContext);
                switch (mCollectionType) {
                    case Constants.LIST:
                        convertView = inflater.inflate(R.layout.list_view_item, parent, false);
                        break;
                    case Constants.GRID:
                        convertView = inflater.inflate(R.layout.grid_view_item, parent, false);
                        break;
                }

                viewHolder.mRlItem = (RelativeLayout) convertView.findViewById(R.id.rlItem);
                viewHolder.mTvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                viewHolder.mIvThumbnail = (ImageView) convertView.findViewById(R.id.ivThumbnail);
                viewHolder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                CommonFunctions.setProgressBarColor(mContext, viewHolder.mProgressBar, R.color.orange);

                if (viewHolder.mTvTitle != null) {
                    viewHolder.setRowBackgroundColor(R.color.grey);
                    viewHolder.setTitleColor(R.color.white);
                    viewHolder.mTvTitle.setText(post.getTitle());
                }

                viewHolder.mIvThumbnail.setTag(position);

                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            new ShowImagesTask(post.getThumbnailUrl(), viewHolder.mIvThumbnail, viewHolder.mProgressBar).execute();
        }

        return convertView;
    }

}
