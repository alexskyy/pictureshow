package flikrshow.alexskyy.com.flickrshow.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.universaladapter.ListBasedAdapter;
import com.raizlabs.universaladapter.ViewHolder;

import flikrshow.alexskyy.com.flickrshow.R;

public class MyPhotoAdapter extends ListBasedAdapter<MetadataHolder, ViewHolder> {

    private static final long SLOW = 500;
    private static final long FASTER = 250;
    private Context mContext;

    private enum ViewHolderType {
        ThumbView,
        Photo
    }

    public MyPhotoAdapter(Context ctx) {
        super();
        mContext = ctx;
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        ViewHolder viewHolder = null;
        if (itemType == ViewHolderType.ThumbView.ordinal()) {
            viewHolder = new MyThumbHolder(inflateView(parent, R.layout.photo_thumb_layout));
        } else if (itemType == ViewHolderType.Photo.ordinal()) {
            viewHolder = new MyPhotoHolder(inflateView(parent, R.layout.photo_layout));
        }
        return viewHolder;
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, MetadataHolder s, int position) {
        if (viewHolder.getItemViewType() == ViewHolderType.ThumbView.ordinal()) {
            MyThumbHolder holder = (MyThumbHolder) viewHolder;
            holder.tv.setText(s.getTitle());
            holder.iv.setImageBitmap(s.getThumb());
            showAnimation(holder.iv, FASTER);
        } else if(viewHolder.getItemViewType() == ViewHolderType.Photo.ordinal()) {
            MyPhotoHolder holder = (MyPhotoHolder) viewHolder;
            holder.iv.setImageBitmap(s.getPhoto());
            showAnimation(holder.iv, SLOW);
        }
    }

    @Override
    public int getItemViewType(int position) {

        return get(position).isThumb() ? 0 : 1;
    }

    private void showAnimation(View viewToAnimate, long duration) {
        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
        animation.setDuration(duration);
        viewToAnimate.startAnimation(animation);
    }

    @Override
    public int getItemViewTypeCount() {
        return 2;
    }

    public class MyThumbHolder extends ViewHolder {

        public TextView tv;
        public ImageView iv;
        public MyThumbHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.title);
            iv = (ImageView) itemView.findViewById(R.id.photo);
        }
    }

    public class MyPhotoHolder extends ViewHolder {

        public ImageView iv;
        public MyPhotoHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView;
        }
    }

    public void setPhoto(MetadataHolder holder, Bitmap bm) {
        for(MetadataHolder oneHolder : getItemsList()) {
            if (holder.id.equals(oneHolder.id)) {
                oneHolder.setPhotoBitmap(bm);
                oneHolder.setThumbType(false);
            } else {
                oneHolder.setThumbType(true);
            }
        }
        notifyDataSetChangedOnUIThread();
    }
}
