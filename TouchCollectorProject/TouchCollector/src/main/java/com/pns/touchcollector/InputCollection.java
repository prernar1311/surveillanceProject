package com.pns.touchcollector;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.*;

import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

class InputCollection extends Activity {
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
        mViewPager = (ViewPager) findViewById(R.id.pager);
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

    static abstract class DataStreamCollector<Event> implements DataCollector<List<Event>> {
        private final LinkedBlockingQueue<Event> q = new LinkedBlockingQueue<Event>();

        protected void registerEvent(Event d) {
            try {
                q.put(d);
            } catch (InterruptedException e) {
                try {
                    q.put(d);
                } catch (InterruptedException f) {
                    Log.e("DataStreamCollector",
                            "Interrupted while trying to enqueue event " + d.toString(),
                            e);
                }
            }
        }

        /** Flushes old data and returns in an ordered list. */
        public List<Event> getData() {
            List<Event> l = new ArrayList<Event>();
            q.drainTo(l);
            return l;
        }
    }

    static interface DataCollector <Data> {
        public void startRecording();
        public void stopRecording();
        public Data getData();
    }

    static class DataCollectorSessionManager {
        private SensorManager sManager;

        private AccessGyroscope aGyro;
        private AudioRecorder aRecorder;
        private AccessAccelerometer aAccel;

        private DataCollector[] collectors;

        List<SensorEvent> gyroEvents;
        List<SensorEvent> accelEvents;
        String recordingFilename;
        long startTime;


        public DataCollectorSessionManager(Context context) {
            sManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
            aGyro = new AccessGyroscope(sManager);
            aAccel = new AccessAccelerometer(sManager);

            collectors = array(aGyro, aAccel, aRecorder);
        }

        public void start() {
            startTime = System.currentTimeMillis() / 1000L;
            for (DataCollector c : collectors)
                c.startRecording();
        }

        public DataSession stopAndGetSession() {
            for (DataCollector c : collectors)
                c.stopRecording();

            gyroEvents = aGyro.getData();
            accelEvents = aAccel.getData();
            recordingFilename = aRecorder.getData();

            return new DataSession(gyroEvents, accelEvents, recordingFilename, startTime);
        }

        static class DataSession {
            private final List<SensorEvent> gyro;
            private final List<SensorEvent> accel;
            private final String recording;
            private final long startTime;

            private final JSONObject j;

            public DataSession(List<SensorEvent> Gyro, List<SensorEvent> Accel, String Mic,
                    long startingTimestamp) {
                gyro = Gyro;
                accel = Accel;
                recording = Mic;
                startTime = startingTimestamp;
                try {
                    j = buildSerializedEvents();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            public JSONObject serializedEvents() {
                return j;
            }

            private JSONObject buildSerializedEvents() throws JSONException {
                return new JSONObject()
                        .put("startTimestamp", startTime)
                        .put("gyro",          serializeSensors(gyro,  gyroToJSON))
                        .put("accelerometer", serializeSensors(accel, accelToJSON));
            }

            private static JSONObject serializeSensors(List<SensorEvent> le, EventJSONer converter)
                    throws JSONException {
                return new JSONObject()
                        .put("events", eventListToJSON(le, converter))
                        .put("name", le.size() > 0 ? le.get(0).sensor.getName() : "no_events");
            }

            EventJSONer accelToJSON = new EventJSONer() {
                public JSONObject toJSON(SensorEvent se) throws JSONException {
                    float[] vals = se.values;
                    return new JSONObject()
                        .put("accuracy", se.accuracy)
                        .put("timestamp", se.timestamp)
                        .put("x", vals[0])
                        .put("y", vals[1])
                        .put("z", vals[2]);
                }
            };

            EventJSONer gyroToJSON = accelToJSON;

            private static JSONArray eventListToJSON(List<SensorEvent> le, EventJSONer converter)
                    throws JSONException {
                JSONArray a = new JSONArray();
                for (SensorEvent e : le) {
                    a.put(converter.toJSON(e));
                }
                return a;
            }

            private interface EventJSONer {
                JSONObject toJSON(SensorEvent e) throws JSONException;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        TextInputFragment tif = new TextInputFragment();
        ButtonGridFragment bgf = new ButtonGridFragment();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 1) return new ButtonGridFragment();
            return new TextInputFragment();
        }

        /**
         * {@link Fragment} Superclass handling the data collection tasks common to both of our
         * views - microphone, gyroscope, accelerometer, anything else that may come up. Subclasses
         * should
         */
        //public class SurveilDataFragment extends Fragment {
        //    @Override
        //public View onCreateView(Layou)
        //}

        public class TextInputFragment extends Fragment {
            // Button Grid input view
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                return inflater.inflate(R.layout.fragment_keyboard_entry, container, false);
            }
        }

        public class ButtonGridFragment extends Fragment {
            // Button Grid input view
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
                // Inflate the layout for this fragment
                return inflater.inflate(R.layout.activity_button_grid_layout, container, false);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
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
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_input_collection, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /** Magic array literals */
    static  <T> T[] array(T... elems) {
        return elems;
    }
}
