package com.pauselabs.pause.core;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * This message bus allows you to post a message from any thread and it will get handled and then
 * posted to the main thread for you. See stack overflow http://stackoverflow.com/questions/15431768/how-to-send-event-from-service-to-activity-with-otto-event-bus
 */
public class PostFromAnyThreadBus extends Bus{

    private static final String TAG = PostFromAnyThreadBus.class.getSimpleName();

    public PostFromAnyThreadBus()
    {
        super(ThreadEnforcer.MAIN);
    }

    @Override
    public void post(final Object event)
    {
        if (Looper.myLooper() != Looper.getMainLooper())
        {
            // We're not in the main loop, so we need to get into it.
            (new Handler(Looper.getMainLooper())).post(new Runnable()
            {
                @Override
                public void run()
                {
                    // We're now in the main loop, we can post now
                    PostFromAnyThreadBus.super.post(event);
                }
            });
        }
        else
        {
            super.post(event);
        }
    }

    @Override
    public void unregister(final Object object)
    {
        //  Lots of edge cases with register/unregister that sometimes throw.
        try
        {
            super.unregister(object);
        }
        catch (IllegalArgumentException e)
        {
            // TODO: use Crashlytics unhandled exception logging
            Log.e(TAG, e.getMessage());
        }
    }
}
