package com.pns.touchcollector;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pns.touchcollector.DataCollection.DataSession;

public class InputCollection extends Activity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_collection);

        // Create the adapter that will return a fragment for each of the three (?)
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.aic_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.input_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return (position == 1) ? new ButtonGridFragment() : new TextInputFragment();
        }

        public static class ButtonGridFragment extends TextInputFragment {
            protected static final int layout_id = R.layout.numeric_input;
        }

        public static class TextInputFragment extends Fragment {
            protected static final int layout_id = R.layout.fragment_keyboard_entry;
            Activity activity;
            DataCollection dc;
            View view;

            public TextInputFragment() { super(); }

            @Override
            public void onAttach(Activity activity) {
                super.onAttach(activity);
                this.activity = activity;
            }

            // Button Grid input view
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
                super.onCreateView(inflater, container, savedInstanceState);

                // Inflate the layout for this fragment
                this.view = inflater.inflate(R.layout.fragment_keyboard_entry, container, false);
                return this.view;
            }

            @Override
            public void onActivityCreated(Bundle sIS) {
                super.onActivityCreated(sIS);
                if (view == null) {
                    throw new IllegalStateException("View not available.");
                }
                EditTextKeyRegister etkr = ((EditTextKeyRegister)
                        this.view.findViewById(R.id.numeric_editText));
                if (this.activity == null) {
                    throw new IllegalStateException("Activity not available.");
                }
                if (etkr == null) {
                    throw new IllegalStateException("Activity not available.");
                }
                dc = new DataCollection(this.activity, etkr);
                dc.start();
            }

            @Override
            public void onPause() {
                super.onPause();
                DataSession ds = dc.stopAndGetSession();
            }
        }

        /*public static class ButtonGridFragment extends Fragment {
            public ButtonGridFragment() { super(); }

            // Button Grid input view
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
                //((EditTextKeyRegister) findViewById(R.layout.numeric_editText))
                //        .setKeyImeListener(this);

                // Inflate the layout for this fragment
                //return inflater.inflate(R.layout.activity_button_grid_layout, container, false);
                return inflater.inflate(R.layout.numeric_input, container, false);
            }

            @Override
            public void onPause() {
                super.onPause();}
        }*/

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Title";
            /* Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;*/
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
            super();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_input_collection, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.fic_section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

}
