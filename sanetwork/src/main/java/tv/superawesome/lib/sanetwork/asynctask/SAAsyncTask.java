package tv.superawesome.lib.sanetwork.asynctask;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import java.util.HashMap;
import java.util.Random;

// maiN async task class
public class SAAsyncTask {

    private SAAsyncTaskReceiver receiver;
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    /**
     * Creates a new SAAsync Task
     */
    public SAAsyncTask(Context context, final SAAsyncTaskInterface listener) {

        String hash = "asyncTask_" + new Random().nextInt(65548);
        SAAsyncTaskPersister persister = new SAAsyncTaskPersister();
        persister.listener = listener;
        SAAsyncTaskPersisterStore.getInstance().persisterHashMap.put(hash, persister);

        receiver = new SAAsyncTaskReceiver(new Handler());
        receiver.setReceiver(new SAAsyncTaskReceiverInterface() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {

                String hash = resultData.getString("hash");
                SAAsyncTaskPersister persister = SAAsyncTaskPersisterStore.getInstance().persisterHashMap.get(hash);

                switch (resultCode) {
                    case STATUS_RUNNING: break;
                    case STATUS_FINISHED: { persister.listener.onFinish(persister.result); break; }
                    case STATUS_ERROR: { persister.listener.onError(); break; }
                }
                SAAsyncTaskPersisterStore.getInstance().persisterHashMap.remove(hash);
            }
        });

        Intent intent = new Intent(Intent.ACTION_SYNC, null, context, SAAsync.class);
        intent.putExtra("receiver", receiver);
        intent.putExtra("hash", hash);
        context.startService(intent);
    }

    // the actual intent service
    public static class SAAsync extends IntentService {

        public SAAsync() {
            super(SAAsync.class.getName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            final ResultReceiver receiver = intent.getParcelableExtra("receiver");
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

@SuppressLint("ParcelCreator")
// standard receiver
class SAAsyncTaskReceiver extends ResultReceiver {

    private SAAsyncTaskReceiverInterface mReceiver;

    public SAAsyncTaskReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(SAAsyncTaskReceiverInterface receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}

// interface for the receiver
interface SAAsyncTaskReceiverInterface {
    void onReceiveResult(int resultCode, Bundle resultData);
}

// persister object
class SAAsyncTaskPersister {

    public SAAsyncTaskInterface listener = null;
    public Object result = null;
}

// singleton persister - because f!
class SAAsyncTaskPersisterStore {

    public HashMap<String, SAAsyncTaskPersister> persisterHashMap;

    private SAAsyncTaskPersisterStore() {
        persisterHashMap = new HashMap<>();
    }

    private final static SAAsyncTaskPersisterStore instance = new SAAsyncTaskPersisterStore();

    public static SAAsyncTaskPersisterStore getInstance() {
        return instance;
    }
}

