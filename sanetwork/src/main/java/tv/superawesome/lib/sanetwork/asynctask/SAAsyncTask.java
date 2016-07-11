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

// maiN async task class
public class SAAsyncTask {

    // constants
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    /**
     * Creates a new SAAsync Task
     */
    public SAAsyncTask(Context context, final SAAsyncTaskInterface listener) {

        //
        // Step 1: Try starting a new intent
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_SYNC, null, context, SAAsync.class);
        } catch (Exception e) {
            Log.d("SuperAwesome", "New intent for " + SAAsync.class + " could not be created");
            e.printStackTrace();
        }

        //
        // Step 2: Put extra data in intent
        if (intent != null) {

            // form the unique async task hash
            String hash = "asyncTask_" + new Random().nextInt(65548);

            // create a new persister
            SAAsyncTaskPersister persister = new SAAsyncTaskPersister();
            persister.listener = listener;

            // add perister to persister store singleton
            SAAsyncTaskPersisterStore.getInstance().persisterHashMap.put(hash, persister);

            intent.putExtra("hash", hash);
            intent.putExtra("receiver", SAAsyncTaskReceiver.factoryCreate(new Handler(), new SAAsyncTaskReceiverInterface() {
                @Override
                public void onReceiveResult(int resultCode, Bundle resultData) {

                    String hash = resultData.getString("hash");
                    SAAsyncTaskPersister persister = SAAsyncTaskPersisterStore.getInstance().persisterHashMap.get(hash);

                    switch (resultCode) {
                        case STATUS_RUNNING:
                            break;
                        case STATUS_FINISHED: {
                            persister.listener.onFinish(persister.result);
                            break;
                        }
                        case STATUS_ERROR: {
                            persister.listener.onError();
                            break;
                        }
                    }
                    SAAsyncTaskPersisterStore.getInstance().persisterHashMap.remove(hash);
                }
            }));
            context.startService(intent);
        }
    }

    // the actual intent service
    public static class SAAsync extends IntentService {

        public SAAsync() {
            super(SAAsync.class.getName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            ResultReceiver receiver = intent.getParcelableExtra("receiver");
            String hash = intent.getStringExtra("hash");
            SAAsyncTaskPersister persister = SAAsyncTaskPersisterStore.getInstance().persisterHashMap.get(hash);

            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {
                persister.result = persister.listener.taskToExecute();
                SAAsyncTaskPersisterStore.getInstance().persisterHashMap.put(hash, persister);
            } catch (Exception e) {
                persister.result = null;
                SAAsyncTaskPersisterStore.getInstance().persisterHashMap.put(hash, persister);
            }

            /** send results forward */
            Bundle bundle = new Bundle();
            
            bundle.putString("hash", hash);
            receiver.send(STATUS_FINISHED, bundle);
        }
    }
}

// persister object
class SAAsyncTaskPersister {

    public SAAsyncTaskInterface listener = null;
    public Object result = null;
}

// singleton persister - because f!
class SAAsyncTaskPersisterStore {

    // internal hash map
    public HashMap<String, SAAsyncTaskPersister> persisterHashMap = new HashMap<>();

    // private singleton constructor
    private SAAsyncTaskPersisterStore() {}

    // private singleton instance
    private final static SAAsyncTaskPersisterStore instance = new SAAsyncTaskPersisterStore();

    // singleton getter
    public static SAAsyncTaskPersisterStore getInstance() {
        return instance;
    }
}

@SuppressLint("ParcelCreator")
// standard receiver
class SAAsyncTaskReceiver extends ResultReceiver {

    // private listener
    private SAAsyncTaskReceiverInterface listener;

    // private constructor
    private SAAsyncTaskReceiver(Handler handler) {
        super(handler);
    }

    // factory create function
    public static SAAsyncTaskReceiver factoryCreate(Handler handler, SAAsyncTaskReceiverInterface listener) {
        SAAsyncTaskReceiver receiver = new SAAsyncTaskReceiver(handler);
        receiver.listener = listener;
        return receiver;
    }

    // <ResultReceiver> implementation
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (listener != null) {
            listener.onReceiveResult(resultCode, resultData);
        }
    }
}

// interface for the receiver
interface SAAsyncTaskReceiverInterface {
    void onReceiveResult(int resultCode, Bundle resultData);
}

