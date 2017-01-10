package tv.superawesome.lib.sanetwork.asynctask;

import android.annotation.SuppressLint;
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
 *
 */
public class SAAsyncTask {

    // constants
    private static final int STATUS_FINISHED = 1;

    // constructor
    public SAAsyncTask(Context context, final SAAsyncTaskInterface listener) {

        //
        // Step 1: Try starting a new intent
        try {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, context, SAAsync.class);

            // form the unique async task hash
            String hash = "asyncTask_" + new Random().nextInt(65548);

            // create a new persister
            SAAsyncTaskPersister persister = new SAAsyncTaskPersister();
            persister.listener = listener;

            // add perister to persister store singleton
            SAAsyncTaskPersisterStore.getInstance().persisterHashMap.put(hash, persister);

            final SAAsyncTaskReceiver receiver = new SAAsyncTaskReceiver(new Handler());
            receiver.listener = new SAAsyncTaskReceiverInterface() {
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {

                    // get hash
                    String hash = resultData.getString("hash");

                    if (hash == null) {
                        Log.e("SuperAwesome", "[Fatal] Hash for AsyncTask Receiver is null. Quitting intent!");
                        return;
                    }

                    // get persister
                    SAAsyncTaskPersister persister = SAAsyncTaskPersisterStore.getInstance().persisterHashMap.get(hash);

                    if (persister == null) {
                        Log.e("SuperAwesome", "[Fatal] Persister for AsyncTask Receiver is null. Quitting intent!");
                        return;
                    }

                    // do or error
                    if (resultCode == STATUS_FINISHED) {
                        if (persister.listener != null) {
                            if (persister.result != null) {
                                persister.listener.onFinish(persister.result);
                            } else {
                                persister.listener.onError();
                            }
                        }
                    }

                    // delete the sent perister object
                    SAAsyncTaskPersisterStore.getInstance().persisterHashMap.remove(hash);
                }
            };

            intent.putExtra("hash", hash);
            intent.putExtra("receiver", receiver);
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
     *
     */
    public static class SAAsync extends IntentService {

        public SAAsync() {
            super(SAAsync.class.getName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            // get receiver
            ResultReceiver receiver = intent.getParcelableExtra("receiver");

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
            SAAsyncTaskPersister persister = SAAsyncTaskPersisterStore.getInstance().persisterHashMap.get(hash);

            // check for
            if (persister == null) {
                Log.e("SuperAwesome", "[Fatal] Persister for AsyncTask Intent is null. Quitting intent!");
                return;
            }

            // try to obtain result
            try {
                if (persister.listener != null) {
                    persister.result = persister.listener.taskToExecute();
                }
            }catch (Exception ignored) {}

            // update data in persister store
            SAAsyncTaskPersisterStore.getInstance().persisterHashMap.put(hash, persister);

            // send results forward
            Bundle bundle = new Bundle();
            bundle.putString("hash", hash);
            receiver.send(STATUS_FINISHED, bundle);
        }
    }
}

/**
 *
 */
class SAAsyncTaskPersister {

    SAAsyncTaskInterface listener = null;
    Object result = null;
}

/**
 *
 */
class SAAsyncTaskPersisterStore {

    // internal hash map
    HashMap<String, SAAsyncTaskPersister> persisterHashMap = new HashMap<>();

    // private singleton constructor
    private SAAsyncTaskPersisterStore() {}

    // private singleton instance
    private final static SAAsyncTaskPersisterStore instance = new SAAsyncTaskPersisterStore();

    // singleton getter
    static SAAsyncTaskPersisterStore getInstance() {
        return instance;
    }
}

/**
 *
 */
@SuppressLint("ParcelCreator")
class SAAsyncTaskReceiver extends ResultReceiver {

    // private listener
    SAAsyncTaskReceiverInterface listener;

    // private constructor
    SAAsyncTaskReceiver(Handler handler) {
        super(handler);
    }

    // <ResultReceiver> implementation
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (listener != null) {
            listener.onReceiveResult(resultCode, resultData);
        }
    }
}

/**
 *
 */
interface SAAsyncTaskReceiverInterface {
    void onReceiveResult(int resultCode, Bundle resultData);
}

