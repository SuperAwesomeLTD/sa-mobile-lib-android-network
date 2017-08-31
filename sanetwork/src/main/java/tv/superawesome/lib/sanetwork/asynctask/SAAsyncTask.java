/**
 * @Copyright:   SuperAwesome Trading Limited 2017
 * @Author:      Gabriel Coman (gabriel.coman@superawesome.tv)
 */
package tv.superawesome.lib.sanetwork.asynctask;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.HashMap;
import java.util.Random;

/**
 * This class abstracts away the complex things needed in order to run an async task on a
 * secondary thread in Android.
 * It works in tandem with the SAAsyncTaskInterface and uses the "listener" design patter to
 * transmit data to the main thread after the async task has finished.
 */
public class SAAsyncTask <T> {

    // constants
    private static final String INTENT_HASH_KEY = "hash";
    private static final String INTENT_RECEIVER_KEY = "receiver";
    private static final String TASK_KEY = "asyncTask_";
    private static final int STATUS_FINISHED = 1;

    // a static hash map where all the perister objects will be register while doing the
    // async operation (so as not to get lost)
    private static HashMap <String, SAAsyncTaskPersister > persisterHashMap = new HashMap<>();

    /**
     * Public AsyncTask constructor that takes a Context and a listener of type
     * SAASyncTaskInterface as parameters
     *
     * @param context   current context (either the activity or fragment)
     * @param listener  a listener used to send back messages to the main thread
     */
    public SAAsyncTask(Context context, final SAAsyncTaskInterface <T> listener) {

        //
        // Step 1: Try starting a new async type intent
        try {

            // form the unique async task hash
            String hash = TASK_KEY + new Random().nextInt(65548);

            // create a new persister
            SAAsyncTaskPersister <T> persister = new SAAsyncTaskPersister<>(listener);

            // add perister to persister store singleton
            persisterHashMap.put(hash, persister);

            // create the new intent
            Intent intent = new Intent(Intent.ACTION_SYNC, null, context, SAAsync.class);
            intent.putExtra(INTENT_HASH_KEY, hash);
            intent.putExtra(INTENT_RECEIVER_KEY, new ResultReceiver(new Handler()) {
                /**
                 * Overridden method for a generic ResultReceiver that allows the async task
                 * to return data to the main thread
                 *
                 * @param resultCode Current resultCode; only interesting type is STATUS_FINISHED
                 * @param resultData the accompanying result data, which should just contain a
                 *                   hash that's used to get the persister information
                 */
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {

                    if (resultData == null) {
                        Log.e("SuperAwesome", "[Fatal] Result Data for Async task is null!");
                        return;
                    }

                    // get the hash
                    String hash = resultData.getString(INTENT_HASH_KEY);

                    // check if exists, and not, that's a really big fatal error
                    if (hash == null) {
                        Log.e("SuperAwesome", "[Fatal] Hash for AsyncTask Receiver is null. Quitting intent!");
                        return;
                    }

                    // get existing persister (hopefully)
                    SAAsyncTaskPersister persister = persisterHashMap.get(hash);

                    // check the persister is there, and if it's not, that's a
                    // really big fatal error
                    if (persister == null) {
                        Log.e("SuperAwesome", "[Fatal] Persister for AsyncTask Receiver is null. Quitting intent!");
                        return;
                    }

                    // if the whole operation has finished, then do
                    if (resultCode == STATUS_FINISHED) {
                        if (persister.listener != null) {
                            if (persister.result != null) {
                                try {
                                    persister.listener.onFinish(persister.result);
                                } catch (Exception e) {
                                    // abc
                                }
                            } else {
                                persister.listener.onError();
                            }
                        }
                    }

                    // delete the sent persister object
                    persisterHashMap.remove(hash);

                }
            });

            // start the service
            context.startService(intent);

        }
        // just catch the exception and call the error method in the listener
        catch (Exception e) {
            if (listener != null) {
                listener.onError();
            }
        }
    }

    /**
     * Internal class that descends from IntentService.
     * This will have to be added to the Manifest file as a service, so that it can be launched
     * by the app (and thus perform the task on a secondary thread)
     */
    public static class SAAsync extends IntentService {

        /**
         * Public constructor
         */
        public SAAsync() {
            super(SAAsync.class.getName());
        }

        /**
         * Overridden method in which all the data processing should happen.
         * Since this is trying to be as generic as possible, the task to be executed is
         * specified by the listener implementation taken from the associated peristor object
         *
         * @param intent the current intent that has launched this IntentService
         */
        @Override
        protected void onHandleIntent(Intent intent) {
            if (intent == null) {
                Log.e("SuperAwesome", "[Fatal] Intent null for some reason! Quitting!");
                return;
            }

            // get receiver
            ResultReceiver receiver = intent.getParcelableExtra(INTENT_RECEIVER_KEY);

            // if this happens - then something **really** bad has occurred and just exit
            if (receiver == null) {
                Log.e("SuperAwesome", "[Fatal] Receiver for AsyncTask Intent is null. Quitting intent!");
                return;
            }

            // get hash
            String hash = intent.getStringExtra("hash");

            // check hash and receiver are OK
            if (hash == null) {
                Log.e("SuperAwesome", "[Fatal] Hash for AsyncTask Intent is null. Quitting intent!");
                return;
            }

            // get the perister from the persister store
            SAAsyncTaskPersister persister = persisterHashMap.get(hash);

            // check for
            if (persister == null) {
                Log.e("SuperAwesome", "[Fatal] Persister for AsyncTask Intent is null. Quitting intent!");
                return;
            }

            // This is where the actual magic happens.
            // If the code execution has reached this point it means there *is* a peristor
            // in the static hash map that corresponds to the intent and it has a listener
            // associated with it, that then will provide the result in the "taskToExecute"
            // method.
            try {
                if (persister.listener != null) {
                    persister.result = persister.listener.taskToExecute();
                }
            }catch (Exception ignored) {}

            // update data in persister store
            persisterHashMap.put(hash, persister);

            // send results forward
            Bundle bundle = new Bundle();
            bundle.putString(INTENT_HASH_KEY, hash);
            receiver.send(STATUS_FINISHED, bundle);
        }
    }
}

/**
 * This private class holds the result of the network operation as well as a reference to a
 * listener of type SAAsyncTaskInterface. Each new task that gets launched should basically
 * be coupled with it's own persister - meaning a different result and a different listener
 * that a later user can interact with.
 *
 * @param <T> Generic paramter T the class can respond to
 */
class SAAsyncTaskPersister <T> {

    // the listener associated with this perister
    SAAsyncTaskInterface <T> listener = null;

    // the generic result associated with this persister
    T result = null;

    /**
     * Shorthand constructor that takes a listener of type SAAsyncTaskInterface as paramter
     *
     * @param listener a valid, hopefully non-null listener
     */
    SAAsyncTaskPersister(SAAsyncTaskInterface <T> listener) {
        this.listener = listener;
    }
}


