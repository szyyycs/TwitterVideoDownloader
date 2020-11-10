package com.ycs.servicetest;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class MainIntentService extends IntentService {

    private static final String ACTION_FOO = "com.ycs.servicetest.action.FOO";
    private static final String ACTION_BAZ = "com.ycs.servicetest.action.BAZ";


    private static final String EXTRA_PARAM1 = "com.ycs.servicetest.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.ycs.servicetest.extra.PARAM2";

    public MainIntentService() {
        super("MainIntentService");
    }

    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MainIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }


    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MainIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }


    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void handleActionBaz(String param1, String param2) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
