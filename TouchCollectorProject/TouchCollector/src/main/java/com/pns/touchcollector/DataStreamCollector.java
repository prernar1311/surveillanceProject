package com.pns.touchcollector;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

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

    /** Flushes old data and returns in an ordered list. */
    public List<Event> getData() {
        List<Event> l = new ArrayList<Event>();
        q.drainTo(l);
        return l;
    }
}
