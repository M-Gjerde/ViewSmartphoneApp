package net.kaufmanndesigns.view;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

public class RequestQueue {
    private static final String TAG = "RequestQueue";

    private static RequestQueue instance;
    private com.android.volley.RequestQueue requestQueue;
    private static Context ctx;

    RequestQueue(Context context){
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public com.android.volley.RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
