package com.fofxlabs.phuc.inspereddit.async_tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by phuc on 10/15/2016.
 *
 * Since ImageLoader.displayImage() is asynchronous this AsyncTask will check the ImageView's tag to
 * make sure the current ImageView is the correct one. Without this the thumbnails will appear to be
 * duplicated and not match a given Post.
 */

public class ShowImagesTask extends AsyncTask<Void, Void, Void> {

    private final DisplayImageOptions DISPLAY_IMAGE_OPTIONS = new DisplayImageOptions.Builder()
            .cacheInMemory(false)
            .cacheOnDisk(false)
            .build();

    private Integer mTag;
    private String mImageUrl;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    public ShowImagesTask(String imageUrl, ImageView imageView, ProgressBar progressBar) {
        mImageUrl = imageUrl;
        mImageView = imageView;
        mProgressBar = progressBar;
        mTag = mImageView.getTag() == null ? null : (int) mImageView.getTag();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        // Update imageView only if it has the correct tag
        if (mTag == null || (int)mImageView.getTag() == mTag) {
            ImageLoader.getInstance().displayImage(mImageUrl, mImageView, DISPLAY_IMAGE_OPTIONS, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    mImageView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {}

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mImageView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {}
            });
        }
    }
}