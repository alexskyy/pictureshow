package flikrshow.alexskyy.com.flickrshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import flikrshow.alexskyy.com.flickrshow.adapters.MetadataHolder;

public class FlickrManager {

    // String to create Flickr API urls
    private static final String FLICKR_BASE_URL = "https://api.flickr.com/services/rest/?method=";
    private static final String FLICKR_PHOTOS_SEARCH_STRING = "flickr.photos.search";
    private static final String FLICKR_GET_SIZES_STRING = "flickr.photos.getSizes";
    private static final int FLICKR_PHOTOS_SEARCH_ID = 1;
    private static final int FLICKR_GET_SIZES_ID = 2;
    private static final int NUMBER_OF_PHOTOS = 20;

    //You can set here your API_KEY
    private static final String APIKEY_SEARCH_STRING = "&api_key=<your_key_here>";

    private static final String TAGS_STRING = "&tags=";
    private static final String PHOTO_ID_STRING = "&photo_id=";
    private static final String FORMAT_STRING = "&format=json";
    public static final int PHOTO_THUMB = 111;
    public static final int PHOTO_LARGE = 222;


    private static String createURL(int methodId, String parameter) {
        String method_type = "";
        String url = null;
        switch (methodId) {
            case FLICKR_PHOTOS_SEARCH_ID:
                method_type = FLICKR_PHOTOS_SEARCH_STRING;
                url = FLICKR_BASE_URL + method_type + APIKEY_SEARCH_STRING + TAGS_STRING + parameter + FORMAT_STRING + "&per_page=" + NUMBER_OF_PHOTOS + "&media=photos";
                break;
            case FLICKR_GET_SIZES_ID:
                method_type = FLICKR_GET_SIZES_STRING;
                url = FLICKR_BASE_URL + method_type + PHOTO_ID_STRING + parameter + APIKEY_SEARCH_STRING + FORMAT_STRING;
                break;
        }
        return url;
    }

    public static Bitmap getImage(String photoUrl) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(photoUrl);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (Exception e) {
            Log.e("FlickrManager", e.getMessage());
        }
        return bm;
    }

    private static Bitmap getThumbnail(MetadataHolder photoHolder) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(photoHolder.getThumbURL());
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (Exception e) {
            Log.e("FlickrManager", e.getMessage());
        }
        return bm;
    }

    public static class GetThumbnailsThread extends Thread {
        Handler uih;
        MetadataHolder photoHolder;

        public GetThumbnailsThread(Handler uih, MetadataHolder photoH) {
            this.uih = uih;
            this.photoHolder = photoH;
        }

        @Override
        public void run() {
            Bitmap b = getThumbnail(photoHolder);
            if (b != null) {
                photoHolder.setThumbBitmap(b);
                Message msg = Message.obtain(uih, 3);
                uih.sendMessage(msg);
            }
        }

    }

    public static List<MetadataHolder> searchImagesByTag(Context ctx, String tag) {
        String url = createURL(FLICKR_PHOTOS_SEARCH_ID, tag);
        List<MetadataHolder> tmp = new ArrayList<>();
        String jsonString = null;
        try {
            if (URLConnector.isOnline(ctx)) {
                ByteArrayOutputStream baos = URLConnector.readBytes(url);
                jsonString = baos.toString();
            }
            JSONObject root = new JSONObject(jsonString.replace("jsonFlickrApi(", "").replace(")", ""));
            JSONObject photos = root.getJSONObject("photos");
            JSONArray imageJSONArray = photos.getJSONArray("photo");
            for (int i = 0; i < imageJSONArray.length(); i++) {
                JSONObject item = imageJSONArray.getJSONObject(i);
                MetadataHolder imgCon = new MetadataHolder(item.getString("id"), item.getString("owner"), item.getString("secret"), item.getString("server"),
                        item.getString("farm"), item.getString("title"));
                tmp.add(imgCon);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return tmp;
    }

    public static List<MetadataHolder> searchImagesByTag(Handler uih, Context ctx, String tag) {
        List<MetadataHolder> tmp = searchImagesByTag(ctx, tag);

        Message msg = Message.obtain(uih, 1);
        msg.obj = tmp;
        uih.sendMessage(msg);

        return tmp;
    }
}