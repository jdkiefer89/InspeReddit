package com.fofxlabs.phuc.inspereddit.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by phuc on 10/11/2016.
 */

public class Post {
    private String mTitle;
    private String mThumbnailUrl;
    private String mImageUrl;

    public static ArrayList<Post> sPosts = new ArrayList<>();

    public Post(String title, String thumbnailURL, String imageUrl) {
        mTitle = title;
        mThumbnailUrl = thumbnailURL;
        mImageUrl = imageUrl;
    }

    public static void getPostsFromJson(String jsonString) {
        sPosts = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray children = data.getJSONArray("children");
            for (int i = 0; i < children.length(); i++) {
                JSONObject childData = children.getJSONObject(i).getJSONObject("data");

                String title = childData.getString("title");
                String thumbnailUrl = childData.getString("thumbnail");
                String imageUrl = childData.getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject("source").getString("url");

                // Sometimes the image urls have "amp;" injected in for some strange reason. These are removed to get the correct url.
                thumbnailUrl = thumbnailUrl.replace("amp;", "");
                imageUrl = imageUrl.replace("amp;", "");

                Post post = new Post(title, thumbnailUrl, imageUrl);
                sPosts.add(post);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

}
