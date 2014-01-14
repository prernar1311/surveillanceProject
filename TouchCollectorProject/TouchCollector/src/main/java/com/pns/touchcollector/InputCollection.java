package com.pns.touchcollector;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.json.JSONObject;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;

import com.pns.touchcollector.InputCollection.NamedJson;

import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.JSchException;


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


    static class NamedJson {
        JSONObject j;
        String name;
        public NamedJson(JSONObject j, String name) {
            this.j = j;
            this.name = name;
        }
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
            return (position == 0) ? new TextInputFragment() : new ButtonGridFragment();
        }

        public static class ButtonGridFragment extends TextInputFragment {
            protected static final int LAYOUT_ID = R.layout.numeric_input;
            protected static final int VIEW_ID = R.id.numeric_etkr;

            protected static int getLayoutId() {
                return R.layout.numeric_input;
            }
            protected static int getViewId() {   return R.id.numeric_etkr; }
        }

        public static class TextInputFragment extends Fragment {
            protected static final String LTAG = "TIFFragment";
            protected static final int LAYOUT_ID = R.layout.fragment_keyboard_entry;
            protected static final int VIEW_ID = R.id.fke_etkr;
            Context context;
            DataCollection dc;
            View view;

            public TextInputFragment() {
                super();
                Log.d(LTAG, ":constructor");
            }

            protected static int getLayoutId() {
                return R.layout.fragment_keyboard_entry;
            }
            protected static int getViewId() {   return R.id.fke_etkr; }

            @Override
            public void onAttach(Activity activity) {
                super.onAttach(activity);
                this.context = activity;

                if (context == null) {
                    Log.d(LTAG , ":onAttach : Attached to null activity.");
                    System.exit(1);
                } else
                    Log.d(LTAG, ":onAttach :" + context.toString());
            }

            // Button Grid input view
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
                super.onCreateView(inflater, container, savedInstanceState);

                Log.d(LTAG, ":oncreateview");

                // Inflate the layout for this fragment
                Log.i(LTAG, toString() + " inflating " + getLayoutId());
                view = inflater.inflate(getLayoutId(), container, false);

                if (context == null)
                    context = view.getContext();

                return this.view;
            }

            @Override
            public void onActivityCreated(Bundle sIS) {
                Log.d(LTAG, "hello from onactivitycreated 1");

                super.onActivityCreated(sIS);
                if (view == null) {
                    throw new IllegalStateException("View not available.");
                }
                EditTextKeyRegister etkr =
                        (EditTextKeyRegister) this.view.findViewById(getViewId());
                Log.d(LTAG, "onActivityCreated: post finding etkr 2");

                if (etkr == null) {
                        Log.d("DataCollector", "etkr not available");
                        // throw new IllegalStateException("etkr view not available.");
                }

                if (context == null)
                    context = getActivity();

                if (context == null)
                    context = view.getContext();

                if (context != null) {
                    Log.d(LTAG, "Woohoo! Starting data collection.");
                    dc = new DataCollection(context, etkr, Integer.toString(getLayoutId()));
                    dc.start();
                } else {
                    Log.d(LTAG, "Not starting DataCollection.");
                }
            }

            @Override
            public void onPause() {
                super.onPause();
                if (dc != null) {
                    Log.v(LTAG, "onPause: Getting recorded input data.");
                    JSONObject j = dc.stopAndGetSession();
                    new AsyncTask<NamedJson, Void, Void>() {
                       @Override
                       protected Void doInBackground(NamedJson... n) {
                           try {
                           // TODO make pkey and ip resources
                           Scp scp = new Scp("pkey", "sftp", "162.242.219.223");
                           scp.cd("/home/sftp/json");
                           String json = n[0].j.toString(2);
                           scp.put(new ByteArrayInputStream(json.getBytes("UTF-8")), n[0].name);}
                           catch (JSONException je) {
                               Log.e("AsyncJsonTask", "couldn't send json", je);
                           }
                           catch (SftpException se) {
                               Log.e("AsyncJsonTask", "couldn't send json", se);
                           }
                           catch (JSchException je) {
                               Log.e("AsyncJsonTask", "couldn't send json", je);
                           }
                           catch (UnsupportedEncodingException ee) {
                               Log.e("AsyncJsonTask", "couldn't send json", ee);
                           }
                           return null;
                       }
                    }.execute(new NamedJson(j, "datacollection")); // TODO determine names
                }
                else {
                    Log.v(LTAG, "onPause: Never got recorded input data.");
                }
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
