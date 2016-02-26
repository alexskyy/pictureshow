package flikrshow.alexskyy.com.flickrshow.adapters;

import android.graphics.Bitmap;

import flikrshow.alexskyy.com.flickrshow.FlickrManager;

public class MetadataHolder {
    String id;
    String thumbURL;
    Bitmap thumb;
    Bitmap photo;
    String largeURL;
    String owner;
    String secret;
    String server;
    String farm;
    String title;
    private boolean isThumb = true;

    private static final String URL_TEMPLATE =
            "https://farm%s.staticflickr.com/%s/%s_%s";
    private static final String TO_STRING_TEMPL =
            "MetadataHolder [id=%s, thumbURL=%s largeURL=%s, owner=%s secret=%s, server=%s, farm=%s]";
    public MetadataHolder(
            String id, String owner, String secret, String server, String farm, String title) {
        super();
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        setThumbURL(createPhotoURL(FlickrManager.PHOTO_THUMB, this));
        setLargeURL(createPhotoURL(FlickrManager.PHOTO_LARGE, this));
        this.title = title;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public String getLargeURL() {
        return largeURL;
    }

    public void setLargeURL(String largeURL) {
        this.largeURL = largeURL;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_TEMPL, id, thumbURL, largeURL, owner, secret, server, farm);
    }

    private String createPhotoURL(int photoType, MetadataHolder imgCon) {
//        String t = "https://farm" + imgCon.farm + ".staticflickr.com/" + imgCon.server + "/" + imgCon.id + "_" + imgCon.secret;// +".jpg";
        String tmp = String.format(URL_TEMPLATE, imgCon.farm, imgCon.server, imgCon.id, imgCon.secret);
        switch (photoType) {
            case FlickrManager.PHOTO_THUMB:
                tmp += "_t";
                break;
            case FlickrManager.PHOTO_LARGE:
                tmp += "_z";
                break;

        }
        tmp += ".jpg";
        return tmp;
    }

    public String getTitle() {
        return title;
    }

    public void setPhotoBitmap(Bitmap b) {
        photo = b;
    }

    public void setThumbBitmap(Bitmap b) {
        thumb = b;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setThumbType(boolean thumb) {
        isThumb = thumb;
    }

    public boolean isThumb() {
        return isThumb;
    }
}
