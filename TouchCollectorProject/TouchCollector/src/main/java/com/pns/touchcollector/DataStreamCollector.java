package com.pns.touchcollector;

import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
* Created by nicolascrowell on 2014/1/10.
*/
abstract class DataStreamCollector<Event> implements DataCollection.DataCollector<List<Event>> {
    private final LinkedBlockingQueue<Event> q = new LinkedBlockingQueue<Event>();
    protected void registerEvent(Event d) {
        try {
            q.put(d);
        } catch (InterruptedException e) {
            try {
                q.put(d);
            } catch (InterruptedException f) {
                Log.e("DataStreamCollector",
                        "Interrupted while trying to enqueue event " + d.toString(), e);
            }
        }
    }

    public abstract String getName();
    /** Used to serialize the events. */
    public abstract DataConverter<Event> getConverter();

    /** Flushes old data and returns in an ordered list. */
    public synchronized ArrayList<Event> getData() {
        ArrayList<Event> l = new ArrayList<Event>();
        q.drainTo(l);
        return l;
    }

    interface DataConverter <T> {
        JSONObject toJson(T t) throws JSONException;
    }

    public JSONObject getSerializedData() throws JSONException {
        List<Event> elist = getData();
        JSONArray a = new JSONArray();

        DataConverter<Event> converter = getConverter();
        for (Event e : elist) {
            a.put(converter.toJson(e));
        }

        return (new JSONObject()).put("events", a);
    }
}
