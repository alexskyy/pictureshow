package flikrshow.alexskyy.com.flickrshow;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.RadioButton;

import flikrshow.alexskyy.com.flickrshow.fragments.MyListFragment;

public class ListActivity extends Activity {

    private static final String FRAG_TAG = "_frag_tag";
    private FloatingActionButton mFab;
    private SearchCat mSearchKey = SearchCat.Sunrise;

    private enum SearchCat {
        Sunset("Sunset"),
        Sunrise("Sunrise");

        private final String category;

        SearchCat(String cat) {
            category = cat;
        }


        @Override
        public String toString() {
            return category;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.container_layout);

        mFab = (FloatingActionButton) findViewById(R.id.fab_search);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Search for", Snackbar.LENGTH_LONG)
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                mFab.setVisibility(View.VISIBLE);
                                SearchCat oldSearch = mSearchKey;
                                RadioButton rb2 = (RadioButton) snackbar.getView().findViewById(R.id.rb_sunset_action);
                                mSearchKey = rb2.isChecked() ? SearchCat.Sunset : SearchCat.Sunrise;
                                if (!oldSearch.equals(mSearchKey)) kickoffSearch(mSearchKey);
                            }

                            @Override
                            public void onShown(Snackbar snackbar) {
                                super.onShown(snackbar);
                                mFab.setVisibility(View.GONE);
                                RadioButton rb2;
                                if (SearchCat.Sunset.equals(mSearchKey)) {
                                    rb2 = (RadioButton) snackbar.getView().findViewById(R.id.rb_sunset_action);
                                } else {
                                    rb2 = (RadioButton) snackbar.getView().findViewById(R.id.rb_sunrise_action);
                                }
                                rb2.setChecked(true);
                            }
                        }).show();
            }
        });
        getFragmentManager()
                .beginTransaction()
                .add(R.id.container_id, MyListFragment.newInstance(mSearchKey.toString()), FRAG_TAG)
                .commit();
    }

    private void kickoffSearch(SearchCat mSearchKey) {
        Fragment frag = getFragmentManager().findFragmentByTag(FRAG_TAG);
        if (frag != null) {
            MyListFragment listFrag = (MyListFragment) frag;
            listFrag.searchForPhotos(mSearchKey.toString());
        }
    }
}
