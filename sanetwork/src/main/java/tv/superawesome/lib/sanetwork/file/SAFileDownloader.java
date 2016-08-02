package tv.superawesome.lib.sanetwork.file;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Set;

import tv.superawesome.lib.sanetwork.asynctask.*;

/**
 * Created by gabriel.coman on 19/04/16.
 */
public class SAFileDownloader {

    /** constants */
    private final String PREFERENCES = "MyPreferences";
    private final String SA_FOLDER = "/satmofolder";

    /** private variables */
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    /** the singleton SuperAwesome instance */
    private static SAFileDownloader instance = new SAFileDownloader();
    private SAFileDownloader () {}
    public static SAFileDownloader getInstance(){
        return instance;
    }

    public void setupDownloader(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor = preferences.edit();
        cleanup(context);
    }

    /**
     * Returns a key-enabled disk location
     * @return
     */
    public String getDiskLocation () {
        return "samov_" + new Random().nextInt(65548) + ".mp4";
    }

    /**
     * Function that downloads a file
     * @param videoUrl - the remote file ULR
     * @param filename - the simple file path of the file
     * @param listener - result listener
     */
    public void downloadFile(final String videoUrl, final String filename, final SAFileDownloaderInterface listener) {
        SAAsyncTask task = new SAAsyncTask(context, new SAAsyncTaskInterface() {
            @Override
            public Object taskToExecute() throws Exception {
                /** get the original SA unique key */
                if (filename == null) return null;
                String[] c1 = filename.split("_");
                if (c1.length < 2) return null;
                String key1 = c1[1];
                String[] c2 = key1.split(".mp4");
                if (c2.length < 1) return null;
                String key = c2[0];

                /** create streams */
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    /** start connection */
                    URL url = new URL(videoUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    int statusCode = connection.getResponseCode();

                    /** exception code != 200 */
                    if (statusCode != HttpURLConnection.HTTP_OK) return null;

                    /** get input stream and start writing to disk */
                    input = connection.getInputStream();
                    output = context.openFileOutput(filename, Context.MODE_PRIVATE);

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                    }

                    /** here file is written */
                    editor.putString(key, filename);
                    editor.apply();

                } catch (Exception e) {
                    /** no file has been written here */
                    e.printStackTrace();
                    return null;
                }

                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (IOException ignored) {
                    ignored.printStackTrace();
                }

                if (connection != null) connection.disconnect();

                /** if all goes well up until here, just return an empty object */
                return new Object();
            }

            @Override
            public void onFinish(Object result) {
                if (result != null) {
                    Log.d("SuperAwesome", "[Downloaded] " + videoUrl + " ==> " + filename);
                    listener.response(true);
                } else {
                    Log.d("SuperAwesome", "[Not Downloaded] " + videoUrl + " ==> " + filename);
                    listener.response(false);
                }
            }

            @Override
            public void onError() {
                listener.response(false);
            }
        });
    }

    /**
     * Cleanup function - it will remove files and reset preferences
     */
    private void cleanup(Context context) {
        Set<String> keys = preferences.getAll().keySet();
        for (String key : keys) {

            String filename = preferences.getString(key, null);
            if (filename != null) {
                String fullPath = context.getFilesDir() + "/" + filename;
                File file = new File(context.getFilesDir(), filename);
                boolean hasBeenDeleted = false;
                if (file.exists()) {
                    hasBeenDeleted = file.delete();
                }
                if (hasBeenDeleted) {
                    Log.d("SuperAwesome", "[Deleting] " + fullPath);
                } else {
                    Log.d("SuperAwesome", "[NOK-Deleting]" + fullPath);
                }
                editor.remove(key);
                editor.apply();
            }
        }
        editor.apply();
    }
}
