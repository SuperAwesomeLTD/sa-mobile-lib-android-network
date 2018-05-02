package superawesome.tv.sanetworktester;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import tv.superawesome.lib.sanetwork.file.SAFileDownloader;
import tv.superawesome.lib.sanetwork.file.SAFileDownloaderInterface;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SAFileDownloader.cleanup(this);

        SAFileDownloader downloader = new SAFileDownloader(this);

        String url = "https://sa-beta-ads-video-transcoded-superawesome.netdna-ssl.com/dkopqAGR8eYBV5KNQP7wH9UQniqbG4Ga-low.mp4";
        downloader.downloadFileFrom(url, new SAFileDownloaderInterface() {
            @Override
            public void saDidDownloadFile(boolean success, String key, String filePath) {

                Log.d("SuperAwesome", "Key " + key + " File " + filePath);

            }
        });

    }
}
