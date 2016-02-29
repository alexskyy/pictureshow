package flikrshow.alexskyy.com.flickrshow.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.ItemClickedListener;
import com.raizlabs.universaladapter.converter.UniversalAdapter;
import com.raizlabs.universaladapter.converter.UniversalConverter;
import com.raizlabs.universaladapter.converter.UniversalConverterFactory;

import java.util.List;

import flikrshow.alexskyy.com.flickrshow.FlickrManager;
import flikrshow.alexskyy.com.flickrshow.R;
import flikrshow.alexskyy.com.flickrshow.adapters.MetadataHolder;
import flikrshow.alexskyy.com.flickrshow.adapters.MyPhotoAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MyListFragment extends android.app.Fragment {

    public static final String ARG_1 = "ARG1";
    private RecyclerView mRList;
    private MyPhotoAdapter mAdapter;
    private Handler mHandler;
    private Subscription _subscription;

    public static Fragment newInstance() {
        return newInstance("Cats");
    }

    public static Fragment newInstance(String searchKey) {
        Fragment fragment = new MyListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_1, searchKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_subscription != null) {
            _subscription.unsubscribe();
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        mHandler = new MyHandler(Looper.getMainLooper());

        mRList = (RecyclerView) view;
        mRList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new MyPhotoAdapter(getActivity());

        UniversalConverter converter = UniversalConverterFactory.create(mAdapter, mRList);
        converter.setItemClickedListener(
            new ItemClickedListener<MetadataHolder, ViewHolder>() {

                @Override
                public void onItemClicked(
                        UniversalAdapter<MetadataHolder, ViewHolder> adapter,
                        final MetadataHolder item,
                        ViewHolder holder,
                        final int position) {

                    if (holder instanceof MyPhotoAdapter.MyThumbHolder) {
                        getFullPhoto(item);
                    }
                }
            });

        if (args != null && args.containsKey(ARG_1)) {
            searchForPhotos((String)args.get(ARG_1));
        } else {
            searchForPhotos("Cats");
        }
    }

    private void getFullPhoto(final MetadataHolder holder) {
        // get full image.
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = FlickrManager.getImage(holder.getLargeURL());
                mAdapter.setPhoto(holder, bm);
            }
        });
        t.start();
    }

    public void searchForPhotos(final String s) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                FlickrManager.searchImagesByTag(mHandler, getActivity(), s);
//            }
//        }).start();
        _subscription = _getObservable(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(_getObserver());
    }

    private Observer<List<MetadataHolder>> _getObserver() {
        return new Observer<List<MetadataHolder>>() {
            @Override
            public void onCompleted() {
                Log.wtf("iii","onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.wtf("iii","onError: "+e.toString());
            }

            @Override
            public void onNext(List<MetadataHolder> metaData) {
                Log.wtf("iii","onNext");
                boolean firstThumb = true;
                for (MetadataHolder photo : metaData) {
                    Log.wtf("iii","onNext-"+photo.toString());
                    mAdapter.add(photo);
                    new FlickrManager.GetThumbnailsThread(mHandler, photo).start();
                    if ( firstThumb ){
                        firstThumb = false;
                        getFullPhoto(photo);
                    }
                }
            }
        };
    }

    private Observable<List<MetadataHolder>> _getObservable(final String searchTag) {
        return Observable.defer(new Func0<Observable<List<MetadataHolder>>>() {
            @Override
            public Observable<List<MetadataHolder>> call() {
                return Observable.just(getMetadata(searchTag));
            }
        });
    }

    private List<MetadataHolder> getMetadata(final String tag) {

        return FlickrManager.searchImagesByTag(getActivity(), tag);
    }

    public static final int META = 1;
    public static final int PHOTO = 2;
    public static final int REFRESH = 3;

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case META:
                    mAdapter.clear();
                    if (msg.obj != null) {
                        List<MetadataHolder> metaList = (List<MetadataHolder>) msg.obj;
                        boolean firstThumb = true;
                        for (MetadataHolder photo : metaList) {
                            mAdapter.add(photo);
                            new FlickrManager.GetThumbnailsThread(mHandler, photo).start();
                            if ( firstThumb ){
                                firstThumb = false;
                                getFullPhoto(photo);
                            }
                        }
                    }
                    break;
                case PHOTO:
                    break;
                case REFRESH:
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
            }
            super.handleMessage(msg);
        }
    }
    
    
}